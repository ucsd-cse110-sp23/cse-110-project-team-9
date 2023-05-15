package sayit.server.openai;

import java.io.File;
import java.io.IOException;

/**
 * An interface that represents a wrapper for OpenAI's Whisper API.
 */
public interface IWhisper {

    /**
     * Transcribes the given audio file into a String.
     * @param file The audio file to transcribe.
     * @return The transcription of the audio file.
     * @throws IOException If an error occurs from the request to OpenAI's Whisper API.
     * @throws OpenAiException If an error occurred with the request data itself.
     */
    String transcribe(File file) throws IOException, OpenAiException;
}
