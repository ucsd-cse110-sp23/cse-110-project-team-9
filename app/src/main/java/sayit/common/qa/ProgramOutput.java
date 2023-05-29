package sayit.common.qa;

/**
 * Represents an answer to a question from ChatGPT.
 */
public class ProgramOutput {
    private String _text;

    /**
     * Creates a new <c>Answer</c> object.
     *
     * @param s The answer to set.
     */
    public ProgramOutput(String s) {
        this._text = s;
    }


    /**
     * Sets the answer to the specified answer.
     *
     * @param s The answer to set.
     */
    public void setOutput(String s) {
        this._text = s;
    }

    /**
     * Gets the answer.
     *
     * @return The answer.
     */
    public String getOutputText() {
        return this._text;
    }

    /**
     * Checks if this <c>Answer</c> object is equal to another object.
     *
     * @param obj The object to compare to.
     * @return <c>true</c> if the objects are equal, <c>false</c> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProgramOutput o) {
            return this._text.equals(o._text);
        }

        return false;
    }

    /**
     * Returns a string representation of this <c>Answer</c> object.
     *
     * @return A string representation of this <c>Answer</c> object.
     */
    @Override
    public String toString() {
        return this._text;
    }
}
