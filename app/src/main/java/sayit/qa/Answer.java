package sayit.qa;

/**
 * Represents an answer to a question from ChatGPT.
 */
public class Answer {
    private String _text;

    /**
     * Creates a new <c>Answer</c> object.
     *
     * @param s The answer to set.
     */
    public Answer(String s) {
        this._text = s;
    }


    /**
     * Sets the answer to the specified answer.
     *
     * @param s The answer to set.
     */
    public void setAnswer(String s) {
        this._text = s;
    }

    /**
     * Gets the answer.
     *
     * @return The answer.
     */
    public String getAnswerText() {
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
        if (obj instanceof Answer o) {
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
