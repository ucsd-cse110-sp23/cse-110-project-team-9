package sayit.frontend;

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
    public static final String DELETION_NONE_SELECTED_TEXT = "No question selected";
    public static final String DELETION_ERROR_TEXT = "Unable to delete recording file.";
    public static final String SERVER_UNAVAILABLE_TEXT = "The server is currently offline "
            + "and cannot handle any requests. This functionality is currently unavailable.";
    public static final String APP_CANNOT_RUN_TEXT = "The server is currently offline and cannot handle"
            + " any requests, and will terminate.";
    public static final String ERROR_TEXT = "Error";


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
}
