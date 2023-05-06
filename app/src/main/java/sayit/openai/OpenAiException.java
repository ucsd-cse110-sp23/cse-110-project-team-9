package sayit.openai;

/**
 * Represents an error that occurred from either the Whisper or ChatGPT APIs.
 */
public class OpenAiException extends Throwable {
    /**
     * Creates a new instance of the <c>OpenAiException</c> with the specified error message.
     * @param errorMsg The error message, preferably from the OpenAI API response.
     */
    public OpenAiException(String errorMsg) {
        super(errorMsg);
    }
}
