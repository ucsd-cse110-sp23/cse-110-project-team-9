package sayit.storage;

import sayit.qa.Answer;
import sayit.qa.Question;
import sayit.qa.QuestionAnswerEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
public class TsvStore implements IStore<QuestionAnswerEntry> {
    private static final int NUM_COLUMNS = 3;
    private static final String TSV_HEADER = "id\tquestion\tanswer";
    private static final String DELIMITER = "\t";

    /**
     * The name of the TSV file.
     */
    private final String _filename;

    /**
     * The id of the next entry to be added to the TSV file.
     */
    private int _nextId;

    /**
     * The entries in the TSV file. The key is the id of the entry, and the value is the <c>QuestionAnswerEntry</c>.
     * This is acting as a "cache" of some sorts, so we don't have to constantly read from the file.
     */
    private final HashMap<Integer, QuestionAnswerEntry> _entries;

    /**
     * Creates a new TsvStore object.
     *
     * @param filename The name for the TSV file. This will automatically check if the file exists and create
     *                 it if it doesn't.
     * @throws IOException If there is an error reading or writing to the specified file.
     */
    private TsvStore(String filename) throws IOException {
        this._filename = filename.endsWith(".tsv") ? filename : filename + ".tsv";
        this._nextId = 0;
        this._entries = new HashMap<>();

        // Check if the file exists, and create it if it doesn't.
        File file = new File(this._filename);
        // If the file exists, read the contents and set the id to the last id in the file.
        if (file.exists()) {
            List<String> lines = Files.readAllLines(file.toPath());
            int maxId = 0;
            if (lines.size() > 1) {
                // Skip the first line (i = 0) since that's the CSV header.
                for (int i = 1; i < lines.size(); ++i) {
                    String[] columns = lines.get(i).split(DELIMITER);
                    if (columns.length != NUM_COLUMNS) {
                        continue;
                    }

                    int id;
                    // If the id is not a number, skip this line.
                    // This should not happen, but just in case something does happen, we don't want to crash the program.
                    try {
                        id = Integer.parseInt(columns[0]);
                    } catch (Exception e) {
                        continue;
                    }

                    maxId = Math.max(id, maxId);
                    String question = columns[1];
                    String answer = columns[2];

                    this._entries.put(id, new QuestionAnswerEntry(new Question(question), new Answer(answer)));
                }

                this._nextId = maxId + 1;
            } else {
                this._nextId = 0;
            }
        } else if (file.createNewFile()) {
            // If the file doesn't exist, create it and write the header.
            Files.write(file.toPath(), TSV_HEADER.getBytes());
        } else {
            throw new IOException("Unable to create file " + this._filename);
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
     * @param entry The entry to insert.
     * @return The id of the inserted entry.
     */
    public int insert(QuestionAnswerEntry entry) {
        int thisId = this._nextId;
        this._entries.put(thisId, entry);
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
        return this._entries.get(id);
    }

    /**
     * Deletes the entry with the specified id.
     *
     * @param id The id of the entry to delete.
     * @return <c>true</c> if the entry was deleted, <c>false</c> otherwise.
     */
    public boolean delete(int id) {
        return this._entries.remove(id) != null;
    }

    /**
     * Saves the contents of the store to the file. This must be executed if you want to ensure that any changes
     * to the store are saved.
     *
     * @return <c>true</c> if the save was successful, <c>false</c> otherwise.
     */
    public boolean save() {
        try {
            File file = new File(this._filename);
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(TSV_HEADER + System.lineSeparator());
                for (var entry : this._entries.entrySet()) {
                    writer.write(entry.getKey()
                            + DELIMITER
                            + entry.getValue().getQuestion().getQuestionText()
                            + DELIMITER
                            + entry.getValue().getAnswer().getAnswerText()
                            + System.lineSeparator());
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the number of entries in the store.
     *
     * @return The number of entries in the store.
     */
    public int size() {
        return this._entries.size();
    }

    /**
     * Gets all entries in the store. This returns an immutable map of the entries.
     *
     * @return An immutable map of all entries in the store.
     */
    public Map<Integer, QuestionAnswerEntry> getEntries() {
        return Map.copyOf(this._entries);
    }

    /**
     * Deletes the store file and clears all data from cache. This also resets the id to 0.
     *
     * @return <c>true</c> if the file was deleted, <c>false</c> otherwise.
     */
    public boolean clearAll() {
        this._entries.clear();
        this._nextId = 0;
        File file = new File(this._filename);
        return file.delete();
    }
}
