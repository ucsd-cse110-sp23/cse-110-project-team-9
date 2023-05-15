package sayit.frontend;

import org.json.JSONArray;
import org.json.JSONObject;
import sayit.common.qa.Answer;
import sayit.common.qa.Question;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.frontend.helpers.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A class that contains static methods to make requests to our HTTP server to access the busines logic of our project
 * </p>
 * <p>
 * Send request for GET, which should return the question history.
 * Send Request for POST/OR Put which takes sends an audio file to the server for whisper and chat GPT.
 * Send DELETE request to either delete a single entry or clear all entriews
 * </p>
 * <p>
 * The following assumptions are made for this class:
 *     <ul>
 *         <li> Calls from UI will contain endpoint and Audio File for POST or PUT exists.</li>
 *     </ui>
 * </p>
 */

public final class RequestSender {

    private final URL askQuestionUrl;
    private final URL historyUrl;
    private final URL clearHistoryUrl;
    private final URL deleteEntryUrl;

    private static RequestSender requestSender;


    private RequestSender(String host, int port) {
        try {
            this.askQuestionUrl = new URL("http://" + host + ":" + port + "/ask");
            this.historyUrl = new URL("http://" + host + ":" + port + "/history");
            this.clearHistoryUrl = new URL("http://" + host + ":" + port + "/clear-all");
            this.deleteEntryUrl = new URL("http://" + host + ":" + port + "/delete-question");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static RequestSender getInstance(String host, int port) {
        if (requestSender == null) {
            requestSender = new RequestSender(host, port);
        }
        return requestSender;
    }

    public Pair<Integer, QuestionAnswerEntry> askQuestion(File audioFile) throws IOException {
        //make connection
        HttpURLConnection connection = (HttpURLConnection) askQuestionUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.connect();

        //convert audio file into bytes
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
             FileInputStream fileInputStream = new FileInputStream(audioFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();

            // Get the response
            int responseCode = connection.getResponseCode();
            String response = getConnectionBody(connection);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Response Code: " + responseCode + ", Response: " + response);
            }

            JSONObject json = new JSONObject(response);
            return new Pair<>(
                    json.getInt("id"),
                    new QuestionAnswerEntry(
                            new Question(json.getString("question")),
                            new Answer(json.getString("answer"))
                    )
            );
        } finally {
            connection.disconnect();

        }
    }

    public Map<Integer, QuestionAnswerEntry> getHistory() throws IOException, URISyntaxException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(historyUrl.toURI())
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        JSONArray json = new JSONArray(response.body());
        HashMap<Integer, QuestionAnswerEntry> entries = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            JSONObject entry = json.getJSONObject(i);
            QuestionAnswerEntry questionAnswerEntry = new QuestionAnswerEntry(
                    new Question(entry.getString("question")),
                    new Answer(entry.getString("answer"))
            );
            int id = entry.getInt("id");
            entries.put(id, questionAnswerEntry);
        }

        return entries;
    }

    public boolean delete(int id) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(deleteEntryUrl + "?id=" + id);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        return response.body().equals("true");
    }

    public boolean clearHistory() throws IOException, URISyntaxException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(clearHistoryUrl.toURI())
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        return response.body().equals("true");
    }


    private static String getConnectionBody(HttpURLConnection connection) throws IOException {
        StringBuilder resp = new StringBuilder();
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = errorReader.readLine()) != null) {
                resp.append(inputLine);
            }
        }

        return resp.toString();
    }
}

