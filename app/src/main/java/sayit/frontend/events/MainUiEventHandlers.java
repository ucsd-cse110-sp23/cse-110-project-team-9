package sayit.frontend.events;

import sayit.common.UniversalConstants;
import sayit.common.qa.InputOutputEntry;
import sayit.frontend.*;
import sayit.frontend.components.SidebarButton;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static sayit.common.UniversalConstants.EMAIL_SENT_SUCCESSFULLY;
import static sayit.frontend.FrontEndConstants.*;

/**
 * A class that contains static methods to handle events in the main UI.
 */
public final class MainUiEventHandlers {
    /**
     * Handles the event when the user presses the button from the sidebar (the
     * question/answer history button).
     *
     * @param ui     The <c>MainUserInterface</c> object.
     * @param qa     The <c>QuestionAnswerEntry</c> object.
     * @param button The <c>QuestionButton</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onQaButtonPress(MainUserInterface ui, InputOutputEntry qa, SidebarButton button) {
        return e -> {
            ui.displayEntry(qa);
            // track which button was last selected for deletion
            ui.setSelectedButton(button);
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
                JOptionPane.showMessageDialog(ui.getFrame(), FrontEndConstants.SERVER_UNAVAILABLE_TEXT,
                        FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                resetStartButton(ui, null);
                return;
            }

            //eiter record/stop recording
            if (ui.getRecorder() == null) {
                ui.setRecorder(new AudioRecorder());
                ui.getRecorder().startRecording();
                ui.getStartButton().setText(RECORD_ONGOING_TEXT);
            } else {
                // Start a new thread to transcribe the recording, since we don't want
                // to block the UI thread.
                Thread t = new Thread(() -> {
                    ui.getRecorder().stopRecording();
                    ui.getStartButton().setEnabled(false);
                    ui.getStartButton().setText(RECORD_PROCESSING_TEXT);


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
                        serverResponse = RequestSender.getInstance().sendRecording(recordingFile, ui.getUser());
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(ui.getFrame(), e1.getMessage(),
                                FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                        resetStartButton(ui, recordingFile);
                        return;
                    }

                    switch (serverResponse.getType()) {
                        case UniversalConstants.QUESTION, UniversalConstants.EMAIL_DRAFT ->
                                addButtonToSidebar(ui, serverResponse);
                        case UniversalConstants.DELETE_PROMPT -> {
                            if (!checkButtonSelected(ui, recordingFile)) {
                                JOptionPane.showMessageDialog(ui.getFrame(), DELETION_NONE_SELECTED_TEXT,
                                        FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            try {
                                RequestSender.getInstance().delete(ui.getSelectedButton().getId(),
                                        ui.getUser());
                            } catch (Exception ex) {
                                resetStartButton(ui, recordingFile);
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
                                RequestSender.getInstance().clearHistory(ui.getUser());
                            } catch (Exception ex) {
                                resetStartButton(ui, recordingFile);
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
                        case UniversalConstants.SETUP_EMAIL -> {
                            // See if the user has any email configuration set up

                            Map<String, String> emailConfig;
                            try {
                                emailConfig = RequestSender.getInstance().getEmailConfiguration(ui.getUser());
                            } catch (Exception ex) {
                                resetStartButton(ui, recordingFile);
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, ex.getMessage());
                                return;
                            }

                            String[] emailInfo = null;
                            if (emailConfig != null) {
                                String firstName = emailConfig.get(UniversalConstants.FIRST_NAME);
                                String lastName = emailConfig.get(UniversalConstants.LAST_NAME);
                                String email = emailConfig.get(UniversalConstants.EMAIL);
                                String password = emailConfig.get(UniversalConstants.EMAIL_PASSWORD);
                                String smtp = emailConfig.get(UniversalConstants.SMTP);
                                String tls = emailConfig.get(UniversalConstants.TLS);
                                String displayName = emailConfig.get(UniversalConstants.DISPLAY_NAME);

                                emailInfo = new String[]{firstName, lastName, displayName, email, password, smtp, tls};
                            }

                            EmailSetupUserInterface emailSetupUserInterface = new EmailSetupUserInterface(data -> {
                                if (!RequestSender.getInstance().isAlive()) {
                                    JOptionPane.showMessageDialog(ui.getFrame(), FrontEndConstants.SERVER_UNAVAILABLE_TEXT,
                                            FrontEndConstants.ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                String firstName = data[0];
                                String lastName = data[1];
                                String displayName = data[2];
                                String email = data[3];
                                String password = data[4];
                                String smtp = data[5];
                                String tls = data[6];

                                boolean result;
                                try {
                                    result = RequestSender.getInstance().saveEmailConfiguration(ui.getUser(),
                                            firstName, lastName, displayName, email, password, smtp, tls);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null,
                                            EMAIL_NOT_SAVED + " " + ex.getMessage(),
                                            ERROR_TEXT,
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                    return;
                                }

                                if (result) {
                                    JOptionPane.showMessageDialog(null, EMAIL_SAVED, SUCCESS_TEXT,
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, EMAIL_NOT_SAVED, ERROR_TEXT,
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }, emailInfo);

                            emailSetupUserInterface.open();
                        }
                        case UniversalConstants.SEND_EMAIL -> {
                            if (!checkButtonSelected(ui, recordingFile)) {
                                JOptionPane.showMessageDialog(null, EMAIL_NOT_SEND_NO_PROMPT, ERROR_TEXT,
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            boolean success;
                            InputOutputEntry result;
                            try {
                                result = RequestSender.getInstance().sendEmail(ui.getUser(),
                                        serverResponse.getOutput().getOutputText(),
                                        ui.getSelectedButton().getId(),
                                        serverResponse.getID());

                                addButtonToSidebar(ui, result);
                                success = result.getOutput().getOutputText().trim().startsWith(EMAIL_SENT_SUCCESSFULLY);
                            } catch (Exception ex) {
                                resetStartButton(ui, recordingFile);
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null,
                                        EMAIL_NOT_SENT + " " + ex.getMessage(),
                                        ERROR_TEXT,
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }

                            if (success) {
                                JOptionPane.showMessageDialog(null, EMAIL_SENT_SUCCESSFULLY, SUCCESS_TEXT,
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, result.getOutput(), ERROR_TEXT,
                                        JOptionPane.ERROR_MESSAGE);
                            }
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

                    resetStartButton(ui, recordingFile);
                });

                t.start();
            }
        };
    }

    /**
     * Adds a button to the sidebar, updating the scroll bar.
     *
     * @param ui             The main user interface.
     * @param serverResponse The server response.
     */
    private static void addButtonToSidebar(MainUserInterface ui, InputOutputEntry serverResponse) {
        ui.displayEntry(serverResponse);

        // add data to scrollBar
        SidebarButton button = new SidebarButton(serverResponse.getInput().getInputText(),
                serverResponse.getID());
        button.setPreferredSize(PROMPT_HISTORY_BTN_DIMENSIONS);
        button.addActionListener(onQaButtonPress(ui, serverResponse, button));
        ui.getScrollBar().add(button);

        // update scrollBar
        ui.getScrollBar().revalidate();
        ui.getScrollBar().repaint();
        ui.setSelectedButton(button);
    }

    /**
     * Checks if a button is selected. If not, it displays an error message and resets the start button.
     *
     * @param ui The main user interface.
     * @return <c>true</c> if a button is selected, <c>false</c> otherwise.
     */
    private static boolean checkButtonSelected(MainUserInterface ui, File recordingFile) {
        if (ui.getSelectedButton() != null) {
            return true;
        }

        ui.getQuestionTextArea().setText(QUESTION_HEADER_TEXT);
        ui.getAnswerTextArea().setText(ANSWER_HEADER_TEXT);
        resetStartButton(ui, recordingFile);
        return false;
    }

    /**
     * Resets the start button to the initial button state.
     *
     * @param ui            The main user interface.
     * @param recordingFile The recording file, if any, or <c>null</c> otherwise.
     */
    private static void resetStartButton(MainUserInterface ui, File recordingFile) {
        ui.setRecorder(null);
        ui.getStartButton().setEnabled(true);
        ui.getStartButton().setText(RECORD_START_TEXT);
        if (recordingFile != null && !recordingFile.delete()) {
            System.err.println(FrontEndConstants.DELETION_ERROR_TEXT);
        }
    }
}
