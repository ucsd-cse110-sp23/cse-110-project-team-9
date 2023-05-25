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
    public static final String TITLE_FIELD = "title";
    public static final String RESULT_FIELD = "result";

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

    @BsonProperty(TITLE_FIELD)
    private String title;

    @BsonProperty(RESULT_FIELD)
    private String result;

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
     * @param title     The title.
     * @param result    The result.
     */
    public SayItPrompt(String username, long timestamp, String type, String title, String result) {
        this.username = username;
        this.timestamp = timestamp;
        this.type = type;
        this.title = title;
        this.result = result;
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
     * Gets the title associated with this prompt.
     *
     * @return The title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets the result associated with this prompt.
     *
     * @return The result.
     */
    public String getResult() {
        return this.result;
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
     * Sets the title associated with this prompt.
     *
     * @param title The title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the result associated with this prompt.
     *
     * @param result The result.
     */
    public void setResult(String result) {
        this.result = result;
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
                + ", title='" + title + '\''
                + ", result='" + result + '\''
                + '}';
    }
}
