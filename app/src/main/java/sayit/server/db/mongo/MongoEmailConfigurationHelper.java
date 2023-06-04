package sayit.server.db.mongo;

import com.mongodb.client.MongoCollection;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.doctypes.SayItAccount;
import sayit.server.db.doctypes.SayItEmailConfiguration;
import sayit.server.db.doctypes.SayItPrompt;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * A class containing several helper methods to interact with
 * the <c>email_configurations</c> collection.
 */
public final class MongoEmailConfigurationHelper implements IEmailConfigurationHelper {
    public static final String EMAIL_CONFIGURATIONS_COLLECTION = "email_configurations";
    private static MongoEmailConfigurationHelper _instance;

    private final MongoCollection<SayItEmailConfiguration> _sayItEmailConfigurations;

    private MongoEmailConfigurationHelper(MongoWrapper wrapper) {
        this._sayItEmailConfigurations = wrapper.getSayItDatabase().
                getCollection(EMAIL_CONFIGURATIONS_COLLECTION, SayItEmailConfiguration.class);
    }

    /**
     * Gets, or creates, a new instance of the <c>MongoEmailConfigurationHelper</c> class.
     *
     * @param wrapper The <c>MongoWrapper</c> instance to use.
     * @return The <c>MongoEmailConfigurationHelper</c> instance.
     */
    public static MongoEmailConfigurationHelper getOrCreateInstance(MongoWrapper wrapper) {
        if (_instance == null) {
            _instance = new MongoEmailConfigurationHelper(wrapper);
        }

        return _instance;
    }

    /**
     * Gets an email configuration by username.
     *
     * @param username The username to search for.
     * @return The email configuration, or null if none exists.
     */
    @Override
    public SayItEmailConfiguration getEmailConfiguration(String username) {
        return this._sayItEmailConfigurations.find(eq(
                SayItEmailConfiguration.ACC_USERNAME_FIELD, username)).first();
    }

    /**
     * Inserts a new <c>SayItEmailConfiguration</c> into the database.
     * Precondition: config has all nonnull fields.
     *
     * @param config The email configuration to create.
     */
    @Override
    public void createEmailConfiguration(SayItEmailConfiguration config) {
        this._sayItEmailConfigurations.insertOne(config);
    }

    /**
     * Deletes a <c>SayItEmailConfiguration</c> from the database.
     *
     * @param username The user whose email configuration will be deleted.
     */
    @Override
    public boolean deleteEmailConfiguration(String username) {
        return this._sayItEmailConfigurations.deleteOne(
                eq(SayItEmailConfiguration.ACC_USERNAME_FIELD, username)
        ).getDeletedCount() > 0;
    }

    /**
     * Replaces a <c>SayItEmailConfiguration</c> from the database.
     * Precondition: This method is only called after <c>getEmailConfiguration()</c> returns a nonull value.
     * Calls <c>deleteEmailConfiguration()</c> then <c>createEmailConfiguration()</c>.
     *
     * @param config The email configuration to be inserted into the databse, replacing
     *               a pre-existing one.
     */
    @Override
    public void replaceEmailConfiguration(SayItEmailConfiguration config) {
        this.deleteEmailConfiguration(config.getAccUsername());
        this.createEmailConfiguration(config);
    }

    @Override
    public void save() {
        // Do nothing, since Mongo automatically handles saving.
    }
}
