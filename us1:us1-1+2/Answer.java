//Author: Jimmy Zhao (jiz110@ucsd.edu)
//The Answer class contains an object that represents an answer to a question. 
//The Entry class uses this class's objects as one of its fields.


public class Answer {
    
    String text;
    
    /**
     * Default constructor with no arguments
     */
    public Answer() {
	
    }
    
    /**
     * Constructor that sets text field to parameter.
     * @param s is what the field variable text will be set to
     */
    public Answer(String s) {
	this.text = s;
    }
    
    
    /**
     * A setter method that sets the field variable text to the specified string.
     * @param text is what the field variable text will be set to
     */
    public void setAnswer(String s) {
	this.text = s;
    }
    
    /**
     * @return the Answer's field variable text
     */
    public String getAnswerText() {
	return text;
    }

}
