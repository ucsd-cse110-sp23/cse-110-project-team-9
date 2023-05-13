package sayit;

import sayit.helpers.ImageHelper;

import sayit.openai.ChatGpt;
import sayit.openai.IWhisper;
import sayit.openai.Whisper;
import sayit.openai.WhisperCheck;

import sayit.qa.Answer;
import sayit.qa.Question;
import sayit.qa.QuestionAnswerEntry;

import sayit.storage.TsvStore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.Map;

import javax.swing.*;

public class MainUserInterface {
    // Constants for filenames
    private static final String TRASHCAN_FILENAME = "./Pictures/TrashCan.jpg";
    private static final String RECORD_BUTTON_FILENAME = "./Pictures/RecordButton.jpg";
    private static final String STOP_BUTTON_FILENAME = "./Pictures/StopButton.jpg";
    private static final String DATA_FILENAME = "./data.tsv";

    // Constants for headers and titles
    private static final String APP_TITLE = "SayIt Assistant";
    private static final String QUESTION_HEADER_TEXT = "Question: \n\n";
    private static final String ANSWER_HEADER_TEXT = "ChatGPT Response: \n\n";
    private static final String CLEAR_ALL_BUTTON_TITLE = "Clear All";
    private static final String CLOSE_WINDOW_TEXT = "Are you sure you want to close this window?";
    private static final String CLOSE_WINDOW_TITLE = "Close Window?";

    // Constants for success and error messages
    private static final String DELETION_SUCCESS_TEXT = "Deleted question";
    private static final String DELETION_NONE_SELECTED_TEXT = "No question selected";
    private static final String DELETION_ERROR_TEXT = "Unable to delete recording file.";
    private static final String TRANSCRIPTION_ERROR_TEXT = "Unable to transcribe response. ";
    private static final String ERROR_TEXT = "Error";

    private JButton recordButton;
    private JPanel scrollBar;
    private JTextArea questionTextArea;
    private JTextArea answerTextArea;
    private JButton deleteButton;
    private QuestionButton selectedButton; // tracks the last selected button from sidebar (for deletion)
    private JScrollPane answerScrollPane;
    private JScrollPane questionScrollPane;
    private final JFrame frame;
    private static AudioRecorder recorder;
    private TsvStore db;
    private int currentQID;

