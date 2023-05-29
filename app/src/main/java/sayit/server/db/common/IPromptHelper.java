package sayit.server.db.common;

import sayit.server.db.doctypes.SayItPrompt;

import java.util.List;

/**
 * An interface containing several helper methods to interact with the <c>prompts</c> collection.
 */
public interface IPromptHelper extends ISaveable {
    /**
     * Gets all prompts for a particular user.
     *
     * @param username The username to search for.
     * @return A list of prompts.
     */
    List<SayItPrompt> getAllPromptsBy(String username);

    /**
     * Inserts a new <c>SayItPrompt</c> into the database.
     *
     * @param prompt The prompt to create.
     */
    void createPrompt(SayItPrompt prompt);

    /**
     * Deletes a specific prompt.
     *
     * @param username The username of the prompt
     * @param timestamp The timestamp of the prompt
     * @param type The type of prompt
     * @param title The title of the prompt
     * @param result The result of the prompt
     */
    void deletePrompt(String username, long timestamp, String type, String title, String result);

    /**
     * Deletes all prompts for a specific user.
     *
     * @param username The username
     */
    void clearAllPrompts(String username);
}
