package sayit;

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
}