package sayit.server.storage;

import sayit.common.qa.Answer;
import sayit.common.qa.IdQaEntry;
import sayit.common.qa.Question;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.db.store.ITsvStrategy;
import sayit.server.db.store.TsvWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Represents a "database" which uses a TSV file as its storage system. This particular class is
 * designed to be used to store Question and Answer pairs.
 * </p>
 * <p>
 * To ensure that we can have the same entries in the TSV file (since the user can ask the same questions,
 * for example), we will use a unique id for each entry. This id will be used to identify the entry in the
 * TSV file. Note that, if an entry is deleted, the id will not be reused. In other words, the id will be
 * monotonically increasing.
 * </p>
 * <p>
 * The following assumptions are made for this store:
 *     <ul>
 *         <li>There is only <i>one</i> store for a given file at any given time.</li>
 *     </ui>
 * </p>
 */
@Deprecated
public class TsvStore implements IStore<QuestionAnswerEntry> {
    /**
     * The id of the next entry to be added to the TSV file.
     */
    private int _nextId;

    private final TsvWriter<IdQaEntry> _writer;

    /**
     * Creates a new TsvStore object.
     *
     * @param filename The name for the TSV file. This will automatically check if the file exists and create
     *                 it if it doesn't.
     * @throws IOException If there is an error reading or writing to the specified file.
     */
    private TsvStore(String filename) throws IOException {
        this._writer = TsvWriter.createWriter(List.of("id", "question", "answer"), filename, new ITsvStrategy<>() {
            @Override
            public IdQaEntry parse(String[] columns) {
                return new IdQaEntry(
                        Integer.parseInt(columns[0]),
                        new Question(columns[1]),
                        new Answer(columns[2]));
            }

            @Override
            public String[] write(IdQaEntry obj) {
                return new String[]{
                        Integer.toString(obj.getId()),
                        obj.getQuestion().getQuestionText(),
                        obj.getAnswer().getAnswerText()
                };
            }
        });

        assert this._writer != null;
        if (this._writer.size() == 0) {
            this._nextId = 0;
        } else {
            this._nextId = this._writer.getEntries().stream()
                    .map(IdQaEntry::getId)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
        }
    }

    /**
     * Creates a new <c>TsvStore</c> object, or opens an existing one if it already exists.
     *
     * @param filename The name of the TSV file.
     * @return A new <c>TsvStore</c> object, or <c>null</c> if there was an error creating the file.
     */
    public static TsvStore createOrOpenStore(String filename) {
        try {
            return new TsvStore(filename);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Inserts the <c>QuestionAnswerEntry</c> into the store.
     *
     * @param entry The entry to insert. The ID does not need to be set.
     * @return The id of the inserted entry.
     */
    public int insert(QuestionAnswerEntry entry) {
        int thisId = this._nextId;
        this._writer.addEntry(new IdQaEntry(thisId, entry.getQuestion(), entry.getAnswer()));
        this._nextId++;
        return thisId;
    }

    /**
     * Gets the <c>QuestionAnswerEntry</c> with the specified id.
     *
     * @param id The id of the entry to get.
     * @return The <c>QuestionAnswerEntry</c> with the specified id, or <c>null</c> if it doesn't exist.
     */
    public QuestionAnswerEntry get(int id) {
        for (IdQaEntry entry : this._writer.getEntries()) {
            if (entry.getId() == id) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Deletes the entry with the specified id.
     *
     * @param id The id of the entry to delete.
     * @return <c>true</c> if the entry was deleted, <c>false</c> otherwise.
     */
    public boolean delete(int id) {
        return this._writer.removeEntriesBy(obj -> obj.getId() == id) > 0;
    }

    /**
     * Saves the contents of the store to the file. This must be executed if you want to ensure that any changes
     * to the store are saved.
     *
     * @return <c>true</c> if the save was successful, <c>false</c> otherwise.
     */
    public boolean save() {
        return this._writer.save();
    }

    /**
     * Gets the number of entries in the store.
     *
     * @return The number of entries in the store.
     */
    public int size() {
        return this._writer.size();
    }

    /**
     * Gets all entries in the store. This returns an immutable map of the entries.
     *
     * @return An immutable map of all entries in the store.
     */
    public Map<Integer, QuestionAnswerEntry> getEntries() {
        HashMap<Integer, IdQaEntry> entries = new HashMap<>();
        for (IdQaEntry entry : this._writer.getEntries()) {
            entries.put(entry.getId(), entry);
        }

        return Map.copyOf(entries);
    }

    /**
     * Deletes the store file and clears all data from cache. This also resets the id to 0.
     *
     * @return <c>true</c> if the file was deleted, <c>false</c> otherwise.
     */
    public boolean clearAll() {
        this._nextId = 0;
        return this._writer.clearAll();
    }
}