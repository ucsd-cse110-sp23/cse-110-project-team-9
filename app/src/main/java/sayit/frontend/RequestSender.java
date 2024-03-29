package sayit.frontend;

import org.json.JSONArray;
import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.common.qa.InputOutputEntry;
import sayit.common.qa.ProgramOutput;
import sayit.common.qa.UserInput;
import sayit.server.ServerConstants;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static sayit.frontend.FrontEndConstants.*;

/**
 * A class that contains static methods to make requests to our HTTP server to
 * access the business logic of our project.
 */
public final class RequestSender {

    private final URL inputUrl;
    private final URL historyUrl;
    private final URL clearHistoryUrl;
    private final URL deleteEntryUrl;
    private final URL pingUrl;
    private final URL createAccountUrl;
    private final URL checkAccountUrl;
    private final URL loginUrl;
    private final URL sendUrl;
    private final URL saveEmailConfigurationUrl;
    private final URL getEmailConfigurationUrl;

    private static RequestSender requestSender;


    private RequestSender(String host, int port) {
        try {
            this.inputUrl = new URL("http://" + host + ":" + port + "/input");
            this.historyUrl = new URL("http://" + host + ":" + port + "/history");
            this.clearHistoryUrl = new URL("http://" + host + ":" + port + "/clear-all");
            this.deleteEntryUrl = new URL("http://" + host + ":" + port + "/delete-question");
            this.pingUrl = new URL("http://" + host + ":" + port + "/ping");
            this.createAccountUrl = new URL("http://" + host + ":" + port + "/create-account");
            this.checkAccountUrl = new URL("http://" + host + ":" + port + "/check-account");
            this.loginUrl = new URL("http://" + host + ":" + port + "/login");
            this.saveEmailConfigurationUrl = new URL("http://" + host + ":" + port + "/save_email_configuration");
            this.getEmailConfigurationUrl = new URL("http://" + host + ":" + port + "/get_email_configuration");
            this.sendUrl = new URL("http://" + host + ":" + port + "/send_email");

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
            HttpResponse<String> response = sendRequest(pingUrl.toURI(), RequestType.GET, null, Duration.ofSeconds(2));
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
        parameters.put(UniversalConstants.USERNAME, username);
        parameters.put(UniversalConstants.PASSWORD, password);
        HttpResponse<String> response = sendRequest(this.createAccountUrl.toURI(), RequestType.POST, parameters, null);
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
        parameters.put(UniversalConstants.USERNAME, username);
        parameters.put(UniversalConstants.PASSWORD, password);

        HttpResponse<String> response = sendRequest(this.loginUrl.toURI(), RequestType.POST, parameters, null);

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
     * @param username  The username to use.
     * @return The result of the request.
     * @throws IOException If an error occurs while sending the request.
     */
    public InputOutputEntry sendRecording(File audioFile, String username) throws IOException {
        URL url = new URL(inputUrl + "?" +
                USERNAME_QUERY_PARAM + URLEncoder.encode(username, StandardCharsets.UTF_8));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
            String type = json.getString(UniversalConstants.TYPE);
            return switch (type) {
                case UniversalConstants.QUESTION -> new InputOutputEntry(
                        json.getLong(UniversalConstants.ID),
                        UniversalConstants.QUESTION,
                        new UserInput(json.getString(UniversalConstants.INPUT)),
                        new ProgramOutput(json.getString(UniversalConstants.OUTPUT))
                );
                case UniversalConstants.DELETE_PROMPT -> new InputOutputEntry(
                        Integer.MIN_VALUE,
                        UniversalConstants.DELETE_PROMPT,
                        null,
                        null
                );
                case UniversalConstants.CLEAR_ALL -> new InputOutputEntry(
                        Integer.MIN_VALUE,
                        UniversalConstants.CLEAR_ALL,
                        null,
                        null
                );
                case UniversalConstants.EMAIL_DRAFT -> new InputOutputEntry(
                        json.getLong(UniversalConstants.ID),
                        UniversalConstants.EMAIL_DRAFT,
                        new UserInput(json.getString(UniversalConstants.INPUT)),
                        new ProgramOutput(json.getString(UniversalConstants.OUTPUT))
                );
                case UniversalConstants.SEND_EMAIL -> new InputOutputEntry(
                        json.getLong(UniversalConstants.ID),
                        UniversalConstants.SEND_EMAIL,
                        new UserInput(json.getString(UniversalConstants.INPUT)),
                        new ProgramOutput(json.getString(UniversalConstants.OUTPUT))
                );
                case UniversalConstants.SETUP_EMAIL -> new InputOutputEntry(
                        Integer.MIN_VALUE,
                        UniversalConstants.SETUP_EMAIL,
                        null,
                        null
                );
                default -> new InputOutputEntry(
                        Integer.MIN_VALUE,
                        UniversalConstants.ERROR,
                        new UserInput(json.getString(UniversalConstants.INPUT)),
                        new ProgramOutput(json.getString(UniversalConstants.OUTPUT))
                );
            };
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
    public Map<Long, InputOutputEntry> getHistory(String username)
            throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = sendRequest(
                new URI(historyUrl + "?" +
                        USERNAME_QUERY_PARAM + URLEncoder.encode(username, StandardCharsets.UTF_8)),
                RequestType.GET,
                null,
                null
        );

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        JSONArray json = new JSONArray(response.body());
        HashMap<Long, InputOutputEntry> entries = new HashMap<>();
        for (int i = 0; i < json.length(); i++) {
            JSONObject entry = json.getJSONObject(i);
            long id = entry.getLong(UniversalConstants.ID);
            InputOutputEntry inputOutputEntry = new InputOutputEntry(
                    id,
                    entry.getString(UniversalConstants.TYPE),
                    new UserInput(entry.getString(UniversalConstants.INPUT)),
                    new ProgramOutput(entry.getString(UniversalConstants.OUTPUT))
            );
            entries.put(id, inputOutputEntry);
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
    public boolean delete(long id, String username) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(deleteEntryUrl + "?" +
                USERNAME_QUERY_PARAM + URLEncoder.encode(username, StandardCharsets.UTF_8) + "&id=" + id);
        HttpResponse<String> response = sendRequest(uri, RequestType.DELETE, null, null);

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
        URI uri = new URI(checkAccountUrl + "?" +
                USERNAME_QUERY_PARAM + URLEncoder.encode(username, StandardCharsets.UTF_8));
        HttpResponse<String> response = sendRequest(uri, RequestType.GET, null, null);

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
     * @return The number of entries deleted.
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    public long clearHistory(String username) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(clearHistoryUrl + "?"
                + USERNAME_QUERY_PARAM + URLEncoder.encode(username, StandardCharsets.UTF_8));
        HttpResponse<String> response = sendRequest(uri, RequestType.DELETE, null, null);
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        return Long.parseLong(response.body());
    }

    /**
     * Sends a POST request to create an email configuration.
     *
     * @param username      The username.
     * @param firstName     The first name.
     * @param lastName      The last name.
     * @param displayName   The display name.
     * @param email         The email.
     * @param emailPassword The email password.
     * @param smtp          The smtp port.
     * @param tls           The tls port.
     * @return <c>true</c> if the email configuration was created, <c>false</c> otherwise.
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    public boolean saveEmailConfiguration(String username, String firstName, String lastName,
                                          String displayName, String email, String emailPassword,
                                          String smtp, String tls)
            throws IOException, URISyntaxException, InterruptedException {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(UniversalConstants.USERNAME, username);
        parameters.put(UniversalConstants.FIRST_NAME, firstName);
        parameters.put(UniversalConstants.LAST_NAME, lastName);
        parameters.put(UniversalConstants.DISPLAY_NAME, displayName);
        parameters.put(UniversalConstants.EMAIL, email);
        parameters.put(UniversalConstants.EMAIL_PASSWORD, emailPassword);
        parameters.put(UniversalConstants.SMTP, smtp);
        parameters.put(UniversalConstants.TLS, tls);

        HttpResponse<String> response = sendRequest(this.saveEmailConfigurationUrl.toURI(), RequestType.POST, parameters, null);
        return response.statusCode() == HttpURLConnection.HTTP_OK;
    }

    /**
     * Sends a GET request to get an email configuration with the specified username.
     *
     * @param username The username
     * @return An email configuration with the specified username (if found)
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    public Map<String, String> getEmailConfiguration(String username)
            throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(getEmailConfigurationUrl + "?" +
                USERNAME_QUERY_PARAM + URLEncoder.encode(username, StandardCharsets.UTF_8));
        HttpResponse<String> response = sendRequest(uri, RequestType.GET, null, null);

        if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return null;
        }

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Response Code: " + response.statusCode() + ", Response: " + response.body());
        }

        JSONObject json = new JSONObject(response.body());
        Map<String, String> emailInfo = new HashMap<>();
        emailInfo.put(UniversalConstants.USERNAME, json.getString(UniversalConstants.USERNAME));
        emailInfo.put(UniversalConstants.FIRST_NAME, json.getString(UniversalConstants.FIRST_NAME));
        emailInfo.put(UniversalConstants.LAST_NAME, json.getString(UniversalConstants.LAST_NAME));
        emailInfo.put(UniversalConstants.DISPLAY_NAME, json.getString(UniversalConstants.DISPLAY_NAME));
        emailInfo.put(UniversalConstants.EMAIL, json.getString(UniversalConstants.EMAIL));
        emailInfo.put(UniversalConstants.EMAIL_PASSWORD, json.getString(UniversalConstants.EMAIL_PASSWORD));
        emailInfo.put(UniversalConstants.SMTP, json.getString(UniversalConstants.SMTP));
        emailInfo.put(UniversalConstants.TLS, json.getString(UniversalConstants.TLS));
        return emailInfo;
    }

    enum RequestType {
        GET,
        DELETE,
        POST
    }

    /**
     * Attempts to email the user based on the given ID.
     *
     * @param username  The username of the current user.
     * @param toAddress The email address to send the email to.
     * @param createID  The ID of the prompt entry with the email contents.
     * @param sendID    The ID corresponding to the prompt with the email address.
     * @return The input output entry of the email.
     * @throws IOException          If an error occurs while sending the request.
     * @throws URISyntaxException   Should never happen.
     * @throws InterruptedException If an error occurs while sending the request.`
     */
    public InputOutputEntry sendEmail(String username, String toAddress, long createID, long sendID)
            throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI(sendUrl + "?" +
                USERNAME_QUERY_PARAM + URLEncoder.encode(username, StandardCharsets.UTF_8) + "&" +
                TO_ADDRESS_QUERY_PARAM + URLEncoder.encode(toAddress, StandardCharsets.UTF_8) + "&" +
                ID_QUERY_PARAM + URLEncoder.encode(Long.toString(createID), StandardCharsets.UTF_8) + "&" +
                NEW_ID_QUERY_PARAM + URLEncoder.encode(Long.toString(sendID), StandardCharsets.UTF_8)
        );
        HttpResponse<String> response = sendRequest(uri, RequestType.POST, null, null);
        String body = response.body();
        JSONObject object = new JSONObject(body);

        boolean success = object.getBoolean(UniversalConstants.SEND_SUCCESS);
        String output;

        if (success) {
            output = object.getString(UniversalConstants.OUTPUT);
        } else {
            output = object.getString(UniversalConstants.ERROR);
        }

        return new InputOutputEntry(sendID, UniversalConstants.SEND_EMAIL,
                new UserInput("Send email to: " + toAddress),
                new ProgramOutput(output));
    }

    /**
     * Sends a request to the server.
     *
     * @param uri     The URI to send the request to.
     * @param type    The type of request to send.
     * @param body    The body of the request. This is only used if the request type is POST.
     * @param timeout The timeout for the request.
     * @return The response from the server.
     * @throws IOException          If an error occurs while sending the request.
     * @throws InterruptedException If an error occurs while sending the request.
     */
    private static HttpResponse<String> sendRequest(
            URI uri,
            RequestType type,
            Map<String, Object> body,
            Duration timeout
    ) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json");
        if (timeout != null) {
            request.timeout(timeout);
        }

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

