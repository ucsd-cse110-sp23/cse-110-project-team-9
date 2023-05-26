package sayit.server.db.common;

import sayit.server.db.doctypes.SayItAccount;

/**
 * An interface containing several helper methods to interact with the <c>accounts</c> collection.
 */
public interface IAccountHelper extends ISaveable {
    /**
     * Gets an account by username.
     *
     * @param username The username to search for.
     * @return The account, or null if none exists.
     */
    SayItAccount getAccount(String username);

    /**
     * Inserts a new <c>SayItAccount</c> into the database.
     *
     * @param account The account to create.
     */
    void createAccount(SayItAccount account);
}
