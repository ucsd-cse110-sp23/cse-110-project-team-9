package sayit.frontend;

// Imports for remembering login

import sayit.frontend.events.LoginUiEventHandlers;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static sayit.frontend.FrontEndConstants.*;

/**
 * The login user interface for the application.
 */
public class LoginUserInterface {

    // class variables
    private final JFrame frame;
    private JTextField emailField;
    private JTextField passwordField;
    private final File info;

    // constants
    private final Dimension BUTTON_DIMENSION = new Dimension(150, 40);
    private final Dimension TEXT_FIELD_DIMENSION = new Dimension(200, 40);
    private static final String APP_NAME = "SAYIT_ASSISTANT 2.0";
    private static final String CREATE_ACCOUNT_PROMPT = "Create Account";
    private static final String LOGIN_PROMPT = "Login";
    private static final String EMAIL_HEADER = "Email: ";
    private static final String PASSWORD_HEADER = "Password: ";
    private static final String EMPTY_TEXT = "";
    private static final String LOGIN_INFO_FAILED_TEXT = "No saved username found";
    private static final String AUTO_LOGIN_FAILED = "Automatic login failed. The most probable cause is that"
            + " the server is offline or the automatic login file has been corrupted. Try logging in manually.";

    private LoginUserInterface() {
        frame = new JFrame(APP_NAME);

        //automatically login if file storing login information already exists
        info = new File(LOGIN_INFO_FILENAME);

        // If the file exists...
        if (info.exists()) {
            // See if we can log in
            if (getLoginInformation()) {
                return;
            }

            // If we can't log in, notify the user
            JOptionPane.showMessageDialog(frame, AUTO_LOGIN_FAILED, ERROR_TEXT,
                    JOptionPane.ERROR_MESSAGE);
        }

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
        createButton.addActionListener(LoginUiEventHandlers.onCreateButtonPress(this));
        bottomPanel.add(createButton);
        JButton loginButton = new JButton(LOGIN_PROMPT);
        loginButton.setPreferredSize(BUTTON_DIMENSION);
        loginButton.addActionListener(LoginUiEventHandlers.onLoginButtonPress(this));
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
    public void clearText() {
        emailField.setText(EMPTY_TEXT);
        passwordField.setText(EMPTY_TEXT);
    }

    /**
     * Helper function for asking and logging whether to remember user login on this device
     *
     * @param username String containing user's email to automatically login
     */
    public void rememberLogin(String username) {
        int confirmRemember = JOptionPane.showConfirmDialog(this.frame, REMEMBER_LOGIN_TEXT, REMEMBER_LOGIN_TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        // if user confirms, write username to file
        if (confirmRemember == JOptionPane.YES_OPTION) {
            // create file and close
            try {
                info.createNewFile();

                FileWriter writer = new FileWriter(LOGIN_INFO_FILENAME);

                //save username
                writer.write(username);
                writer.close();
            } catch (IOException exception) {
                System.out.println(ERROR_TEXT);
                exception.printStackTrace();
            }
        }
    }

    /**
     * Read login information from the file stored on local device
     * Automatically log in using stored username
     *
     * @return true if successfully logged in automatically
     */
    public boolean getLoginInformation() {
        try {
            Scanner readInfo = new Scanner(info);

            //check for file content and read username
            if (readInfo.hasNextLine()) {
                if (!RequestSender.getInstance().isAlive()) {
                    return false;
                }

                String username = readInfo.nextLine();
                readInfo.close();

                // Check if the account exists
                try {
                    if (RequestSender.getInstance().doesAccountExist(username)) {
                        MainUserInterface.createInstance(username); // start the main UI
                        return true;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            FrontEndConstants.SERVER_UNAVAILABLE_TEXT + " " + ex.getMessage());
                }
            } else {
                System.out.println(LOGIN_INFO_FAILED_TEXT);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ERROR_TEXT);
            ex.printStackTrace();
        }

        return false;
    }
}
