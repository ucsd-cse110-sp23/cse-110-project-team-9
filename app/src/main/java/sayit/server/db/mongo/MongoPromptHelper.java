package sayit.server.db.mongo;

import com.mongodb.client.MongoCollection;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;

import java.util.ArrayList;
import java.util.List;

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
     * @param username The username of the prompt
     * @param timestamp The timestamp of the prompt
     * @param type The type of prompt
     * @param title The title of the prompt
     * @param result The result of the prompt
     */
    @Override
    public void deletePrompt(String username, long timestamp, String type, String title, String result) {
        //TODO

    }

    /**
     * Clears all <c>SayItPrompt</c> under the specific username.
     *
     * @param username The username
     */
    @Override
    public void clearAllPrompts(String username) {
        //TODO

    }

    @Override
    public void save() {
        // Do nothing, since Mongo automatically handles saving.
    }


}
