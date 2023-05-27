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
}
