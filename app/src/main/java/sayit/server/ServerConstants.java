package sayit.server;

import java.io.File;
import java.nio.file.Files;

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
    public static final String OPENAI_API_KEY;

    static {
        String apiKey = "";
        try {
            var configFile = new File("token.txt");
            apiKey = Files.readString(configFile.toPath()).trim();
        } catch (Exception e) {
            System.err.println("Failed to read OpenAI API key from token.txt. See stack trace below.");
            e.printStackTrace();
        }

        OPENAI_API_KEY = apiKey;
    }

    /**
     * The MongoDB connection URI. It might be worth checking if this is an empty
     * string before letting the rest of the application run; if this is an empty
     * string, this implies that we have not set up the MongoDB connection yet so
     * none of the MongoDB-related functions will work.
     */
    public static final String MONGO_URI;

    static {
        String mongoUri = "";
        try {
            var configFile = new File("mongo.txt");
            mongoUri = Files.readString(configFile.toPath()).trim();
        } catch (Exception e) {
            System.err.println("Failed to read MongoDB URI from mongo_uri.txt. See stack trace below.");
            e.printStackTrace();
        }

        MONGO_URI = mongoUri;
    }

    public static final String UNKNOWN_PROMPT_OUTPUT = "The command you provided is not recognized."
            + "Try a command like \"Question.\" or \"Clear All.\" or \"Delete Prompt.\"";
}
