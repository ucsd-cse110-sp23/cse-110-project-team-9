package sayit.server.db.mongo;

import com.mongodb.client.MongoCollection;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * A class containing several helper methods to interact with the <c>prompts</c> collection.
 */
public class MongoPromptHelper implements IPromptHelper {

    public static final String PROMPTS_COLLECTION = "prompts";
    private static MongoPromptHelper _instance;

    private final MongoCollection<SayItPrompt> _sayItPrompts;

    private MongoPromptHelper(MongoWrapper wrapper) {
        this._sayItPrompts = wrapper.getSayItDatabase().getCollection(PROMPTS_COLLECTION, SayItPrompt.class);
    }

    /**
     * Gets, or creates, a new instance of the <c>MongoPromptHelper</c> class.
     *
     * @param wrapper The <c>MongoWrapper</c> instance to use.
     * @return The <c>MongoPromptHelper</c> instance.
     */
    public static MongoPromptHelper getOrCreateInstance(MongoWrapper wrapper) {
        if (_instance == null) {
            _instance = new MongoPromptHelper(wrapper);
        }

        return _instance;
    }

    /**
     * Gets all prompts for a particular user.
     *
     * @param username The username to search for.
     * @return A list of prompts.
     */
    @Override
    public List<SayItPrompt> getAllPromptsBy(String username) {
        List<SayItPrompt> prompts = new ArrayList<>();
        this._sayItPrompts.find(eq(SayItPrompt.USERNAME_FIELD, username)).into(prompts);
        return prompts;
    }

    /**
     * Gets a prompt by username and id.
     * @param username The username of the prompt.
     * @param id       The id of the prompt.
     * @return The prompt, or null if none exists.
     */
    @Override
    public SayItPrompt get(String username, long id) {
        return this._sayItPrompts.find(
                and(
                        eq(SayItPrompt.USERNAME_FIELD, username),
                        eq(SayItPrompt.TIMESTAMP_FIELD, id)
                )
        ).first();
    }

    /**
     * Inserts a new <c>SayItPrompt</c> into the database.
     *
     * @param prompt The prompt to create.
     */
    @Override
    public void createPrompt(SayItPrompt prompt) {
        this._sayItPrompts.insertOne(prompt);
    }

    /**
     * Deletes the <c>SayItPrompt</c> from the database.
     *
     * @param username  The username of the prompt.
     * @param timestamp The timestamp of the prompt.
     */
    @Override
    public boolean deletePrompt(String username, long timestamp) {
        return this._sayItPrompts.deleteOne(
                and(
                        eq(SayItPrompt.USERNAME_FIELD, username),
                        eq(SayItPrompt.TIMESTAMP_FIELD, timestamp)
                )
        ).getDeletedCount() > 0;
    }

    /**
     * Clears all <c>SayItPrompt</c> under the specific username.
     *
     * @param username The username
     */
    @Override
    public long clearAllPrompts(String username) {
        return this._sayItPrompts.deleteMany(eq(SayItPrompt.USERNAME_FIELD, username)).getDeletedCount();
    }

    @Override
    public void save() {
        // Do nothing, since Mongo automatically handles saving.
    }
}
