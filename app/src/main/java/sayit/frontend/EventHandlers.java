package sayit.frontend;

import sayit.common.qa.QuestionAnswerEntry;
import sayit.frontend.helpers.ImageHelper;
import sayit.frontend.helpers.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static sayit.frontend.FrontEndConstants.*;

/**
 * A class that contains static methods to handle events in the UI.
 */
public final class EventHandlers {

    // constants
    private static final String PASSWORD_HEADER = "Password: ";
    private static final String VERFIY_PASSWORD_HEADER = "Verify Password";
    private static final String INVALID_INPUT_PROMPT = "Invalid email/password";
    private static final String VERIFICATION_FAILED_PROMPT = "Password Verification Failed";

    /**
     * Handles the event when the user presses the button from the sidebar (the
     * question/answer history button).
     *
     * @param ui     The <c>MainUserInterface</c> object.
     * @param qa     The <c>QuestionAnswerEntry</c> object.
     * @param button The <c>QuestionButton</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onQaButtonPress(MainUserInterface ui, QuestionAnswerEntry qa, QuestionButton button) {
        return e -> {
            ui.displayEntry(qa);
            // track which button was last selected for deletion
            ui.setSelectedButton(button);
        };
    }

    /**
     * Handles the event when the user presses the create button.
     * 
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onCreateButtonPress() {
        return e -> {
            // check valid input
            if (LoginUserInterface.getInstance().getEmail().length() == 0
                    || LoginUserInterface.getInstance().getPassword().length() == 0) {
                JOptionPane.showMessageDialog(null, INVALID_INPUT_PROMPT);
                return;
            }

            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel(PASSWORD_HEADER));
            JTextField passwordField = new JTextField(10);
            myPanel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(null, myPanel, VERFIY_PASSWORD_HEADER,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                if (passwordField.getText().equals(LoginUserInterface.getInstance().getPassword())) {
                    // successful login
                    LoginUserInterface.getInstance().close(); // close the login UI
                    MainUserInterface.getInstance(); // start the main UI
                } else {
                    JOptionPane.showMessageDialog(null, VERIFICATION_FAILED_PROMPT);
                }
            }
        };
    }

    /**
     * Handles the event when the user presses the login button.
     * 
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onLoginButtonPress() {
        return e -> {
            boolean verify = true; //verify email and password cobination
            if(verify) {
                LoginUserInterface.getInstance().close(); // close the login UI
                MainUserInterface.getInstance(); // start the main UI
            } else {
                
            }
        };
    }

    /**
     * Handles the event when the user presses the Start button from the sidebar (the
     * question/answer history button).
     *
     * @param ui     The <c>MainUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onStartButtonPress(MainUserInterface ui) {
        return e -> {
            //check everything is good
            if (!ui.getRequestSender().isAlive()) {
                JOptionPane.showMessageDialog(ui.getFrame(), SERVER_UNAVAILABLE_TEXT, ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                return;
            }

            //eiter record/stop recording
            if (ui.getRecorder() == null) {
                ui.setRecorder(new AudioRecorder());
                ui.getRecorder().startRecording();
                ui.getStartButton().setText("RECORDING");
            } else {
                // Start a new thread to transcribe the recording, since we don't want
                // to block the UI thread.
                Thread t = new Thread(() -> {
                    ui.getRecorder().stopRecording();
                    ui.getStartButton().setEnabled(false);
                    ui.getStartButton().setText("PROCESSING");


                    // Just so the file can be saved to the disk
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        // ...
                    }

                    /*
                     * TODO Jimmy
                     * you are going to need to figure out how excatly the server is going to handle things
                     * start button just sends file for now
                     */

                    File recordingFile = ui.getRecorder().getRecordingFile();
                    Pair<Integer, QuestionAnswerEntry> serverResponse;//server should respond with JSON because this will eventually be all request
                    try {
                        serverResponse = ui.getRequestSender().sendRecording(recordingFile); //NEED TO CHANGE PLACEHOLDER FOR NOW
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(ui.getFrame(), e1.getMessage(), ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                        ui.setRecorder(null);
                        ui.getStartButton().setEnabled(true);
                        ui.getStartButton().setText("START");
                        return;
                    }

                    //TESTING REMOVE LATER
                    //right now its behaving as if question is asked, whole prompt will be displayed as if it were a question from MS1
                    QuestionAnswerEntry qaEntry = serverResponse.getSecond();
                    ui.displayEntry(qaEntry);

                    /*
                     * We need some way to handle the server response JSON here
                     * Maybe another file of helper methods that we can just pass it too
                     */

                    ui.setRecorder(null);
                    ui.getStartButton().setEnabled(true);
                    ui.getStartButton().setText("START");
                    if (!recordingFile.delete()) {
                        System.err.println(DELETION_ERROR_TEXT);
                    }
                });

                t.start();
            }
        };
    }
}
