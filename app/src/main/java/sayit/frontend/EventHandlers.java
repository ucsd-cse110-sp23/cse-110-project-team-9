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
}
