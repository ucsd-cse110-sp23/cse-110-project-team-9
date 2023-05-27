package sayit.server.db.store;

import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;

import java.util.List;

/**
 * A class containing several helper methods to interact with the <c>prompts</c> TSV file.
 */
public class TsvPromptHelper implements IPromptHelper {
    private final TsvWriter<SayItPrompt> _writer;

    /**
     * Create a new <c>TsvAccountHelper</c> instance.
     *
     * @param fileName The name of the file to use.
     */
    public TsvPromptHelper(String fileName) {
        this._writer = TsvWriter.createWriter(
                List.of(SayItPrompt.USERNAME_FIELD, SayItPrompt.TIMESTAMP_FIELD,
                        SayItPrompt.TYPE_FIELD, SayItPrompt.TITLE_FIELD, SayItPrompt.RESULT_FIELD),
                fileName,
                new ITsvStrategy<>() {
                    @Override
                    public SayItPrompt parse(String[] columns) {
                        return new SayItPrompt(columns[0], Long.parseLong(columns[1]), columns[2], columns[3], columns[4]);
                    }

                    @Override
                    public String[] write(SayItPrompt obj) {
                        return new String[]{obj.getUsername(), Long.toString(obj.getTimestamp()),
                                obj.getType(), obj.getTitle(), obj.getResult()};
                    }
                });
    }

    /**
     * Gets all prompts by username.
     *
     * @param username The username to search for.
     * @return The list of prompts, or null if none exist.
     */
    @Override
    public List<SayItPrompt> getAllPromptsBy(String username) {
        return this._writer.getEntries().stream()
                .filter(prompt -> prompt.getUsername().equals(username))
                .toList();
    }

    /**
     * Inserts a new <c>SayItPrompt</c> into the TSV file.
     *
     * @param prompt The prompt to create.
     */
    @Override
    public void createPrompt(SayItPrompt prompt) {
        this._writer.addEntry(prompt);
    }

    /**
     * Saves the TSV file.
     */
    @Override
    public void save() {
        this._writer.save();
    }
}
