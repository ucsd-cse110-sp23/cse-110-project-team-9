package sayit;

public class Answer {

    String text;

    /**
     * Default constructor with no arguments
     */
    public Answer() {

    }

    /**
     * Constructor that sets text field to parameter
     * @param text
     */
    public Answer(String s) {
	this.text = s;
    }


    /**
     * Constructor that sets answer text to parameter
     * @param text
     */
    public void setAnswer(String s) {
	this.text = s;
    }

    public String getAnswerText() {
	return text;
    }

}
