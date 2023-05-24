package sayit.server.db.common;

import sayit.server.db.mongo.doctypes.SayItAccount;

public interface IAccountHelper {
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
