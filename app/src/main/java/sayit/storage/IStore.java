package sayit.storage;

import java.util.Map;

/**
 * <p>
 * An interface that represents some arbitrary storage system.
 * </p>
 * <p>
 * Some assumptions are made about any class that implements this interface:
 *     <ul>
 *          <li>
 *              Entries are mutable after adding them to the store. In other words, you
 *              should not be able to modify an entry after adding it to the store.
 *          </li>
 *          <li>
 *              Entries are associated with a unique id. This id is used to identify the entry
 *              in the store. In particular, this means that you can have multiple entries with
 *              the same value, but they will have different ids.
 *          </li>
 *     </ul>
 * </p>
 * <p>
 * Some examples of classes that can implement this interface include:
 *    <ul>
 *        <li>A database (in this case, each method here represents a call to the database).</li>
 *        <li>A TSV file</li>
 *        <li>A JSON file</li>
 *        <li>A CSV file</li>
 *    </ul>
 * </p>
 *
 * @param <T> The type of entry that is stored.
 */
public interface IStore<T> {
    /**
     * Inserts the specified entry into the store.
     *
     * @param entry The entry to insert.
     * @return The id of the inserted entry.
     */
    int insert(T entry);

    /**
     * Gets the entry with the specified id.
     *
     * @param id The id of the entry to get.
     * @return The entry with the specified id, or <c>null</c> if it doesn't exist.
     */
    T get(int id);

    /**
     * Deletes the entry with the specified id.
     *
     * @param id The id of the entry to delete.
     * @return <c>true</c> if the entry was deleted, <c>false</c> otherwise.
     */
    boolean delete(int id);

    /**
     * Gets the number of entries in the store.
     *
     * @return The number of entries in the store.
     */
    int size();

    /**
     * <p>
     * Saves the contents of the store to the underlying storage system. Note that
     * the implementation of this method depends on the underlying system.
     * </p>
     * <p>
     * For example,
     * <ul>
     *     <li>
     *         in a file-based store, it's recommended that you reduce the number of
     *         write operations to a file (especially if this is done constantly). Thus,
     *         this method should be implemented such that it writes any changes to the
     *         file.
     *     </li>
     *     <li>
     *         In a database like SQLite, you probably do not need to do anything other
     *         than returning <c>true</c>, since saving is done automatically on a general
     *         write operation.
     *     </li>
     * </p>
     *
     * @return <c>true</c> if the save was successful, <c>false</c> otherwise.
     */
    boolean save();

    /**
     * Gets the entries in the store.
     *
     * @return The entries in the store.
     */
    Map<Integer, T> getEntries();

    /**
     * Clears all entries in the store.
     *
     * @return <c>true</c> if the entries were cleared, <c>false</c> otherwise.
     */
    boolean clearAll();
}
