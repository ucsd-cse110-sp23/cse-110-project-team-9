package sayit.frontend;

import static sayit.frontend.FrontEndConstants.CLOSE_WINDOW_TEXT;
import static sayit.frontend.FrontEndConstants.CLOSE_WINDOW_TITLE;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The login user interface for the application.
 */
public class LoginUserInterface {

    // class variables
    private final JFrame frame;
    private JTextField emailField;
    private JTextField passwordField;

    // constants
    private final Dimension BUTTON_DIMENSION = new Dimension(150, 40);
    private final Dimension TEXT_FIELD_DIMENSION = new Dimension(200, 40);
    private static final String APP_NAME = "SAYIT_ASSISTANT 2.0";
    private static final String CREATE_ACCOUNT_PROMPT = "Create Account";
    private static final String LOGIN_PROMPT = "Login";
    private static final String EMAIL_HEADER = "Email: ";
    private static final String PASSWORD_HEADER = "Password: ";
    private static final String EMPTY_TEXT = "";

    private LoginUserInterface() {
        frame = new JFrame(APP_NAME);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addComponentsToPane(this.frame.getContentPane());
        this.frame.pack();
        this.frame.setVisible(true);

        // Add behavior for closing app, update db.
        // https://stackoverflow.com/questions/9093448/how-to-capture-a-jframes-close-button-click-event
        // https://www.codejava.net/java-se/swing/preventing-jframe-window-from-closing
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirmClose = JOptionPane.showConfirmDialog(frame, CLOSE_WINDOW_TEXT, CLOSE_WINDOW_TITLE,
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                // if user confirms closing the window, exit
                if (confirmClose == JOptionPane.YES_OPTION) {
                    // terminate Java VM and exit
                    System.exit(0);
                }
            }
        });
    }

    private static LoginUserInterface userInterface;

    /**
     * <p>
     * Gets or creates a new instance of the <c>LoginUserInterface</c> class. This
     * method is designed so that there can be at most one instance of the
     * <c>LoginUserInterface</c> class at any point.
     * </p>
     *
     * <p>
     * This will also automatically make the user interface visible if it hasn't
     * been initialized.
     * </p>
     *
     * @return The instance of the <c>LoginUserInterface</c> class.
     */
    public static LoginUserInterface getInstance() {
        if (userInterface == null) {
            userInterface = new LoginUserInterface();
        }

        return userInterface;
    }

    /**
     * Adds the specified components to this user interface.
     *
     * @param contentPane The pane to add the components to.
     */
    private void addComponentsToPane(Container contentPane) {

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel(EMAIL_HEADER));
        emailField = new JTextField();
        emailField.setPreferredSize(TEXT_FIELD_DIMENSION);
        topPanel.add(emailField);
        contentPane.add(topPanel);

        JPanel middlePanel = new JPanel();
        middlePanel.add(new JLabel(PASSWORD_HEADER));
        passwordField = new JTextField();
        passwordField.setPreferredSize(TEXT_FIELD_DIMENSION);
        middlePanel.add(passwordField);
        contentPane.add(middlePanel);

        JPanel bottomPanel = new JPanel();
        JButton createButton = new JButton(CREATE_ACCOUNT_PROMPT);
        createButton.setPreferredSize(BUTTON_DIMENSION);
        createButton.addActionListener(EventHandlers.onCreateButtonPress(this));
        bottomPanel.add(createButton);
        JButton loginButton = new JButton(LOGIN_PROMPT);
        loginButton.setPreferredSize(BUTTON_DIMENSION);
        loginButton.addActionListener(EventHandlers.onLoginButtonPress(this));
        bottomPanel.add(loginButton);
        contentPane.add(bottomPanel);
    }

    /**
     * Closes the LoginUserInterface
     */
    public void close() {
        frame.setVisible(false);
    }

    /**
     * Returns the text in the email field
     * 
     * @return the text in the email field
     */
    public String getEmail() {
        return emailField.getText();
    }

    /**
     * Returns the text in the password field
     * 
     * @return the text in the password field
     */
    public String getPassword() {
        return passwordField.getText();
    }

    /**
     * Clears the text in the email and password field
     */
    public void clearText(){
        emailField.setText(EMPTY_TEXT);
        passwordField.setText(EMPTY_TEXT);
    }
}
