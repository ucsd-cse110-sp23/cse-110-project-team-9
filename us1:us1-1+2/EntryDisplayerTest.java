//Author: Jimmy Zhao (jiz110@ucsd.edu)
//A tester class for Answer.java, Question.java, Entry.java, and EntryDisplayer.java.

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

import org.junit.Test;


public class EntryDisplayerTest {
    
    @Test
    /**
     * Tests the Question class.
     */
    public void testQuestion() {
	String testText1 = "What is the best fruit?";
	String testText2 = "What is the smallest fruit?";
	Question testQuestion1 = new Question();
	assertNull(testQuestion1.getQuestionText());
	Question testQuestion2 = new Question(testText1);
	assertTrue(testQuestion2.getQuestionText().equals(testText1));
	testQuestion1.setQuestion(testText2);
	testQuestion2.setQuestion(testText2);
	assertTrue(testQuestion1.getQuestionText().equals(testText2));
	assertTrue(testQuestion2.getQuestionText().equals(testText2));

    }
    
    @Test
    /**
     * Tests the Answer class.
     */
    public void testAnswer() {
	String testText1 = "Apple";
	String testText2 = "Grape";
	Answer testAnswer1 = new Answer();
	assertNull(testAnswer1.getAnswerText());
	Answer testAnswer2 = new Answer(testText1);
	assertTrue(testAnswer2.getAnswerText().equals(testText1));
	testAnswer1.setAnswer(testText2);
	testAnswer2.setAnswer(testText2);
	assertTrue(testAnswer1.getAnswerText().equals(testText2));
	assertTrue(testAnswer2.getAnswerText().equals(testText2));
    }
    
    @Test
    /**
     * Tests the Entry class.
     */
    public void testEntry() {
	String testText1 = "What is the best fruit?";
	String testText2 = "Grape";
	Question testQuestion1 = new Question(testText1);
	Answer testAnswer1 = new Answer(testText2);
	Entry testEntry1 = new Entry(testQuestion1, testAnswer1);
	assertEquals(testQuestion1, testEntry1.getQuestion());
	assertEquals(testAnswer1, testEntry1.getAnswer());
    }
    
    @Test
    /**
     * Tests the EntryDisplayer class.
     */
    public void testEntryDisplayer() {
	String testText1 = "What is the best fruit?";
	String testText2 = "Grape";
	Question testQuestion1 = new Question(testText1);
	Answer testAnswer1 = new Answer(testText2);
	Entry testEntry1 = new Entry(testQuestion1, testAnswer1);
	EntryDisplayer testDisplayer1 = new EntryDisplayer();
	EntryDisplayer testDisplayer = new EntryDisplayer();
	JTextField testJText1 = testDisplayer.displayQuestion(testEntry1);
	JTextField testJText2 = testDisplayer.displayAnswer(testEntry1);
	JList list3 = testDisplayer.displayEntry(testEntry1);
	
	//Although not shown here, I tested displaying these JTexts and JLists 
	//in a previous version of the EntryDisplayer class. I used a JFrame and JPanel
	//displayed the JTexts and JList. They worked as intended.
	//I'm not sure how to properly test JTexts and JLists through asserts using
	//JUnit because an JTexts and JLists seem to act weirdly if they aren't displayed
	
	
    }
    
}
