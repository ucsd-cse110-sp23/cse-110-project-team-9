package sayit.common;

public interface IMapper<From, To> {
    /**
     * Maps the <c>From</c> object to the <c>To</c> object.
     *
     * @param from The object to map from.
     * @return The object to map to.
     */
    To map(From from);
}
