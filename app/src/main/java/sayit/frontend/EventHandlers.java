package sayit.frontend;

import sayit.common.UniversalConstants;
import sayit.common.qa.InputOutputEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static sayit.frontend.FrontEndConstants.ANSWER_HEADER_TEXT;
import static sayit.frontend.FrontEndConstants.QUESTION_HEADER_TEXT;

/**
 * A class that contains static methods to handle events in the UI.
 */
public final class EventHandlers {

    // constants
    private static final String PASSWORD_HEADER = "Password: ";
    private static final String VERIFY_PASSWORD_HEADER = "Verify Password";
    private static final String INVALID_INPUT_PROMPT = "Invalid email/password";
    private static final String USERNAME_IN_USE_PROMPT = "The provided username is already in use. "
            + "Try a different username.";
    private static final String UNKNOWN_ERROR_PROMPT = "An unknown error occurred when creating your account. "
            + "Please try again later.";
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
    public static ActionListener onQaButtonPress(MainUserInterface ui, InputOutputEntry qa, QuestionButton button) {
        return e -> {
            ui.displayEntry(qa);
            // track which button was last selected for deletion
            ui.setSelectedButton(button);
        };
    }

    /**
     * Handles the event when the user presses the create button.
     *
     * @param instance The <c>LoginUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onCreateButtonPress(LoginUserInterface instance) {
        return e -> {
            // check valid input
            if (instance.getEmail().length() == 0
                    || instance.getPassword().length() == 0) {
                JOptionPane.showMessageDialog(null, INVALID_INPUT_PROMPT);
                return;
            }

            String username = LoginUserInterface.getInstance().getEmail();
            // Check if the account has been created
            try {
                if (RequestSender.getInstance().doesAccountExist(username)) {
                    JOptionPane.showMessageDialog(null, USERNAME_IN_USE_PROMPT);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        FrontEndConstants.SERVER_UNAVAILABLE_TEXT + " " + ex.getMessage());
                return;
            }

            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel(PASSWORD_HEADER));
            JTextField passwordField = new JTextField(10);
            myPanel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(null, myPanel, VERIFY_PASSWORD_HEADER,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            if (!passwordField.getText().equals(instance.getPassword())) {
                JOptionPane.showMessageDialog(null, VERIFICATION_FAILED_PROMPT);
                return;
            }

            // successful login
            // Create the account
            try {
                boolean created = RequestSender
                        .getInstance()
                        .createAccount(username,
                                instance.getPassword());

                if (!created) {
                    JOptionPane.showMessageDialog(null, UNKNOWN_ERROR_PROMPT);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        FrontEndConstants.SERVER_UNAVAILABLE_TEXT + " " + ex.getMessage());
                return;
            }

            instance.close(); // close the login UI
            MainUserInterface.getInstance(); // start the main UI
        };
    }

    /**
     * Handles the event when the user presses the login button.
     *
     * @param instance The <c>LoginUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onLoginButtonPress(LoginUserInterface instance) {
        return e -> {
            instance.close(); // close the login UI
            MainUserInterface.getInstance(); // start the main UI
        };
    }

    /**
     * Handles the event when the user presses the Start button from the sidebar (the
     * question/answer history button).
     *
     * @param ui The <c>MainUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onStartButtonPress(MainUserInterface ui) {
        return e -> {
            //check everything is good
            if (!RequestSender.getInstance().isAlive()) {
                JOptionPane.showMessageDialog(ui.getFrame(), FrontEndConstants.SERVER_UNAVAILABLE_TEXT, FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
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

                    File recordingFile = ui.getRecorder().getRecordingFile();
                    //server should respond with JSON because this will eventually be all request
                    InputOutputEntry serverResponse;
                    try {
                        serverResponse = RequestSender.getInstance().sendRecording(recordingFile, ui.getUsername());
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(ui.getFrame(), e1.getMessage(), FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                        resetRecordButton(ui, recordingFile);
                        return;
                    }

                    switch (serverResponse.getType()) {
                        case UniversalConstants.QUESTION -> {
                            ui.displayEntry(serverResponse);

                            // add data to scrollBar
                            QuestionButton button = new QuestionButton(serverResponse.getInput().getInputText(),
                                    serverResponse.getID());
                            button.setPreferredSize(new Dimension(180, 100));
                            button.addActionListener(onQaButtonPress(ui, serverResponse, button));
                            ui.getScrollBar().add(button);

                            // update scrollBar
                            ui.getScrollBar().revalidate();
                            ui.getScrollBar().repaint();
                            ui.setSelectedButton(button);
                        }
                        case UniversalConstants.DELETE_PROMPT -> {
                            if (ui.getSelectedButton() == null) {
                                if (ui.getQuestionTextArea().getText().equals(QUESTION_HEADER_TEXT)
                                        && ui.getAnswerTextArea().getText().equals(ANSWER_HEADER_TEXT)) {
                                    JOptionPane.showMessageDialog(ui.getFrame(), "No question selected.",
                                            FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                                }

                                ui.getQuestionTextArea().setText(QUESTION_HEADER_TEXT);
                                ui.getAnswerTextArea().setText(ANSWER_HEADER_TEXT);
                                resetRecordButton(ui, recordingFile);
                                return;
                            }

                            try {
                                RequestSender.getInstance().delete(ui.getSelectedButton().getId(),
                                        ui.getUsername());
                            } catch (Exception ex) {
                                resetRecordButton(ui, recordingFile);
                                JOptionPane.showMessageDialog(ui.getFrame(), ex.getMessage(),
                                        FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                            }

                            ui.getScrollBar().remove(ui.getSelectedButton());

                            // reset question and answer text
                            ui.getQuestionTextArea().setText(QUESTION_HEADER_TEXT);
                            ui.getAnswerTextArea().setText(ANSWER_HEADER_TEXT);
                            // update scrollBar
                            ui.getScrollBar().revalidate();
                            ui.getScrollBar().repaint();
                            ui.setSelectedButton(null);
                        }
                        case UniversalConstants.CLEAR_ALL -> {
                            try {
                                RequestSender.getInstance().clearHistory(ui.getUsername());
                            } catch (Exception ex) {
                                resetRecordButton(ui, recordingFile);
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, ex.getMessage());
                                return;
                            }
                            ui.getScrollBar().removeAll();
                            ui.getScrollBar().revalidate();
                            ui.getScrollBar().repaint();
                            ui.getQuestionTextArea().setText(QUESTION_HEADER_TEXT);
                            ui.getAnswerTextArea().setText(ANSWER_HEADER_TEXT);
                            ui.setSelectedButton(null);
                        }
                        default -> {
                            // Assume error
                            ui.getQuestionTextArea().setText(QUESTION_HEADER_TEXT
                                    + serverResponse.getInput().getInputText().trim());
                            ui.getAnswerTextArea().setText(ANSWER_HEADER_TEXT
                                    + serverResponse.getOutput().getOutputText().trim());
                            ui.setSelectedButton(null);
                        }
                    }

                    resetRecordButton(ui, recordingFile);
                });

                t.start();
            }
        };
    }

    private static void resetRecordButton(MainUserInterface ui, File recordingFile) {
        ui.setRecorder(null);
        ui.getStartButton().setEnabled(true);
        ui.getStartButton().setText("START");
        if (recordingFile != null && !recordingFile.delete()) {
            System.err.println(FrontEndConstants.DELETION_ERROR_TEXT);
        }
    }
}
