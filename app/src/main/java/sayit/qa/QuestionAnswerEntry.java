package sayit.qa;

/**
 * Represents a question, answer entry.
 */
public class QuestionAnswerEntry {
    private final Question question;
    private final Answer answer;

    /**
     * Creates a new <c>Entry</c> object with the specified question and answer.
     *
     * @param q The question.
     * @param a The answer.
     */
    public QuestionAnswerEntry(Question q, Answer a) {
        this.question = q;
        this.answer = a;
    }

    /**
     * Gets the question.
     *
     * @return The question.
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Gets the answer.
     *
     * @return The answer.
     */
    public Answer getAnswer() {
        return answer;
    }

    /**
     * Checks if this <c>Entry</c> object is equal to another object.
     *
     * @param obj The object to compare to.
     * @return <c>true</c> if the objects are equal, <c>false</c> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuestionAnswerEntry o) {
            return this.question.equals(o.question) && this.answer.equals(o.answer);
        }

        return false;
    }

    /**
     * Returns a string representation of this <c>QuestionAnswerEntry</c> object.
     *
     * @return A string representation of this <c>QuestionAnswerEntry</c> object.
     */
    @Override
    public String toString() {
        return "Q: " + this.question + " / A: " + this.answer;
    }
}
