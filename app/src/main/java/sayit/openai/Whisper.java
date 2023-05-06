package sayit.openai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioInputStream;

import org.json.*;

/**
 * A simple API wrapper around OpenAI's <c>Whisper</c> API.
 */
public class Whisper implements IWhisper {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
    private static final String MODEL = "whisper-1";

    /**
     * Transcribes the given <c>AudioInputStream</c> into a <c>String</c>, using OpenAI's Whisper API.
     *
     * @param inputStream The audio input stream to transcribe.
     * @return A string representing the transcribed text. If an error occurred when attempting to get data
     * from the Whisper API, this method will instead return <c>null</c>.
     * @throws IOException If an issue occurs with the given connection.
     */
    @Override
    public String transcribe(@Nonnull AudioInputStream inputStream) throws IOException, OpenAiException {
        URL url = new URL(API_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String boundary = "Boundary-" + System.currentTimeMillis();
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Authorization", "Bearer " + 1);

        try (OutputStream outputStream = connection.getOutputStream()) {
            // Write the parameter name and value to the output stream.
            outputStream.write(("--" + boundary + "\r\n").getBytes());
            outputStream.write(
                    ("Content-Disposition: form-data; name=\"model\"\r\n\r\n").getBytes()
            );
            outputStream.write((Whisper.MODEL + "\r\n").getBytes());

            writeAudioStream(outputStream, inputStream, boundary);
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
        }

        if (connection.getResponseCode() == 200) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            JSONObject responseJson = new JSONObject(response.toString());
            return responseJson.getString("text");
        } else {
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = errorReader.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
            }

            throw new OpenAiException(errorResponse.toString());
        }
    }

    /**
     * Writes the specified audio stream (e.g., from the AudioRecorder object) to the specified output stream.
     * @param outputStream The output stream.
     * @param inputStream The input stream.
     * @param boundary The boundary.
     * @throws IOException If an error occurred with reading to or writing from the buffer.
     */
    private static void writeAudioStream(
            OutputStream outputStream,
            AudioInputStream inputStream,
            String boundary
    ) throws IOException {
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(
                ("Content-Disposition: form-data; name=\"file\"; filename=\"hello\"\r\n").getBytes()
        );

        outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
    }
}
