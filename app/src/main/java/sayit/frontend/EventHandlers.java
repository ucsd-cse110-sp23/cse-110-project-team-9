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

    /**
     * Handles the event when the user presses the record button.
     *
     * @param ui The <c>MainUserInterface</c> object.
     * @return An ActionListener object.
     */
    public static ActionListener onRecordButtonPress(MainUserInterface ui) {
        return e -> {
            if (!ui.getRequestSender().isAlive()) {
                JOptionPane.showMessageDialog(ui.getFrame(), SERVER_UNAVAILABLE_TEXT, ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ui.getRecorder() == null) {
                ui.setRecorder(new AudioRecorder());
                ui.getRecorder().startRecording();
                ui.getRecordButton().setIcon(ImageHelper.getImageIcon(STOP_BUTTON_FILENAME, 50));
            } else {
                // Start a new thread to transcribe the recording, since we don't want
                // to block the UI thread.
                Thread t = new Thread(() -> {
                    ui.getRecorder().stopRecording();
                    ui.getRecordButton().setEnabled(false);

                    // Just so the file can be saved to the disk
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        // ...
                    }

                    File recordingFile = ui.getRecorder().getRecordingFile();
                    Pair<Integer, QuestionAnswerEntry> serverResponse;
                    try {
                        serverResponse = ui.getRequestSender().askQuestion(recordingFile);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(ui.getFrame(), e1.getMessage(), ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                        ui.getRecordButton().setIcon(ImageHelper.getImageIcon(RECORD_BUTTON_FILENAME, 50));
                        ui.setRecorder(null);
                        ui.getRecordButton().setEnabled(true);
                        return;
                    }

                    // store data to database
                    QuestionAnswerEntry qaEntry = serverResponse.getSecond();
                    ui.displayEntry(qaEntry);

                    // add data to scrollBar
                    QuestionButton button = new QuestionButton(qaEntry.getQuestion().getQuestionText(), serverResponse.getFirst());
                    button.setPreferredSize(new Dimension(180, 100));
                    button.addActionListener(onQaButtonPress(ui, qaEntry, button));
                    ui.getScrollBar().add(button);

                    // update scrollBar
                    ui.getScrollBar().revalidate();
                    ui.getScrollBar().repaint();

                    ui.setSelectedButton(button);

                    ui.getRecordButton().setIcon(ImageHelper.getImageIcon(RECORD_BUTTON_FILENAME, 50));
                    ui.setRecorder(null);
                    ui.getRecordButton().setEnabled(true);
                    if (!recordingFile.delete()) {
                        System.err.println(DELETION_ERROR_TEXT);
                    }
                });

                t.start();
            }
        };
    }

    /**
     * Handles the event when the user presses the button from the sidebar (the question/answer history button).
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
     * Handles the event when the user presses the clear all button.
     *
     * @param ui The <c>MainUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onClearAllButtonPress(MainUserInterface ui) {
        return e -> {
            if (!ui.getRequestSender().isAlive()) {
                JOptionPane.showMessageDialog(ui.getFrame(), SERVER_UNAVAILABLE_TEXT, ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ui.getRequestSender().clearHistory();
            } catch (Exception ex) {
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
        };
    }

    /**
     * Handles the event when the user presses the delete button.
     *
     * @param ui The <c>MainUserInterface</c> object.
     * @return An <c>ActionListener</c> object.
     */
    public static ActionListener onDeleteButtonPress(MainUserInterface ui) {
        return e -> {
            if (!ui.getRequestSender().isAlive()) {
                JOptionPane.showMessageDialog(ui.getFrame(), SERVER_UNAVAILABLE_TEXT, ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (ui.getSelectedButton() != null) {
                    // delete QuestionAnswer pair from database
                    boolean deleted;
                    try {
                        deleted = ui.getRequestSender().delete(ui.getSelectedButton().getId());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        deleted = false;
                    }

                    if (deleted) {
                        JOptionPane.showMessageDialog(null, DELETION_SUCCESS_TEXT);
                    }
                    // delete button from UI/sidebar
                    ui.getScrollBar().remove(ui.getSelectedButton());

                    // reset question and answer text
                    ui.getQuestionTextArea().setText(QUESTION_HEADER_TEXT);
                    ui.getAnswerTextArea().setText(ANSWER_HEADER_TEXT);

                    // update scrollBar
                    ui.getScrollBar().revalidate();
                    ui.getScrollBar().repaint();

                    ui.setSelectedButton(null);
                }
                // if the last selected question was already deleted or no question has yet been
                // selected
                else {
                    JOptionPane.showMessageDialog(null, DELETION_NONE_SELECTED_TEXT);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ERROR_TEXT + ex.getMessage());
            }
        };
    }
}
