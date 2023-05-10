package sayit.qa;

/**
 * Represents a question asked by a user.
 */
public class Question {
    private String text;

    /**
     * Constructor that sets the question text
     *
     * @param s The question.
     */
    public Question(String s) {
        this.text = s;

    }

    /**
     * Sets the question to the specified question.
     *
     * @param s The question to set.
     */
    public void setQuestion(String s) {
        this.text = s;
    }

    /**
     * Gets the question asked by the user.
     *
     * @return The question.
     */
    public String getQuestionText() {
        return text;
    }

    /**
     * Checks if this <c>Question</c> object is equal to another object.
     *
     * @param obj The object to compare to.
     * @return <c>true</c> if the objects are equal, <c>false</c> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Question o) {
            return this.text.equals(o.text);
        }

        return false;
    }

    /**
     * Returns a string representation of this <c>Question</c> object.
     *
     * @return A string representation of this <c>Question</c> object.
     */

    @Override
    public String toString() {
        return this.text;
    }
}