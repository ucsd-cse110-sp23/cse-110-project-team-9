package sayit;

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
}
