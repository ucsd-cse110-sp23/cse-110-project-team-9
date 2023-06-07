package sayit.frontend.events;

import sayit.frontend.FrontEndConstants;
import sayit.frontend.LoginUserInterface;
import sayit.frontend.MainUserInterface;
import sayit.frontend.RequestSender;

import javax.swing.*;
import java.awt.event.ActionListener;

import static sayit.frontend.FrontEndConstants.*;

/**
 * This class contains the event handlers for the login UI.
 */
public final class LoginUiEventHandlers {
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

            String username = instance.getEmail();
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

            instance.rememberLogin(username);

            instance.close(); // close the login UI
            MainUserInterface.createInstance(username); // start the main UI
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
            String username = instance.getEmail();
            String password = instance.getPassword();
            try {
                if (!RequestSender.getInstance().login(username, password)) {
                    JOptionPane.showMessageDialog(null, LOGIN_FAILED_PROMPT);
                    instance.clearText();
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        FrontEndConstants.SERVER_UNAVAILABLE_TEXT + " " + ex.getMessage());
                return;
            }

            instance.rememberLogin(username);

            instance.close(); // close the login UI
            MainUserInterface.createInstance(username); // start the main UI
        };
    }
}
