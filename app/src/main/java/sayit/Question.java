package sayit;

public class Question {

    String text;

    /**
     * Default constructor with no arguments.
     */
    public Question() {

    }

    /**
     * Constructor that sets the question text
     *
     * @param s The question.
     */
    public Question(String s) {
        this.text = s;

    }

    public void setQuestion(String s) {
        this.text = s;
    }

    public String getQuestionText() {
        return text;
    }


}