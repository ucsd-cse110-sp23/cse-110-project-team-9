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
     * Gets a prompt by its id.
     *
     * @param username The username of the prompt.
     * @param id       The id of the prompt.
     * @return The prompt, or null if none exists.
     */
    SayItPrompt get(String username, long id);

    /**
     * Inserts a new <c>SayItPrompt</c> into the database.
     *
     * @param prompt The prompt to create.
     */
    void createPrompt(SayItPrompt prompt);

    /**
     * Deletes a specific prompt.
     *
     * @param username The username of the prompt.
     * @param id       The id of the prompt.
     */
    boolean deletePrompt(String username, long id);

    /**
     * Deletes all prompts for a specific user.
     *
     * @param username The username.
     * @return The number of prompts deleted.
     */
    long clearAllPrompts(String username);
}
