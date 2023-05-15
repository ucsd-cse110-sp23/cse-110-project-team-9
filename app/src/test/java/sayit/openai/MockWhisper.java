package sayit.openai;

import sayit.server.openai.IWhisper;
import sayit.server.openai.OpenAiException;

import java.io.File;

public class MockWhisper implements IWhisper {
    private final boolean _shouldThrowError;
    private final String _answerOrError;

    public MockWhisper(boolean shouldThrowError, String withMsg) {
        this._shouldThrowError = shouldThrowError;
        this._answerOrError = withMsg;
    }

    @Override
    public String transcribe(File file) throws OpenAiException {
        if (this._shouldThrowError) {
            throw new OpenAiException(this._answerOrError);
        }

        return this._answerOrError;
    }
}
