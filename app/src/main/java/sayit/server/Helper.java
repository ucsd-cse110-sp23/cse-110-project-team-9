package sayit.server;

import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.frontend.helpers.Pair;

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
     * @param query         The query string (possibly null).
     * @param parameterName The name of the parameter to get.
     * @return The value of the parameter.
     */
    public static String getQueryParameter(String query, String parameterName) {
        if (query == null) {
            return null;
        }

        query = URLDecoder.decode(query, StandardCharsets.UTF_8);
        String[] allQueries = query.split("&");
        for (String q : allQueries) {
            if (!q.contains("=")) {
                continue;
            }

            String[] split = q.split("=");
            if (split[0].equals(parameterName)) {
                return split[1];
            }
        }

        return null;
    }

    /**
     * Extracts the username and password from a JSON object.
     *
     * @param json The JSON object to extract from.
     * @return A pair of the username and password, or null if the JSON object is invalid.
     */
    public static Pair<String, String> extractUsernamePassword(JSONObject json) {
        String username;
        String password;
        try {
            username = json.getString(UniversalConstants.USERNAME);
            password = json.getString(UniversalConstants.PASSWORD);
        } catch (Exception e) {
            return null;
        }

        return new Pair<>(username, password);
    }
}
