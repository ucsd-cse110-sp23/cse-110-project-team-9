package sayit.openai;

import java.io.File;

/**
 * A wrapper class around an <c>IWhisper</c> instance, which is designed to handle
 * possible errors sent from the API or while transcribing.
 */
public class WhisperCheck {
    private final IWhisper instance;
    private final File input;
    private boolean isExceptionThrown;

    /**
     * Creates a new <c>WhisperCheck</c> object with the specified <c>IWhisper</c> instance
     * and audio file.
     *
     * @param w The <c>IWhisper</c> instance to use.
     * @param i The file containing the audio.
     */
    public WhisperCheck(IWhisper w, File i) {
        this.instance = w;
        this.input = i;
        this.isExceptionThrown = false;
    }

    /**
     * Gets whether an exception was thrown.
     *
     * @return Whether an exception was thrown.
     */
    public boolean isExceptionThrown() {
        return this.isExceptionThrown;
    }

    /**
     * Transcribes the input stream provided in the constructor.
     *
     * @return The transcription, if any, or the error message.
     */
    public String output() {
        try {
            return instance.transcribe(input);
        } catch (Exception e) {
            this.isExceptionThrown = true;
            return e.getMessage();
        }
    }

}
