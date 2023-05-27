package sayit.server.db.mongo;

import com.mongodb.client.MongoCollection;
import sayit.server.db.common.IAccountHelper;
import sayit.server.db.doctypes.SayItAccount;

import static com.mongodb.client.model.Filters.eq;

/**
 * A class containing several helper methods to interact with the <c>accounts</c> collection.
 */
public final class MongoAccountHelper implements IAccountHelper {
    public static final String ACCOUNTS_COLLECTION = "accounts";
    private static MongoAccountHelper _instance;

    private final MongoCollection<SayItAccount> _sayItAccounts;

    private MongoAccountHelper(MongoWrapper wrapper) {
        this._sayItAccounts = wrapper.getSayItDatabase().getCollection(ACCOUNTS_COLLECTION, SayItAccount.class);
    }

    /**
     * Gets, or creates, a new instance of the <c>MongoAccountHelper</c> class.
     *
     * @param wrapper The <c>MongoWrapper</c> instance to use.
     * @return The <c>MongoAccountHelper</c> instance.
     */
    public static MongoAccountHelper getOrCreateInstance(MongoWrapper wrapper) {
        if (_instance == null) {
            _instance = new MongoAccountHelper(wrapper);
        }

        return _instance;
    }

    /**
     * Gets an account by username.
     *
     * @param username The username to search for.
     * @return The account, or null if none exists.
     */
    @Override
    public SayItAccount getAccount(String username) {
        return this._sayItAccounts.find(eq(SayItAccount.USERNAME_FIELD, username)).first();
    }

    /**
     * Inserts a new <c>SayItAccount</c> into the database.
     *
     * @param account The account to create.
     */
    @Override
    public void createAccount(SayItAccount account) {
        this._sayItAccounts.insertOne(account);
    }


    @Override
    public void save() {
        // Do nothing, since Mongo automatically handles saving.
    }
}
