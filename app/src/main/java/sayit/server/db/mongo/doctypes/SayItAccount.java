package sayit.server.db.mongo.doctypes;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * Every user using the SayIt application must have an account. We'll store their account
 * information in a document represented by this class. Each account will have
 * <ul>
 *     <li>a user ID, which is guaranteed to be unique.</li>
 *     <li>
 *         a username, which is guaranteed to be unique (note that, in future versions,
 *         the username may not be unique.
 *     </li>
 *     <li>the password associated with this account.</li>
 * </ul>
 */
public class SayItAccount {
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";

    @BsonProperty(USERNAME_FIELD)
    private String username;
    @BsonProperty(PASSWORD_FIELD)
    private String password;

    /**
     * Create a new <c>MongoSayItAccount</c> with no properties set. Required for
     * serialization and deserialization.
     */
    @BsonCreator
    public SayItAccount() {
    }

    /**
     * Create a new <c>MongoSayItAccount</c> with the specified username, and password.
     *
     * @param username The username
     * @param password The password
     */
    public SayItAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username associated with this account.
     *
     * @return The username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the password associated with this account.
     *
     * @return The password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the username associated with this account.
     *
     * @param username The username.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Sets the password associated with this account.
     *
     * @param password The password.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of this account.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "{SayItAccount username="
                + this.username
                + ", password="
                + this.password
                + "}";
    }
}