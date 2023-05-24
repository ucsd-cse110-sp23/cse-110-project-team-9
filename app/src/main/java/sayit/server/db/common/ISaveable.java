package sayit.server.db.common;

/**
 * An interface describing how to save an object to the database.
 */
public interface ISaveable {
    /**
     * Saves the object to the database.
     */
    void save();
}
