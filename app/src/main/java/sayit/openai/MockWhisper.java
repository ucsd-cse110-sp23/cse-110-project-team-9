package sayit.openai;

import java.io.InputStream;

public class MockWhisper implements IWhisper {
    private final boolean _shouldThrowError;
    private final String _answerOrError;

    public MockWhisper(boolean shouldThrowError, String withMsg) {
        this._shouldThrowError = shouldThrowError;
        this._answerOrError = withMsg;
    }

    @Override
    public String transcribe(InputStream inputStream) throws OpenAiException {
        if (this._shouldThrowError) {
            throw new OpenAiException(this._answerOrError);
        }

        return this._answerOrError;
    }
}
