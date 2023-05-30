package sayit.server.db.doctypes;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * A class representing a prompt in the <c>sayit</c> database. Each user can
 * have one or more prompts. Each prompt has a <c>username</c> field, which
 * is how we know which prompts belong to which users.
 */
public class SayItPrompt {
    // Document Fields.
    public static final String USERNAME_FIELD = "username";
    public static final String TIMESTAMP_FIELD = "timestamp";
    public static final String TYPE_FIELD = "type";
    public static final String INPUT_FIELD = "input";
    public static final String OUTPUT_FIELD = "output";

    // Types
    public static final String EMAIL_DRAFT = "EMAIL_DRAFT";
    public static final String QUESTION = "QUESTION";
    public static final String EMAIL_SENT = "EMAIL_SENT";

    @BsonProperty(USERNAME_FIELD)
    private String username;

    @BsonProperty(TIMESTAMP_FIELD)
    private Long timestamp;

    @BsonProperty(TYPE_FIELD)
    private String type;

    @BsonProperty(INPUT_FIELD)
    private String input;

    @BsonProperty(OUTPUT_FIELD)
    private String output;

    /**
     * Create a new <c>SayItPrompt</c> with no properties set. Required for
     */
    @BsonCreator
    public SayItPrompt() {
    }

    /**
     * Create a new <c>SayItPrompt</c> with the specified arguments.
     *
     * @param username  The username.
     * @param timestamp The timestamp.
     * @param type      The type.
     * @param input     The input.
     * @param output    The output.
     */
    public SayItPrompt(String username, long timestamp, String type, String input, String output) {
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
        this.input = input;
        this.output = output;
    }

    /**
     * Gets the username associated with this prompt.
     *
     * @return The username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the timestamp associated with this prompt.
     *
     * @return The timestamp.
     */
    public Long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Gets the type associated with this prompt.
     *
     * @return The type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets the input associated with this prompt.
     *
     * @return The input.
     */
    public String getInput() {
        return this.input;
    }

    /**
     * Gets the output associated with this prompt.
     *
     * @return The output.
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Sets the username associated with this prompt.
     *
     * @param username The username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the timestamp associated with this prompt.
     *
     * @param timestamp The timestamp.
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the type associated with this prompt.
     *
     * @param type The type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the input associated with this prompt.
     *
     * @param input The input.
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * Sets the output associated with this prompt.
     *
     * @param output The output.
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Returns a string representation of this prompt.
     *
     * @return A string representation of this prompt.
     */
    @Override
    public String toString() {
        return "SayItPrompt{"
                + "username='" + username + '\''
                + ", timestamp='" + timestamp + '\''
                + ", type='" + type + '\''
                + ", input='" + input + '\''
                + ", output='" + output + '\''
                + '}';
    }
}
