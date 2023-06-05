package sayit.frontend;

import sayit.common.IAction;
import sayit.frontend.events.EmailSetupUiEventHandlers;

import javax.swing.*;
import java.awt.*;

import static sayit.frontend.FrontEndConstants.*;

/**
 * The email setup user interface for the application.
 */
public class EmailSetupUserInterface {
    // class variables
    private final JFrame frame;
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField displayNameField;
    private final JTextField emailField;
    private final JTextField passwordField;
    private final JTextField smtpHostField;
    private final JTextField tlsPortField;
    private String[] storedInformation;

    private final IAction<String[]> saveAction;

    /**
     * Creates a new email setup user interface with the specified action to perform after the save button
     * is pressed.
     *
     * @param saveAction The action to perform after the save button is pressed.
     */
    public EmailSetupUserInterface(IAction<String[]> saveAction) {
        this(saveAction, null);
    }

    /**
     * Creates a new email setup user interface with the specified action to perform after the save button
     * is pressed and the specified data to initially populate the fields with.
     *
     * @param saveAction The action to perform after the save button is pressed.
     * @param data       The data to populate the fields with.
     */
    public EmailSetupUserInterface(IAction<String[]> saveAction, String[] data) {
        this.firstNameField = new JTextField();
        this.lastNameField = new JTextField();
        this.displayNameField = new JTextField();
        this.emailField = new JTextField();
        this.passwordField = new JTextField();
        this.smtpHostField = new JTextField();
        this.tlsPortField = new JTextField();
        this.storedInformation = data == null ? new String[7] : data;
        this.saveAction = saveAction;

        frame = new JFrame(FrontEndConstants.APP_TITLE);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addComponentsToPane(this.frame.getContentPane());
        this.frame.pack();
        this.frame.setVisible(true);

        frame.addWindowListener(EmailSetupUiEventHandlers.onClosePress(this));
    }

    /**
     * Adds the specified components to this user interface.
     *
     * @param contentPane The pane to add the components to.
     */
    private void addComponentsToPane(Container contentPane) {
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JPanel firstNamePanel = new JPanel();
        JPanel lastNamePanel = new JPanel();
        JPanel displayNamePanel = new JPanel();
        JPanel emailPanel = new JPanel();
        JPanel passwordPanel = new JPanel();
        JPanel smtpHostPanel = new JPanel();
        JPanel tlsPortPanel = new JPanel();

        setUpPanel(firstNamePanel, FIRST_NAME_HEADER, firstNameField);
        setUpPanel(lastNamePanel, LAST_NAME_HEADER, lastNameField);
        setUpPanel(displayNamePanel, DISPLAY_NAME_HEADER, displayNameField);
        setUpPanel(emailPanel, EMAIL_HEADER, emailField);
        setUpPanel(passwordPanel, PASSWORD_HEADER, passwordField);
        setUpPanel(smtpHostPanel, SMTP_HOST_HEADER, smtpHostField);
        setUpPanel(tlsPortPanel, TLS_PORT_HEADER, tlsPortField);

        contentPane.add(firstNamePanel);
        contentPane.add(lastNamePanel);
        contentPane.add(displayNamePanel);
        contentPane.add(emailPanel);
        contentPane.add(passwordPanel);
        contentPane.add(smtpHostPanel);
        contentPane.add(tlsPortPanel);

        JPanel bottomPanel = new JPanel();
        JButton saveButton = new JButton(SAVE_PROMPT);
        saveButton.setPreferredSize(SHORT_BUTTON_DIMENSION);
        saveButton.addActionListener(EmailSetupUiEventHandlers.onSavePress(this));
        bottomPanel.add(saveButton);
        JButton cancelButton = new JButton(CANCEL_HEADER);
        cancelButton.setPreferredSize(SHORT_BUTTON_DIMENSION);
        cancelButton.addActionListener(EmailSetupUiEventHandlers.onCancelPress(this));
        bottomPanel.add(cancelButton);
        contentPane.add(bottomPanel);
    }

    /**
     * Sets up the panel given as an input
     *
     * @param pane   The pane to set up
     * @param header The header which the pane uses
     * @param field  The text field which the pane uses
     */
    private void setUpPanel(JPanel pane, String header, JTextField field) {
        pane.add(new JLabel(header));
        field.setPreferredSize(TEXT_FIELD_DIMENSION);
        pane.add(field);
    }

    /**
     * Opens the LoginUserInterface
     */
    public void open() {
        loadTextFields();
        frame.setVisible(true);
    }

    /**
     * loads the text fields using stored Information
     */
    private void loadTextFields() {
        firstNameField.setText(storedInformation[0]);
        lastNameField.setText(storedInformation[1]);
        displayNameField.setText(storedInformation[2]);
        emailField.setText(storedInformation[3]);
        passwordField.setText(storedInformation[4]);
        smtpHostField.setText(storedInformation[5]);
        tlsPortField.setText(storedInformation[6]);
    }

    /**
     * Closes the LoginUserInterface
     */
    public void close() {
        clearTextFields();
        frame.setVisible(false);
    }

    /**
     * clears the textFields of the class
     */
    private void clearTextFields() {
        firstNameField.setText(EMPTY_STRING);
        lastNameField.setText(EMPTY_STRING);
        displayNameField.setText(EMPTY_STRING);
        emailField.setText(EMPTY_STRING);
        passwordField.setText(EMPTY_STRING);
        smtpHostField.setText(EMPTY_STRING);
        tlsPortField.setText(EMPTY_STRING);
    }

    /**
     * Saves stored information into the array storedInformation in the form of
     * [FirstName, LastName, DisplayName, Email, Password, SMTPHost, TLSPort]
     */
    public void save() {
        if (firstNameField.getText().equals(EMPTY_STRING)
                || lastNameField.getText().equals(EMPTY_STRING)
                || displayNameField.getText().equals(EMPTY_STRING)
                || emailField.getText().equals(EMPTY_STRING)
                || passwordField.getText().equals(EMPTY_STRING)
                || smtpHostField.getText().equals(EMPTY_STRING)
                || tlsPortField.getText().equals(EMPTY_STRING)) {
            JOptionPane.showMessageDialog(frame, EMAIL_MISSING_INFO, ERROR_TEXT, JOptionPane.ERROR_MESSAGE);
            return;
        }

        storedInformation[0] = firstNameField.getText();
        storedInformation[1] = lastNameField.getText();
        storedInformation[2] = displayNameField.getText();
        storedInformation[3] = emailField.getText();
        storedInformation[4] = passwordField.getText();
        storedInformation[5] = smtpHostField.getText();
        storedInformation[6] = tlsPortField.getText();
        this.saveAction.execute(storedInformation);
    }

    /**
     * Returns the array containing saved information in the form of
     * [FirstName, LastName, DisplayName, Email, Password, SMTPHost, TLSPort]
     *
     * @return a String array containing stored information
     */
    public String[] getInfo() {
        return storedInformation;
    }

    /**
     * Sets the stored information to the input array. The array must be of length
     * 7. This should be called before calling open() to load the text fields with
     * the stored information.
     *
     * @param info The array containing the information to be stored. This must be
     *             of the form [FirstName, LastName, DisplayName, Email, Password,
     *             SMTPHost, TLSPort].
     */
    public void setInfo(String[] info) {
        if (info.length != NUM_EMAIL_FIELDS) {
            return;
        }

        this.storedInformation = info;
    }
}