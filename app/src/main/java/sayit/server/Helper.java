package sayit.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * A helper class for the server.
 */
public final class Helper {
    /**
     * Saves the audio file to the disk.
     *
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

    /**
     * Gets a single query parameter from a query string.
     * @param query The query string (possibly null).
     * @param parameterName The name of the parameter to get.
     * @return The value of the parameter.
     */
    public static String getSingleQueryParameter(String query, String parameterName) {
        if (query == null) {
            return null;
        }

        query = URLDecoder.decode(query, StandardCharsets.UTF_8);
        String[] querySplit = query.split("=");
        if (querySplit.length != 2 || !querySplit[0].equals(parameterName)) {
            return null;
        }
        return querySplit[1];
    }
}
