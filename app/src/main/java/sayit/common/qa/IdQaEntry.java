package sayit.common.qa;

public class IdQaEntry extends InputOutputEntry {
    private final int _id;

    /**
     * Creates a new <c>Entry</c> object with the specified question and answer.
     *
     * @param q The question.
     * @param a The answer.
     */
    public IdQaEntry(UserInput q, ProgramOutput a) {
        super(q, a);
        this._id = -1;
    }

    /**
     * Creates a new <c>Entry</c> object with the specified id, question and answer.
     *
     * @param id The id.
     * @param q  The question.
     * @param a  The answer.
     */
    public IdQaEntry(int id, UserInput q, ProgramOutput a) {
        super(q, a);
        this._id = id;
    }

    /**
     * Gets the id.
     *
     * @return The id.
     */
    public int getId() {
        return this._id;
    }
}
