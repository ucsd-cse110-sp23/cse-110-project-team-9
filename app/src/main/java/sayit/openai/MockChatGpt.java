package sayit.openai;

public class MockChatGpt implements IChatGpt {
    private final boolean _shouldThrowError;

    public MockChatGpt(boolean shouldThrowError) {
        this._shouldThrowError = shouldThrowError;
    }

    @Override
    public String askQuestion(String prompt) throws OpenAiException {
        if (this._shouldThrowError) {
            throw new OpenAiException("some error");
        }

        return "some response";
    }
}
