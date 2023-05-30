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
     * Gets a  query parameter from a query string. This method assumes there are at most two "="
     * in the query.
     *
     * @param query The query string (possibly null).
     * @param parameterName The name of the parameter to get.
     * @return The value of the parameter.
     */
    public static String getQueryParameter(String query, String parameterName) {
        if (query == null) {
            return null;
        }

        query = URLDecoder.decode(query, StandardCharsets.UTF_8);
        if (!query.contains("&")) { //only one "="
            String[] querySplit = query.split("=");
            if (querySplit.length == 2 && querySplit[0].equals(parameterName)) {
                return querySplit[1];
            } else {
                return null;
            }
        } else { //two "="
            String[] querySplit = query.split("&");
            if (querySplit[0].contains(parameterName)) {
                String[] splitSplit = querySplit[0].split("=");
                if (splitSplit[0].substring(1).equals(parameterName)) { // substring(1) used because of the "?"
                    return splitSplit[1];
                } else {
                    return null;
                }
            } else if (querySplit[1].contains(parameterName)) {
                String[] splitSplit = querySplit[1].split("=");
                if (splitSplit[0].equals(parameterName)) {
                    return splitSplit[1];
                } else {
                    return null;
                }
            }
            else {
                return null; // parameterName not found
            }
        }

    }
}
