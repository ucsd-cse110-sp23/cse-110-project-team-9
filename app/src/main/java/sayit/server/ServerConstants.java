package sayit.server;

/**
 * A class containing constants for the server.
 */
public final class ServerConstants {
    /**
     * The default server port.
     */
    public static final int SERVER_PORT = 8100;

    /**
     * The default server hostname.
     */
    public static final String SERVER_HOSTNAME = "localhost";

    /**
     * The OpenAI API key. It might be worth checking if this is an empty string
     * before letting the rest of the application run; if this is an empty string,
     * this implies that we have not set up their API key yet so none of the
     * ChatGPT-related functions will work.
     */
    public static final String OPENAI_API_KEY = "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";

    /**
     * The MongoDB connection URI. It might be worth checking if this is an empty
     * string before letting the rest of the application run; if this is an empty
     * string, this implies that we have not set up the MongoDB connection yet so
     * none of the MongoDB-related functions will work.
     */
    public static final String MONGO_URI = "mongodb://GregMiranda:welcometocse12prerecordedlecture@ac-cw8rf8r-shard-00-00.uyp7wd8.mongodb.net:27017,ac-cw8rf8r-shard-00-01.uyp7wd8.mongodb.net:27017,ac-cw8rf8r-shard-00-02.uyp7wd8.mongodb.net:27017/?ssl=true&replicaSet=atlas-ubuvdk-shard-0&authSource=admin&retryWrites=true&w=majority";

    public static final String UNKNOWN_PROMPT_OUTPUT = "The command you provided is not recognized. Try a command like:\n"
            + "\"Question <ask a question>\" or \"Clear All.\" or \"Delete Prompt.\" or \"Create an email to <recipient> "
            + "about <subject>\" or \"Send email to <recipient email address>\"";
    
    public static final String MISSING_ECONFIG = "Please set up email configurations first"
            + " with the command \"Set up email\".";
    public static final String NO_PROMPT_AFTER_COMMAND = "Please follow your command with a prompt. Try saying:\n" 
            + "\"Question, how big is the earth?\" or \n \"Create an email to Kyle wishing him happy birthday\"";
}
