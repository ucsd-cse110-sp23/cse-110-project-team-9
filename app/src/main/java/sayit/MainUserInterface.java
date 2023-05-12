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
    private static final String appName = "SayIt Assistant";
    private static final String trashCanFileName = "./Pictures/TrashCan.jpg";
    private static final String recordButtonFileName = "./Pictures/RecordButton.jpg";
    private static final String stopButtonFileName = "./Pictures/StopButton.jpg";
    private static final String dataFileName = "./data.tsv";

    private JButton recordButton;
    private JPanel scrollBar;
    private JTextArea questionTextArea;
    private JTextArea answerTextArea;
    private JButton deleteButton;
    private boolean isSelected;
    private int idSelected;
    private JScrollPane answerScrollPane;
    private JScrollPane questionScrollPane;
    private final JFrame frame;
    private AudioRecorder recorder;
    private TsvStore db;
    private int currentQID;
    private IStore<QuestionAnswerEntry> database;

    private MainUserInterface() {
        this.frame = new JFrame(appName);
        db = TsvStore.createOrOpenStore(dataFileName);
        addComponentsToPane(this.frame.getContentPane());	
        this.frame.pack();
        this.frame.setVisible(true);
        this.recorder = null;

        //add behavior for closing app
        //update db
        //code taken from https://stackoverflow.com/questions/9093448/how-to-capture-a-jframes-close-button-click-event
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(frame, 
                    "Are you sure you want to close this window?", "Close Window?", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    db.save();
                    System.exit(0);
                }
            }
        });
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
    
    public void displayEntry(QuestionAnswerEntry entry) {
		questionTextArea.setText("Question: \n\n" + entry.getQuestion().getQuestionText().trim());
		answerTextArea.setText("ChatGPT Response: \n\n" + entry.getAnswer().getAnswerText().trim());
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
                currentQID = db.insert(qaEntry);
				displayEntry(qaEntry);
                
                //add data to scrollBar
                JButton button = new JButton(question);
                button.setPreferredSize(new Dimension(180, 100));
                button.addActionListener(new ActionListener() {
    				@Override
    				public void actionPerformed(ActionEvent e) {
    					displayEntry(qaEntry);
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

        // Create deleteQuestion button and add listener for functionality
        this.deleteButton = new RoundButton(trashCanFileName, 40);
        toolBar.add(deleteButton);
        deleteButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            //TODO: find which question is selected
                            if(isSelected){
                                //delete qa pair from database
                                if(database.delete(idSelected)){
                                    String response = "Deleted question";
                                    JOptionPane.showMessageDialog(null, response);
                                    //TODO: specify a success message?
                                }
                                // TODO: delete button from UI/sidebar

                                //TODO: refactor?
                            }
                            else{
                                String response = "No question selected";
                                JOptionPane.showMessageDialog(null, response);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                        }
                    }
        });

        toolBar.add(new JButton("Clear All"));
        //TODO: ADD ACTION LISTENER TO THIS BUTTON
        pane.add(toolBar, BorderLayout.PAGE_START);

        //JTextArea scrollBar = new JTextArea("Questions"); //TEST SCROLL BAR
        JPanel scrollBar = new JPanel(new GridLayout(0, 1)); //USE THIS FOR APP
        for (int i = 0; i < 6; i++) {
            JButton test = new JButton(String.valueOf(i));
            scrollBar.add(test);

            // Keep track of which question is selected for the deleteButton
            test.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        isSelected = true;
                        //idSelected = test.id;
                        // TODO: get id from button
                    }
                });
        }

        //TODO: ADD ALL QUESTIONS TO THE PANEL
        JScrollPane scrollPane = new JScrollPane(scrollBar);
        //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(200, 500));
        pane.add(scrollPane, BorderLayout.LINE_START);

        // Create the "Question" JTextArea and JScrollPane
        questionTextArea = new JTextArea();
        questionTextArea.setLineWrap(true);
        questionTextArea.setEditable(false);
        questionTextArea.setText("Question: ");

        this.questionScrollPane = new JScrollPane(questionTextArea);
        this.questionScrollPane.setPreferredSize(new Dimension(380, 240));

        // Create the "Answer" JTextArea and JScrollPane
        answerTextArea = new JTextArea();
        answerTextArea.setLineWrap(true);
        answerTextArea.setEditable(false);
        answerTextArea.setText("Answer: ");

        //Create the "Question" JTextArea and JScrollPane
        this.answerScrollPane = new JScrollPane(answerTextArea);
        this.answerScrollPane.setPreferredSize(new Dimension(380, 240));
        
        //load entries onto scrollBar
        Map<Integer, QuestionAnswerEntry> entries = db.getEntries();
        
        for (Map.Entry<Integer, QuestionAnswerEntry> entry : entries.entrySet()) {
        	String question = entry.getValue().getQuestion().getQuestionText();
        	String answer = entry.getValue().getAnswer().getAnswerText();
            JButton button = new JButton(question);
            button.setPreferredSize(new Dimension(180, 100));
            button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					displayEntry(entry.getValue());
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
    private Shape shape;
    private int id;

    /**
     * Creates a <c>QuestionButton</c> object with the specified image and size.
     *
     * @param fileName The file containing the image.
     * @param size     The size of the button.
     */
    public QuestionButton(String displayName, int id) {
        // TODO: do we want this to change based on the length
        super(displayName);
        this.setPreferredSize(new Dimension(180, 100));
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
    //updates question and answer boxes with a new entry
    //commented out until files are linked
    //public void displayEntry(Entry e){
    //questionScrollPane.setViewportView(displayer.displayQuestion(e));
    //answerScrollPane.setViewportView(displayer.displayAnswer(e));
    //}
}

/**
 * Button class for questions on sidebar
 */
class QuestionButton extends JButton {
    private Shape shape;
    private int id;

    /**
     * Creates a <c>QuestionButton</c> object with the specified image and size.
     *
     * @param fileName The file containing the image.
     * @param size     The size of the button.
     */
    public QuestionButton(String displayName, int id) {
        // TODO: do we want this to change based on the length
        super(displayName);
        this.setPreferredSize(new Dimension(180, 100));
        this.id = id;
    }

    public int getId(){
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