    private MainUserInterface() {
        this.frame = new JFrame(APP_TITLE);
        db = TsvStore.createOrOpenStore(DATA_FILENAME);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addComponentsToPane(this.frame.getContentPane());
        this.frame.pack();
        this.frame.setVisible(true);
        MainUserInterface.recorder = null;

        // add behavior for closing app
        // update db
        // code taken from
        // https://stackoverflow.com/questions/9093448/how-to-capture-a-jframes-close-button-click-event
        // https://www.codejava.net/java-se/swing/preventing-jframe-window-from-closing
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirmClose = JOptionPane.showConfirmDialog(frame,
                CLOSE_WINDOW_TEXT, CLOSE_WINDOW_TITLE,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

                // if user confirms closing the window, save and exit
                if (confirmClose == JOptionPane.YES_OPTION){
                    // save questions and answers to database
                    db.save();

                    // if there is an audio recording, delete it
                    if(MainUserInterface.recorder != null){
                        File audioFile = MainUserInterface.recorder.getRecordingFile();
                        audioFile.delete();
                    }

                    // terminate Java VM and exit
                    System.exit(0);
                }

                // otherwise, close dialog and keep window open
            }
        });
    }

    private static MainUserInterface userInterface;

    /**
     * <p>
     * Gets or creates a new instance of the <c>MainUserInterface</c> class. This
     * method is
     * designed so that there can be at most one instance of the
     * <c>MainUserInterface</c> class
     * at any point.
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
            userInterface = new MainUserInterface();
        }

        return userInterface;
    }

    /**
     * Display entry in the text boxes
     * 
     * @param entry
     */
    public void displayEntry(QuestionAnswerEntry entry) {
        questionTextArea.setText(QUESTION_HEADER_TEXT + entry.getQuestion().getQuestionText().trim());
        answerTextArea.setText(ANSWER_HEADER_TEXT + entry.getAnswer().getAnswerText().trim());
    }

    /**
     * A method that should run when the recording button is pressed.
     *
     * @param e The event arguments.
     */
    private void onRecordButtonPress(ActionEvent e) {
        // If we're not recording
        if (MainUserInterface.recorder == null) {
            MainUserInterface.recorder = new AudioRecorder();
            MainUserInterface.recorder.startRecording();
            this.recordButton.setIcon(ImageHelper.getImageIcon(STOP_BUTTON_FILENAME, 50));
        } else {
            // Start a new thread to transcribe the recording, since we don't want
            // to block the UI thread.
            Thread t = new Thread(() -> {
                MainUserInterface.recorder.stopRecording();
                this.recordButton.setEnabled(false);
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    // ...
                }
                File recordingFile = MainUserInterface.recorder.getRecordingFile();
                IWhisper whisper = new Whisper(Constants.OPENAI_API_KEY);
                WhisperCheck whisperCheck = new WhisperCheck(whisper, recordingFile);

                String question = whisperCheck.output();

                if (whisperCheck.isExceptionThrown()) {
                    // Show a message box with an error containing the exception content
                    JOptionPane.showMessageDialog(this.frame,
                            TRANSCRIPTION_ERROR_TEXT + question,
                            ERROR_TEXT,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ChatGpt chatGpt = new ChatGpt(Constants.OPENAI_API_KEY, 100);
                String response;
                try {
                    response = chatGpt.askQuestion(question);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this.frame,
                            TRANSCRIPTION_ERROR_TEXT + ex.getMessage(),
                            ERROR_TEXT,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // store data to database
                QuestionAnswerEntry qaEntry = new QuestionAnswerEntry(new Question(question), new Answer(response));
                currentQID = db.insert(qaEntry);
                displayEntry(qaEntry);

                // add data to scrollBar
                QuestionButton button = new QuestionButton(question, currentQID);
                button.setPreferredSize(new Dimension(180, 100));
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        displayEntry(qaEntry);

                        // keep track of the selected button
                        selectedButton = button;
                    }

                });
                scrollBar.add(button);

                // update scrollBar
                scrollBar.revalidate();
                scrollBar.repaint();

                this.recordButton.setIcon(ImageHelper.getImageIcon(RECORD_BUTTON_FILENAME, 50));
                MainUserInterface.recorder = null;
                this.recordButton.setEnabled(true);
                if (!recordingFile.delete()) {
                    System.err.println(DELETION_ERROR_TEXT);
                }
            });

            t.start();
        }
    }

    /**
     * Adds the specified components to this user interface.
     *
     * @param pane The pane to add the components to.
     */
    public void addComponentsToPane(Container pane) {
        Map<Integer, QuestionAnswerEntry> entries = db.getEntries();
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.recordButton = new RoundButton(RECORD_BUTTON_FILENAME, 40);
        this.recordButton.addActionListener(this::onRecordButtonPress);

        toolBar.add(recordButton);

        // Create deleteQuestion button and add listener for functionality
        this.deleteButton = new RoundButton(TRASHCAN_FILENAME, 40);
        toolBar.add(deleteButton);

        // deletion functionality on click
        deleteButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if (selectedButton != null) {
                                // delete QuestionAnswer pair from database
                                if (db.delete(selectedButton.getId())) {
                                    // display a success message
                                    String response = DELETION_SUCCESS_TEXT;
                                    JOptionPane.showMessageDialog(null, response);
                                }
                                // delete button from UI/sidebar
                                scrollBar.remove(selectedButton);

                                // reset question and answer text
                                questionTextArea.setText(QUESTION_HEADER_TEXT);
                                answerTextArea.setText(ANSWER_HEADER_TEXT);

                                // update scrollBar
                                scrollBar.revalidate();
                                scrollBar.repaint();

                                selectedButton = null;

                                // TODO: refactor event handlers
                            }
                            // if the last selected question was already deleted or no question has yet been
                            // selected
                            else {
                                String response = DELETION_NONE_SELECTED_TEXT;
                                JOptionPane.showMessageDialog(null, response);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, ERROR_TEXT + ex.getMessage());
                        }
                    }
                });

        JButton clearAllButton = new JButton(CLEAR_ALL_BUTTON_TITLE);
        toolBar.add(clearAllButton);
        clearAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                db.clearAll();
                scrollBar.removeAll();
                scrollBar.revalidate();
                scrollBar.repaint();
                questionTextArea.setText(QUESTION_HEADER_TEXT);
                answerTextArea.setText(ANSWER_HEADER_TEXT);
            }
        });
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

        this.questionScrollPane = new JScrollPane(questionTextArea);
        this.questionScrollPane.setPreferredSize(new Dimension(380, 240));

        // Create the "Answer" JTextArea and JScrollPane
        answerTextArea = new JTextArea();
        answerTextArea.setLineWrap(true);
        answerTextArea.setEditable(false);
        answerTextArea.setText(ANSWER_HEADER_TEXT);

        // Create the "Question" JTextArea and JScrollPane
        this.answerScrollPane = new JScrollPane(answerTextArea);
        this.answerScrollPane.setPreferredSize(new Dimension(380, 240));

        // load entries onto scrollBar

        for (Map.Entry<Integer, QuestionAnswerEntry> entry : entries.entrySet()) {
            String question = entry.getValue().getQuestion().getQuestionText();
            QuestionButton button = new QuestionButton(question, entry.getKey());
            button.setPreferredSize(new Dimension(180, 100));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    displayEntry(entry.getValue());

                    // track which button was last selected for deletion
                    selectedButton = button;
                }
            });
            scrollBar.add(button);
        }

        // Create panel for each JTextArea and add Panel to the Main Pane
        JPanel content = new JPanel();
        content.add(questionScrollPane);
        content.add(answerScrollPane);
        content.setPreferredSize(new Dimension(500, 500));
        pane.add(content, BorderLayout.CENTER);
    }

}

/**
 * Button class for questions on sidebar
 */
class QuestionButton extends JButton {
    private int id;

    /**
     * Creates a <c>QuestionButton</c> object with the displayName.
     *
     * @param displayName The text to be displayed on the button.
     * @param id          The ID of the corresponding QuestionAnswerEntry in the
     *                    database
     */
    public QuestionButton(String displayName, int id) {
        // TODO: do we want this to change based on the length?
        super(displayName);
        this.setPreferredSize(new Dimension(180, 100));
        this.id = id;
    }

    /**
     * Public getter method for the ID
     * 
     * @return
     */
    public int getId() {
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
