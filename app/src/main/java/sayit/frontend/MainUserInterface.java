package sayit.frontend;

import sayit.common.qa.InputOutputEntry;
import sayit.frontend.helpers.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.Map;

import static sayit.frontend.FrontEndConstants.*;

/**
 * The main user interface for the application.
 */
public class MainUserInterface {
    private String currentUsername;

    private JButton startButton;

    private JPanel scrollBar;

    private JTextArea questionTextArea;

    private JTextArea answerTextArea;

    // tracks the last selected button from sidebar (for deletion)
    private QuestionButton selectedButton;

    private final JFrame frame;

    private AudioRecorder recorder;

    private MainUserInterface(String username) {
        if (username != null) {
            this.currentUsername = username;
        }

        this.frame = new JFrame(APP_TITLE);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addComponentsToPane(this.frame.getContentPane());
        this.frame.pack();
        this.frame.setVisible(true);
        this.recorder = null;

        // Add behavior for closing app, update db.
        // https://stackoverflow.com/questions/9093448/how-to-capture-a-jframes-close-button-click-event
        // https://www.codejava.net/java-se/swing/preventing-jframe-window-from-closing
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirmClose = JOptionPane.showConfirmDialog(frame, CLOSE_WINDOW_TEXT, CLOSE_WINDOW_TITLE,
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                // if user confirms closing the window, save and exit
                if (confirmClose == JOptionPane.YES_OPTION) {
                    // if there is an audio recording, delete it
                    if (MainUserInterface.this.recorder != null) {
                        File audioFile = MainUserInterface.this.recorder.getRecordingFile();
                        if (!audioFile.delete()) {
                            System.err.println("Failed to delete audio file: " + audioFile.getAbsolutePath());
                        }
                    }

                    // terminate Java VM and exit
                    System.exit(0);
                }
            }
        });
    }

    private static MainUserInterface userInterface;

    /**
     * <p>
     * Gets or creates a new instance of the <c>MainUserInterface</c> class. This
     * method is designed so that there can be at most one instance of the
     * <c>MainUserInterface</c> class at any point.
     * </p>
     *
     * <p>
     * This will also automatically make the user interface visible if it hasn't
     * been initialized.
     * </p>
     *
     * @return The instance of the <c>MainUserInterface</c> class.
     */
    public static MainUserInterface getInstance() {
        if (userInterface == null) {
            userInterface = new MainUserInterface(null);
        }

        return userInterface;
    }

    /**
     * Gets or creates a new instance of the <c>MainUserInterface</c> class. Note that
     * you should use this method if you want to specify a username.
     *
     * @param username The username to use.
     * @return The instance of the <c>MainUserInterface</c> class.
     */
    public static MainUserInterface createInstance(String username) {
        if (userInterface == null) {
            userInterface = new MainUserInterface(username);
        }
        else {
            userInterface.setUser(username);
        }

        return userInterface;
    }

    /**
     * Display entry in the text boxes
     *
     * @param entry The entry to display.
     */
    public void displayEntry(InputOutputEntry entry) {
        questionTextArea.setText(QUESTION_HEADER_TEXT + entry.getInput().getInputText().trim());
        answerTextArea.setText(ANSWER_HEADER_TEXT + entry.getOutput().getOutputText().trim());
    }

    /**
     * Adds the specified components to this user interface.
     *
     * @param pane The pane to add the components to.
     */
    public void addComponentsToPane(Container pane) {
        if (!RequestSender.getInstance().isAlive()) {
            JOptionPane.showMessageDialog(this.frame, APP_CANNOT_RUN_TEXT, ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        Map<Long, InputOutputEntry> inputHistory;
        try {
            inputHistory = RequestSender.getInstance().getHistory(this.currentUsername);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.frame, e.getMessage(), ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startButton = new JButton("START");
        toolBar.add(startButton);
        startButton.addActionListener(MainUiEventHandlers.onStartButtonPress(this));
        pane.add(toolBar, BorderLayout.PAGE_START);

        scrollBar = new JPanel(new GridLayout(0, 1)); // USE THIS FOR APP
        JScrollPane scrollPane = new JScrollPane(scrollBar);
        // scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(200, 500));
        pane.add(scrollPane, BorderLayout.LINE_START);

        // Create the "Question" JTextArea and JScrollPane
        questionTextArea = new JTextArea();
        questionTextArea.setLineWrap(true);
        questionTextArea.setEditable(false);
        questionTextArea.setText(QUESTION_HEADER_TEXT);

        JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
        questionScrollPane.setPreferredSize(new Dimension(380, 240));

        // Create the "Answer" JTextArea and JScrollPane
        answerTextArea = new JTextArea();
        answerTextArea.setLineWrap(true);
        answerTextArea.setEditable(false);
        answerTextArea.setText(ANSWER_HEADER_TEXT);

        // Create the "Question" JTextArea and JScrollPane
        JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
        answerScrollPane.setPreferredSize(new Dimension(380, 240));

        // load entries onto scrollBar

        for (Map.Entry<Long, InputOutputEntry> entry : inputHistory.entrySet()) {
            String question = entry.getValue().getInput().getInputText();
            QuestionButton button = new QuestionButton(question, entry.getKey());
            button.setPreferredSize(PROMPT_HISTORY_BTN_DIMENSIONS);
            button.addActionListener(MainUiEventHandlers.onQaButtonPress(this, entry.getValue(), button));
            scrollBar.add(button);
        }

        // Create panel for each JTextArea and add Panel to the Main Pane
        JPanel content = new JPanel();
        content.add(questionScrollPane);
        content.add(answerScrollPane);
        content.setPreferredSize(new Dimension(500, 500));
        pane.add(content, BorderLayout.CENTER);
    }

    /**
     * Gets the scroll bar panel.
     *
     * @return The scroll bar panel.
     */
    public JPanel getScrollBar() {
        return scrollBar;
    }

    /**
     * Gets the question text area.
     *
     * @return The question text area.
     */
    public JTextArea getQuestionTextArea() {
        return questionTextArea;
    }

    /**
     * Gets the answer text area.
     *
     * @return The answer text area.
     */
    public JTextArea getAnswerTextArea() {
        return answerTextArea;
    }

    /**
     * Gets the button associated with the question that is currently being
     * displayed.
     *
     * @return The button associated with the question that is currently being
     * displayed.
     */
    public QuestionButton getSelectedButton() {
        return selectedButton;
    }

    /**
     * Gets the Start button
     *
     * @return The Start button
     */
    public JButton getStartButton() {
        return startButton;
    }

    /**
     * Sets the button that was last selected in the sidebar.
     *
     * @param selectedButton The button that was last selected.
     */
    public void setSelectedButton(QuestionButton selectedButton) {
        this.selectedButton = selectedButton;
    }

    /**
     * Gets the frame.
     *
     * @return The frame.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Gets the recorder instance.
     *
     * @param recorder The recorder instance.
     */
    public void setRecorder(AudioRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     * Gets the recorder object. This will be <c>null</c> if the user is not
     * recording.
     *
     * @return The recorder object.
     */
    public AudioRecorder getRecorder() {
        return recorder;
    }

    /**
     * Sets the user field.
     *
     * @param user The username that's currently logged in.
     */
    public void setUser(String user) {
        this.currentUsername = user;
    }

    /**
     * Gets the user field.
     *
     * @return The username that's currently logged in.
     */
    public String getUser() {
        return this.currentUsername;
    }
}

/**
 * Button class for questions on sidebar
 */
class QuestionButton extends JButton {
    private final long id;

    /**
     * Creates a <c>QuestionButton</c> object with the displayName.
     *
     * @param displayName The text to be displayed on the button.
     * @param id          The ID of the corresponding QuestionAnswerEntry in the
     *                    database
     */
    public QuestionButton(String displayName, long id) {
        super(displayName);
        this.setPreferredSize(PROMPT_HISTORY_BTN_DIMENSIONS);
        this.id = id;
    }

    /**
     * Public getter method for the ID
     *
     * @return The ID.
     */
    public long getId() {
        return this.id;
    }
}

/**
 * Represents a round button.
 */
class RoundButton extends JButton {
    private Shape shape;

    /**
     * Creates a <c>RoundButton</c> object with the specified image and size.
     *
     * @param fileName The file containing the image.
     * @param size     The size of the button.
     */
    public RoundButton(String fileName, int size) {
        super.setIcon(ImageHelper.getImageIcon(fileName, size));

        setBackground(Color.lightGray);
        setFocusable(false);
        Dimension size1 = getPreferredSize();
        size1.width = size1.height = 50;
        setPreferredSize(size1);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.gray);
        } else {
            g.setColor(getBackground());
        }

        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.darkGray);
        g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }
}
