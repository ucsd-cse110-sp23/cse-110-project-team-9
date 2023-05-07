//Author: Jimmy Zhao (jiz110@ucsd.edu)
//The EntryDisplayer class contains an object that returns an Entry object's
//Question and Answer object in JTextField (or JList if returned together).
//I know these method names aren't the best but I don't want to change them because I 
//have already merged my work on Github before so I don;t want to mess up anyone's 
//work that uses my method calls.


import javax.swing.*;



public class EntryDisplayer {
    
    /**
     * For the given entry, return the JTextField of its Question field.
     * @param e is the specified entry
     * @return the JTextField of the entry's Question field
     */
    public JTextField displayQuestion(Entry e) {
	
	String text = e.getQuestion().getQuestionText();
	return new JTextField(text);

    }
    /**
     * For the given entry, return the JTextField of its Answer field.
     * @param e is the specified entry
     * @return the JTextField of the entry's Answer field
     */    
    public JTextField displayAnswer(Entry e) {
	
	String text = e.getAnswer().getAnswerText();
	return new JTextField(text);
    }

    /**
     * For the given entry, return a JList comprising of the Entry's Question
     * and Answer field.
     * @param e is the specified entry
     * @return the JList of the entry's Question and Answer field
     */    
    public JList displayEntry(Entry e) {
	
	String[] text = {e.getQuestion().getQuestionText(), 
		e.getAnswer().getAnswerText()};
	return new JList(text);
    }

}

