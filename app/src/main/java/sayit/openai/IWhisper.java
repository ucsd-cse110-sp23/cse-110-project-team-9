package sayit.openai;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

/**
 * An interface that represents a wrapper for OpenAI's Whisper API.
 */
public interface IWhisper {

    /**
     * Transcribes the given audio input stream into a String.
     * @param inputStream The audio input stream to transcribe.
     * @return The transcription of the audio input stream.
     * @throws IOException If an error occurs from the request to OpenAI's Whisper API.
     * @throws OpenAiException If an error occurred with the request data itself.
     */
    String transcribe(AudioInputStream inputStream) throws IOException, OpenAiException;
}
