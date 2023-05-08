//Author: Jimmy Zhao (jiz110@ucsd.edu)
//The Question class contains an object that represents a question. 
//The Entry class uses this class's objects as one of its fields.

public class Question {
    
    String text;
    
    /**
     * Default constructor with no arguments.
     */
    public Question() {
	
    }
    
    /**
     * Constructor that sets the text field to the parameter
     * @param text is what the field variable text will be set to
     */
    public Question(String s) {
	this.text = s;
	
    }
    
    /**
     * A setter method that sets the field variable text to the specified string.
     * @param text is what the field variable text will be set to
     */ 
    public void setQuestion(String s) {
	this.text = s;
    }
    
    /**
     * @return the Question's field variable text
     */
    public String getQuestionText() {
	return text;
    }
    
    
}
