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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.Map;

import javax.swing.*;

public class MainUserInterface {
    private static final String appName = "SayIt Assistant";
    private static final String trashCanFileName = "./Pictures/TrashCan.jpg";
    private static final String recordButtonFileName = "./Pictures/RecordButton.jpg";
    private static final String stopButtonFileName = "./Pictures/StopButton.jpg";
    private static final String dataFileName = "./data.tsv";

    private JButton recordButton;
    private JPanel scrollBar;
    private JTextArea questionTextArea;
    private JTextArea answerTextArea;
    private JScrollPane answerScrollPane;
    private JScrollPane questionScrollPane;
    private final JFrame frame;
    private AudioRecorder recorder;
    private TsvStore data;

    private MainUserInterface() {
        this.frame = new JFrame(appName);
        data = TsvStore.createOrOpenStore(dataFileName);
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

                //store data to database
                QuestionAnswerEntry qaEntry = new QuestionAnswerEntry(new Question(question), new Answer(response));
                int id = data.insert(qaEntry);
				questionTextArea.setText("Question: \n\n" + question);
				answerTextArea.setText("ChatGPT Response: \n\n" + response.trim());
                
                //add data to scrollBar
                JButton button = new JButton(question);
                button.setPreferredSize(new Dimension(180, 100));
                button.addActionListener(new ActionListener() {
    				@Override
    				public void actionPerformed(ActionEvent e) {
    					questionTextArea.setText("Question: \n\n" + question);
    					answerTextArea.setText("ChatGPT Response: \n\n" + response.trim());
    				}
                	
                });
                scrollBar.add(button);
                
                //update scrollBar
                scrollBar.revalidate();
                scrollBar.repaint();

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
        this.recordButton = new RoundButton(recordButtonFileName, 40);
        this.recordButton.addActionListener(this::onRecordButtonPress);

        toolBar.add(recordButton);

        JButton deleteButton = new RoundButton(trashCanFileName, 40);
        toolBar.add(deleteButton);
        //TODO: ADD ACTION LISTENER TO THIS BUTTON
        JButton clearAllButton = new JButton("Clear All");
        toolBar.add(clearAllButton);
        //TODO: ADD ACTION LISTENER TO THIS BUTTON
        pane.add(toolBar, BorderLayout.PAGE_START);
        scrollBar = new JPanel(new GridLayout(0, 1)); //USE THIS FOR APP
        
        JScrollPane scrollPane = new JScrollPane(scrollBar);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(200, 500));
        pane.add(scrollPane, BorderLayout.LINE_START);

        // Create the "Question" JTextArea and JScrollPane
        questionTextArea = new JTextArea();
        questionTextArea.setEditable(false);
        questionTextArea.setText("Question: ");

        this.questionScrollPane = new JScrollPane(questionTextArea);
        this.questionScrollPane.setPreferredSize(new Dimension(380, 240));

        // Create the "Answer" JTextArea and JScrollPane
        answerTextArea = new JTextArea();
        answerTextArea.setEditable(false);
        answerTextArea.setText("Answer: ");

        //Create the "Question" JTextArea and JScrollPane
        this.answerScrollPane = new JScrollPane(answerTextArea);
        this.answerScrollPane.setPreferredSize(new Dimension(380, 240));
        
        //load entries onto scrollBar
        Map<Integer, QuestionAnswerEntry> entries = data.getEntries();
        
        for (Map.Entry<Integer, QuestionAnswerEntry> entry : entries.entrySet()) {
        	String question = entry.getValue().getQuestion().getQuestionText();
        	String answer = entry.getValue().getAnswer().getAnswerText();
            JButton button = new JButton(question);
            button.setPreferredSize(new Dimension(180, 100));
            button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					questionTextArea.setText("Question: \n" + question);
					answerTextArea.setText("ChatGPT Response: \n" + answer);
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
