package sayit.openai;

import javax.sound.sampled.AudioInputStream;

public class MockWhisper implements IWhisper {
    private final boolean _shouldThrowError;

    public MockWhisper(boolean shouldThrowError) {
        this._shouldThrowError = shouldThrowError;
    }

    @Override
    public String transcribe(AudioInputStream inputStream) throws OpenAiException {
        if (this._shouldThrowError) {
            throw new OpenAiException("test error here");
        }

        return "Welcome to CSE 12 Pre-Recorded Lecture";
    }
}
