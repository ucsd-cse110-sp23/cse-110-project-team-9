package sayit.server.db.store;

/**
 * An interface describing how to read from, and write to, a TSV file.
 *
 * @param <T> The type to read from and write to the TSV file.
 */
public interface ITsvStrategy<T> {

    /**
     * Parses the contents of a single row in the TSV file.
     *
     * @param columns A String array where each element represents a column in the TSV file.
     *                It assumes that any serialized newlines have been deserialized.
     * @return An object representing the contents of a single row in the TSV file.
     */
    T parse(String[] columns);

    /**
     * Produces a String array where each element represents a column in the TSV file.
     *
     * @param obj The object to serialize.
     * @return A String array where each element represents a column in the TSV file.
     * The resulting array elements do not need to be serialized.
     */
    String[] write(T obj);
}
