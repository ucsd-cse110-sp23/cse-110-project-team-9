package sayit.frontend;

import java.awt.*;

/**
 * Various constants specifically for the frontend code.
 */
public final class FrontEndConstants {
    // Constants for headers and titles
    public static final String APP_TITLE = "SayIt Assistant";
    public static final String QUESTION_HEADER_TEXT = "Question: \n\n";
    public static final String ANSWER_HEADER_TEXT = "ChatGPT Response: \n\n";
    public static final String CLOSE_WINDOW_TEXT = "Are you sure you want to close this window?";
    public static final String CLOSE_WINDOW_TITLE = "Close Window?";
    public static final String RECORD_START_TEXT = "START";
    public static final String RECORD_ONGOING_TEXT = "RECORDING";
    public static final String RECORD_PROCESSING_TEXT = "PROCESSING";

    // Constants for success and error messages
    public static final String DELETION_NONE_SELECTED_TEXT = "No prompt selected. Please select a prompt and try again.";
    public static final String DELETION_ERROR_TEXT = "Unable to delete recording file.";
    public static final String SERVER_UNAVAILABLE_TEXT = "The server is currently offline "
            + "and cannot handle any requests. This functionality is currently unavailable.";
    public static final String APP_CANNOT_RUN_TEXT = "The server is currently offline and cannot handle"
            + " any requests, and will terminate.";
    public static final String ERROR_TEXT = "Error";
    public static final String SUCCESS_TEXT = "Success";
    public static final String EMAIL_SAVED = "Your email configuration has been saved.";
    public static final String EMAIL_NOT_SAVED = "Your email configuration could not be saved.";
    public static final String EMAIL_NOT_SEND_NO_PROMPT = "Your email could not be sent because you didn't select" +
            " a prompt.";
    public static final String EMAIL_NOT_SENT = "Your email could not be sent. Make sure your email and password "
            + "are correct, and further ensure that your email configuration (e.g., SMTP and TLS port) is correct.";

    // Constants for events
    public static final String PASSWORD_HEADER = "Password: ";
    public static final String VERIFY_PASSWORD_HEADER = "Verify Password";
    public static final String INVALID_INPUT_PROMPT = "Invalid email/password";
    public static final String USERNAME_IN_USE_PROMPT = "The provided username is already in use. "
            + "Try a different username.";
    public static final String UNKNOWN_ERROR_PROMPT = "An unknown error occurred when creating your account. "
            + "Please try again later.";
    public static final String VERIFICATION_FAILED_PROMPT = "Password Verification Failed";
    public static final String LOGIN_FAILED_PROMPT = "Login credentials invalid";
    public static final String EMAIL_MISSING_INFO = "Please fill out all fields before continuing.";

    public static final int NUM_EMAIL_FIELDS = 7;

    // Constants for requests
    public static final String USERNAME_QUERY_PARAM = "username=";
    public static final String TO_ADDRESS_QUERY_PARAM = "toAddress=";
    public static final String ID_QUERY_PARAM = "id=";
    public static final String NEW_ID_QUERY_PARAM = "newID=";
    

    // Button Dimensions
    public static final Dimension PROMPT_HISTORY_BTN_DIMENSIONS = new Dimension(180, 100);
    public static final Dimension SHORT_BUTTON_DIMENSION = new Dimension(150, 40);
    public static final Dimension TEXT_FIELD_DIMENSION = new Dimension(200, 40);

    // Remember login
    public static final String LOGIN_INFO_FILENAME = System.getProperty("user.home") + "/LoginInfo.txt";
    public static final String REMEMBER_LOGIN_TEXT = "Would you like to remember your login information on this device?";
    public static final String REMEMBER_LOGIN_TITLE = "Automatically Log In?";

    // Email constants
    public static final String EMAIL_HEADER = "Email: ";
    public static final String FIRST_NAME_HEADER = "First Name: ";
    public static final String LAST_NAME_HEADER = "LAST Name: ";
    public static final String DISPLAY_NAME_HEADER = "Display Name: ";
    public static final String SMTP_HOST_HEADER = "SMTP Host: ";
    public static final String TLS_PORT_HEADER = "TLS Port: ";
    public static final String CANCEL_HEADER = "Cancel";
    public static final String EMPTY_STRING = "";
    public static final String SAVE_PROMPT = "Save";

}
