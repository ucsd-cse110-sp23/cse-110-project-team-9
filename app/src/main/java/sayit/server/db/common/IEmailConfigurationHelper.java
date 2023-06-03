package sayit.server.db.common;

import sayit.server.db.doctypes.SayItAccount;
import sayit.server.db.doctypes.SayItEmailConfiguration;

/**
 * An interface containing several helper methods to interact with the
 * <c>email_configurations</c> collection.
 */
public interface IEmailConfigurationHelper extends ISaveable {
    /**
     * Gets an email configuration by username.
     *
     * @param username The username to search for.
     * @return The email configuration, or null if none exists.
     */
    SayItEmailConfiguration getEmailConfiguration(String username);

    /**
     * Inserts a new <c>SayItEmailConfiguration</c> into the database.
     *
     * @param config The email configuration to create.
     */
    void createEmailConfiguration(SayItEmailConfiguration config);

    /**
     * Deletes a <c>SayItEmailConfiguration</c> from the database.
     *
     * @param username The user whose email configuration will be deleted.
     * @return true if an email configuration was deleted, false otherwise
     */
    boolean deleteEmailConfiguration(String username);

    /**
     * Replaces a <c>SayItEmailConfiguration</c> from the database.
     * This method is only called after <c>getEmailConfiguration()</c> returns true.
     * Calls <c>deleteEmailConfiguration()</c> then <c>createEmailConfiguration()</c>.
     *
     * @param config The email configuration to be inserted into the databse, replacing
     *               a pre-existing one.
     */
    void replaceEmailConfiguration(SayItEmailConfiguration config);
}
