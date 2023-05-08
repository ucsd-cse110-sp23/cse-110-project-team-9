import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;


public class EntryDisplayer {
    
    
    JTextField displayQuestion(Entry e) {
	
	String text = e.getQuestion().getQuestionText();
	return new JTextField(text);

    }
    
    public JTextField displayAnswer(Entry e) {
	
	String text = e.getAnswer().getAnswerText();
	return new JTextField(text);
    }
    
    public JList displayEntry(Entry e) {
	
	String[] text = {e.getQuestion().getQuestionText(), 
		e.getAnswer().getAnswerText()};
	return new JList(text);
    }
    
    public static void main(String[] args) {
	
	Question testQuestion = new Question("What is the best fruit?");
	Answer testAnswer = new Answer("Apple");
	Entry testEntry = new Entry(testQuestion, testAnswer);
	
	EntryDisplayer testDisplayer = new EntryDisplayer();
	JTextField text1 = testDisplayer.displayQuestion(testEntry);
	JTextField text2 = testDisplayer.displayAnswer(testEntry);
	JList list3 = testDisplayer.displayEntry(testEntry);

    }

}

