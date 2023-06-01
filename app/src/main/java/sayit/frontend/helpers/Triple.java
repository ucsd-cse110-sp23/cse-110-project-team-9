package sayit.frontend.helpers;

/**
 * A class representing a triple of three objects.
 *
 * @param <A> The type of the first object.
 * @param <B> The type of the second object.
 * @param <C> The type of the third object.
 */
public class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    /**
     * Creates a new instance of the <c>Triple</c> class.
     *
     * @param first  The first object.
     * @param second The second object.
     * @param third  The third object.
     */
    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
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

    /**
     * Gets the third object.
     *
     * @return The third object.
     */
    public C getThird() {
        return this.third;
    }
}
