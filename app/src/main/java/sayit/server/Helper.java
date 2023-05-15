package sayit.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A helper class for the server.
 */
public final class Helper {
    /**
     * Saves the audio file to the disk.
     * @param audioBytes The audio bytes to save.
     * @return The path to the saved file.
     * @throws IOException If there is an error writing to the file.
     */
    public static String saveAudioFile(byte[] audioBytes) throws IOException {
        String soundFilePath = "question.wav"; // Provide the desired file path
        try (OutputStream outStream = new FileOutputStream(soundFilePath)) {
            outStream.write(audioBytes);
        }
        return soundFilePath;
    }
}
