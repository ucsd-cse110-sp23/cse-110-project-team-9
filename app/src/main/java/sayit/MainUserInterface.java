package sayit;

import sayit.helpers.ImageHelper;
import sayit.openai.ChatGpt;
import sayit.openai.IWhisper;
import sayit.openai.Whisper;
import sayit.openai.WhisperCheck;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;

import javax.swing.*;

public class MainUserInterface {
    private static final String appName = "SayIt Assistant";
    private static final String trashCanFileName = "./Pictures/TrashCan.jpg";
    private static final String recordButtonFileName = "./Pictures/RecordButton.jpg";
    private static final String stopButtonFileName = "./Pictures/StopButton.jpg";

    private JButton recordButton;
    private JButton stopButton;
    private JScrollPane answerScrollPane;
    private JScrollPane questionScrollPane;
    private final JFrame frame;
    private AudioRecorder recorder;

    private MainUserInterface() {
        this.frame = new JFrame(appName);
        addComponentsToPane(this.frame.getContentPane());
        this.frame.pack();
        this.frame.setVisible(true);
        this.recorder = null;
    }

    private static MainUserInterface userInterface;

    /**
     * <p>
     * Gets or creates a new instance of the <c>MainUserInterface</c> class. This method is
     * designed so that there can be at most one instance of the <c>MainUserInterface</c> class
     * at any point.
     * </p>
     *
     * <p>
     * This will also automatically make the user interface visible if it hasn't been initialized.
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
     * A method that should run when the recording button is pressed.
     *
     * @param e The event arguments.
     */
    private void onRecordButtonPress(ActionEvent e) {
        // If we're not recording
        if (this.recorder == null) {
            this.recorder = new AudioRecorder();
            this.recorder.startRecording();
            this.recordButton.setIcon(ImageHelper.getImageIcon(stopButtonFileName, 50));
        } else {
            // Start a new thread to transcribe the recording, since we don't want
            // to block the UI thread.
            Thread t = new Thread(() -> {
                this.recorder.stopRecording();
                this.recordButton.setEnabled(false);
                File recordingFile = this.recorder.getRecordingFile();
                IWhisper whisper = new Whisper(Constants.OPENAI_API_KEY);
                WhisperCheck whisperCheck = new WhisperCheck(whisper, recordingFile);

                String question = whisperCheck.output();

                if (whisperCheck.isExceptionThrown()) {
                    // Show a message box with an error containing the exception content
                    JOptionPane.showMessageDialog(this.frame,
                            "Unable to transcribe response. " + question,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);

                }

                ChatGpt chatGpt = new ChatGpt(Constants.OPENAI_API_KEY, 100);
                String response;
                try {
                    response = chatGpt.askQuestion(question);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this.frame,
                            "Unable to transcribe response. " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // question & response -> database
                // TODO connor

                this.recordButton.setIcon(ImageHelper.getImageIcon(recordButtonFileName, 50));
                this.recorder = null;
                this.recordButton.setEnabled(true);
                if (!recordingFile.delete()) {
                    System.err.println("Unable to delete recording file.");
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
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.recordButton = new RoundButton(recordButtonFileName, 50);
        this.recordButton.addActionListener(this::onRecordButtonPress);

        toolBar.add(recordButton);
        this.stopButton = new RoundButton(stopButtonFileName, 40);
        toolBar.add(stopButton);

        toolBar.add(new RoundButton(trashCanFileName, 40));
        //TODO: ADD ACTION LISTENER TO THIS BUTTON
        toolBar.add(new JButton("Clear All"));
        //TODO: ADD ACTION LISTENER TO THIS BUTTON
        pane.add(toolBar, BorderLayout.PAGE_START);

        //JTextArea scrollBar = new JTextArea("Questions"); //TEST SCROLL BAR
        JPanel scrollBar = new JPanel(new GridLayout(0, 1)); //USE THIS FOR APP
        for (int i = 0; i < 6; i++) {
            JButton test = new JButton(String.valueOf(i));
            test.setPreferredSize(new Dimension(180, 100));
            scrollBar.add(test);
        }

        //TODO: ADD ALL QUESTIONS TO THIE PANEL
        JScrollPane scrollPane = new JScrollPane(scrollBar);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(200, 500));
        pane.add(scrollPane, BorderLayout.LINE_START);

        //Add entry
        //boxes are hardcoded as of right now, once we get entries figured out I can change it to the answer
        //im thinking entry object will be passed over to interface and then interface code can fix it

        // Create the "Question" JTextArea and JScrollPane
        JTextArea questionTextArea = new JTextArea();
        questionTextArea.setEditable(false);
        questionTextArea.setText("Question: This is a hard coded question");

        this.questionScrollPane = new JScrollPane(questionTextArea);
        this.questionScrollPane.setPreferredSize(new Dimension(380, 250));

        // Create the "Answer" JTextArea and JScrollPane
        JTextArea answerTextArea = new JTextArea();
        answerTextArea.setEditable(false);
        answerTextArea.setText("Answer: This is a hard coded answer");

        //Create the "Question" JTextArea and JScrollPane
        this.answerScrollPane = new JScrollPane(answerTextArea);
        this.answerScrollPane.setPreferredSize(new Dimension(380, 250));

        // Create panel for each JTextArea and add Panel to the Main Pane

        JPanel content = new JPanel();
        content.add(questionScrollPane);
        content.add(answerScrollPane);
        content.setPreferredSize(new Dimension(500, 500));
        pane.add(content, BorderLayout.CENTER);
    }

    //updates question and answer boxes with a new entry
    //commented out until files are linked
    //public void displayEntry(Entry e){
    //questionScrollPane.setViewportView(displayer.displayQuestion(e));
    //answerScrollPane.setViewportView(displayer.displayAnswer(e));
    //}
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
