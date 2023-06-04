package sayit.common;

/**
 * Represents an action to be executed.
 */
public interface IAction<T> {
    /**
     * Executes the action.
     */
    void execute(T data);
}
