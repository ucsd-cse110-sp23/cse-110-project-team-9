package sayit.frontend.helpers;

/**
 * A class representing a pair of two objects.
 *
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 */
public class Pair<A, B> {
    private final A first;
    private final B second;

    /**
     * Creates a new instance of the <c>Pair</c> class.
     *
     * @param first  The first object.
     * @param second The second object.
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first object.
     *
     * @return The first object.
     */
    public A getFirst() {
        return this.first;
    }

    /**
     * Gets the second object.
     *
     * @return The second object.
     */
    public B getSecond() {
        return this.second;
    }
}
