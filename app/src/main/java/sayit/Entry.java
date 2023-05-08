package sayit;

public class Entry {

    private Question question;
    private Answer answer;

    public Entry(Question q, Answer a) {
	this.question = q;
	this.answer = a;
    }

    public Question getQuestion() {
	return question;
    }

    public Answer getAnswer() {
	return answer;
    }


}
