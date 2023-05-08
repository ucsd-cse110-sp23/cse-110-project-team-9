package sayit;

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

}
