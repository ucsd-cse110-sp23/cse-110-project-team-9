//Author: Jimmy Zhao (jiz110@ucsd.edu)
//The Entry class contains an object that has two field variables: a Question
//and an Answer. 


public class Entry {

    private Question question;
    private Answer answer;
    
    /**
     * Constructor that sets the field variables to their specified values.
     * @param q the specified question
     * @param a the specified answer
     */
    public Entry(Question q, Answer a) {
	this.question = q;
	this.answer = a;
    }
    
    /**
     * @return the Question object of this Entry
     */
    public Question getQuestion() {
	return question;
    }
    
    /**
     * @return the Answer object of this Entry
     */
    public Answer getAnswer() {
	return answer;
    }
    
    
}
