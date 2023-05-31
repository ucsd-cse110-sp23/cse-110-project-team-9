package sayit.frontend;

import org.json.JSONArray;
import org.json.JSONObject;
import sayit.common.qa.Answer;
import sayit.common.qa.Question;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.frontend.helpers.Pair;
import sayit.server.ServerConstants;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that contains static methods to make requests to our HTTP server to
 * access the business logic of our project.
 */
public final class RequestSender {

    private final URL askQuestionUrl;
    private final URL historyUrl;
    private final URL clearHistoryUrl;
    private final URL deleteEntryUrl;
    private final URL pingUrl;
    private final URL createAccountUrl;
    private final URL checkAccountUrl;
    private final URL loginUrl;

    private static RequestSender requestSender;


    private RequestSender(String host, int port) {
        try {
            this.askQuestionUrl = new URL("http://" + host + ":" + port + "/ask");
            this.historyUrl = new URL("http://" + host + ":" + port + "/history");
            this.clearHistoryUrl = new URL("http://" + host + ":" + port + "/clear-all");
            this.deleteEntryUrl = new URL("http://" + host + ":" + port + "/delete-question");
            this.pingUrl = new URL("http://" + host + ":" + port + "/ping");
            this.createAccountUrl = new URL("http://" + host + ":" + port + "/create-account");
            this.checkAccountUrl = new URL("http://" + host + ":" + port + "/check-account");
            this.loginUrl = new URL("http://" + host + ":" + port + "/login");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets an instance of the <c>RequestSender</c> class. It is guaranteed that
     * only one instance of this class will be created.
     *
     * @param host The host to use.
     * @param port The port to use.
     * @return An instance of the <c>RequestSender</c> class.
     */
    public static RequestSender getInstance(String host, int port) {
        if (requestSender == null) {
            requestSender = new RequestSender(host, port);
        }
        return requestSender;
    }


    /**
     * Gets an instance of the <c>RequestSender</c> class with the default host
     * and port. It is guaranteed that only one instance of this class will be created.
     *
     * @return An instance of the <c>RequestSender</c> class.
     */
    public static RequestSender getInstance() {
        if (requestSender == null) {
            requestSender = new RequestSender(ServerConstants.SERVER_HOSTNAME, ServerConstants.SERVER_PORT);
        }
        return requestSender;
    }

    /**
     * Sends a request to the server to see if it's alive.
     *
     * @return <c>true</c> if the server is alive, <c>false</c> otherwise.
     */
    public boolean isAlive() {
        try {
            HttpResponse<String> response = sendRequest(pingUrl.toURI(), RequestType.GET, null);
            return response.statusCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Sends a POST request to create an account.
     *
     * @param username The username to use.
     * @param password The password to use.
     * @return <c>true</c> if the account was created, <c>false</c> otherwise.
     * @throws IOException        If an error occurs while sending the request.
     * @throws URISyntaxException If an error occurs while sending the request.
     */
    public boolean createAccount(String username, String password)
            throws IOException, URISyntaxException, InterruptedException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        HttpResponse<String> response = sendRequest(this.createAccountUrl.toURI(), RequestType.POST, parameters);
        return response.statusCode() == HttpURLConnection.HTTP_OK;
    }

    /**
     * Sends a POST request to log into an account.
     *
     * @param username The username to use.
     * @param password The password to use.
     * @return <c>true</c> if the account and password is correct, <c>false</c> otherwise.
     * @throws IOException        If an error occurs while sending the request.
     * @throws URISyntaxException If an error occurs while sending the request.
     */
    public boolean login(String username, String password)
            throws IOException, URISyntaxException, InterruptedException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        
        HttpResponse<String> response = sendRequest(this.loginUrl.toURI(), RequestType.POST, parameters);

        return response.statusCode() == HttpURLConnection.HTTP_OK;
    }

    /**
     * <p>
     * Sends a request to the server to handle a voice prompt
     * </p>
     * <p>
     * It is assumed that the server is up.
     * </p>
     *
     * @param audioFile The audio file to send.
     * @return A pair with the first item being the ID and the second item being the question and the answer.
     * @throws IOException If an error occurs while sending the request.
     */
    public Pair<Integer, QuestionAnswerEntry> sendRecording(File audioFile) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) askQuestionUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.connect();

        // Convert audio file into bytes
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

            //TODO: change how response is handled because it may not be an entry
            //this will need to be changed from pair to a more general parsing of the returned server JSON
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

    /**
     * <p>
     * Sends a request to the server to get the history of questions and answers.
     * </p>
     * <p>
     * It is assumed that the server is up.
     * </p>
     *
     * @return A map with the ID as the key and the question and answer as the value.
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    public Map<Integer, QuestionAnswerEntry> getHistory() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = sendRequest(historyUrl.toURI(), RequestType.GET, null);
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

    /**
     * <p>
     * Sends a request to the server to delete a question and answer entry.
     * </p>
     * <p>
     * It is assumed that the server is up.
     * </p>
     *
     * @param id The ID of the entry to delete.
     * @return True if the entry was deleted, false otherwise.
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    public boolean delete(int id) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(deleteEntryUrl + "?id=" + id);
        HttpResponse<String> response = sendRequest(uri, RequestType.DELETE, null);

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        return response.body().equals("true");
    }

    /**
     * Checks if the username is already taken.
     *
     * @param username The username to check.
     * @return True if the username is taken, false otherwise.
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    public boolean doesAccountExist(String username) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(checkAccountUrl + "?username=" + username);
        HttpResponse<String> response = sendRequest(uri, RequestType.GET, null);

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        return response.body().equals("true");
    }

    /**
     * <p>
     * Sends a request to the server to clear the history of questions and answers.
     * </p>
     * <p>
     * It is assumed that the server is up.
     * </p>
     *
     * @return True if the history was cleared, false otherwise.
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    public boolean clearHistory() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = sendRequest(clearHistoryUrl.toURI(), RequestType.DELETE, null);
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        return response.body().equals("true");
    }

    enum RequestType {
        GET,
        DELETE,
        POST
    }

    /**
     * Sends a request to the server.
     *
     * @param uri  The URI to send the request to.
     * @param type The type of request to send.
     * @param body The body of the request. This is only used if the request type is POST.
     * @return The response from the server.
     * @throws IOException          If an error occurs while sending the request.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    private static HttpResponse<String> sendRequest(
            URI uri,
            RequestType type,
            Map<String, Object> body
    ) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json");

        switch (type) {
            case GET -> request.GET();
            case DELETE -> request.DELETE();
            case POST -> {
                if (body == null) {
                    request.POST(HttpRequest.BodyPublishers.noBody());
                } else {
                    request.POST(HttpRequest.BodyPublishers.ofString(new JSONObject(body).toString()));
                }
            }
        }

        return client.send(
                request.build(),
                HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Gets the body of the connection.
     *
     * @param connection The connection to get the body of.
     * @return The body of the connection.
     * @throws IOException If an error occurs while getting the body.
     */
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

