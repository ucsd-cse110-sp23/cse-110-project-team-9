package sayit.openai;

import sayit.server.openai.IChatGpt;
import sayit.server.openai.OpenAiException;

public class MockChatGpt implements IChatGpt {
    private boolean _shouldThrowError;
    private String _answerOrError;

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

    public void setValues(boolean shouldThrowError, String withMsg) {
        this._shouldThrowError = shouldThrowError;
        this._answerOrError = withMsg;
    }
}
