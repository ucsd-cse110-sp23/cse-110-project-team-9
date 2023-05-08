package sayit;

import javax.swing.*;


public class EntryDisplayer {


    public JTextArea displayQuestion(QuestionAnswerEntry e) {

        String text = e.getQuestion().getQuestionText();
        JTextArea question = new JTextArea(text);
        question.setEditable(false);
        return question;
    }

    public JTextArea displayAnswer(QuestionAnswerEntry e) {

        String text = e.getAnswer().getAnswerText();
        JTextArea answer = new JTextArea(text);
        answer.setEditable(false);
        return answer;

    }

    public JList<String> displayEntry(QuestionAnswerEntry e) {

        String[] text = {e.getQuestion().getQuestionText(),
                e.getAnswer().getAnswerText()};
        return new JList<>(text);
    }

    public static void main(String[] args) {

        Question testQuestion = new Question("What is the best fruit?");
        Answer testAnswer = new Answer("Apple");
        QuestionAnswerEntry testEntry = new QuestionAnswerEntry(testQuestion, testAnswer);

        EntryDisplayer testDisplayer = new EntryDisplayer();
        JTextArea text1 = testDisplayer.displayQuestion(testEntry);
        JTextArea text2 = testDisplayer.displayAnswer(testEntry);
        JList list3 = testDisplayer.displayEntry(testEntry);

    }

}
