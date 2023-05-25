package sayit.frontend;

import sayit.common.qa.QuestionAnswerEntry;

import javax.swing.*;
import java.awt.event.ActionListener;

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
}
