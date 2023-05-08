package sayit.openai;

public class MockChatGpt implements IChatGpt {
    private final boolean _shouldThrowError;
    private final String _answerOrError;

    public MockChatGpt(boolean shouldThrowError, String withMsg) {
        this._shouldThrowError = shouldThrowError;
        this._answerOrError = withMsg;
    }

    @Override
    public String askQuestion(String prompt) throws OpenAiException {
        if (this._shouldThrowError) {
            throw new OpenAiException(this._answerOrError);
        }

        return this._answerOrError;
    }
}
