package sayit.server.db.doctypes;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import sayit.common.UniversalConstants;

/**
 * A class representing a prompt in the <c>sayit</c> database. Each user can
 * have one or more prompts. Each prompt has a <c>username</c> field, which
 * is how we know which prompts belong to which users.
 */
public class SayItPrompt {
    // Document Fields.
    public static final String USERNAME_FIELD = UniversalConstants.USERNAME;
    public static final String TIMESTAMP_FIELD = "timestamp";
    public static final String TYPE_FIELD = UniversalConstants.TYPE;
    public static final String INPUT_FIELD = UniversalConstants.INPUT;
    public static final String OUTPUT_FIELD = UniversalConstants.OUTPUT;

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

    /**
     * Checks if the provided object (<c>SayItPrompt</c>) has the same fields
     * as this <c>SayItPrompt</c>.
     *
     * @param obj the object being compared to
     * @return true if the fields are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SayItPrompt prompt) {
            return (this.username.equals(prompt.getUsername())
                    && this.timestamp.equals(prompt.getTimestamp())
                    && this.type.equals(prompt.getType())
                    && this.input.equals(prompt.getInput())
                    && this.output.equals(prompt.getOutput()));
        }
        return false;
    }
}
