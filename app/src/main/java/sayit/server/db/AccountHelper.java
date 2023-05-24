package sayit.server.db;

import com.mongodb.client.MongoCollection;
import sayit.server.db.doctypes.SayItAccount;

import static com.mongodb.client.model.Filters.eq;

/**
 * A class containing several helper methods to interact with the <c>accounts</c> collection.
 */
public final class AccountHelper {
    public static final String ACCOUNTS_COLLECTION = "accounts";
    private static AccountHelper _instance;

    private final MongoCollection<SayItAccount> _sayItAccounts;

    private AccountHelper(MongoWrapper wrapper) {
        this._sayItAccounts = wrapper.getSayItDatabase().getCollection(ACCOUNTS_COLLECTION, SayItAccount.class);
    }

    /**
     * Gets, or creates, a new instance of the <c>AccountHelper</c> class.
     *
     * @param wrapper The <c>MongoWrapper</c> instance to use.
     * @return The <c>AccountHelper</c> instance.
     */
    public static AccountHelper getOrCreateInstance(MongoWrapper wrapper) {
        if (_instance == null) {
            _instance = new AccountHelper(wrapper);
        }

        return _instance;
    }

    /**
     * Gets an account by username.
     *
     * @param username The username to search for.
     * @return The account, or null if none exists.
     */
    public SayItAccount getAccount(String username) {
        return this._sayItAccounts.find(eq(SayItAccount.USERNAME_FIELD, username)).first();
    }

    /**
     * Inserts a new <c>SayItAccount</c> into the database.
     *
     * @param account The account to create.
     */
    public void createAccount(SayItAccount account) {
        this._sayItAccounts.insertOne(account);
    }
}
