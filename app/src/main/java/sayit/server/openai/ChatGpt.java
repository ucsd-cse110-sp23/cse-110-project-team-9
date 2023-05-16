package sayit.server.openai;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A class designed to handle ChatGPT requests.
 */
public class ChatGpt implements IChatGpt {
    private static final URI API_URI;

    static {
        try {
            API_URI = new URI("https://api.openai.com/v1/completions");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String MODEL = "text-davinci-003";
    // https://platform.openai.com/docs/models/gpt-3-5
    // See max tokens.
    private static final int DEFAULT_MAX_TOKENS = 2048;


    private final int _maxTokens;
    private final String _apiKey;
    private double _temperature;

    /**
     * Creates a new instance of the <c>ChatGpt</c> class with the specified API key and default token.
     *
     * @param apiKey The API key to use for the ChatGPT API.
     */
    public ChatGpt(String apiKey) {
        this(apiKey, DEFAULT_MAX_TOKENS);
    }

    /**
     * Creates a new instance of the <c>ChatGpt</c> class with the specified API key and token.
     *
     * @param apiKey    The API key to use for the ChatGPT API.
     * @param maxTokens The maximum number of tokens to return.
     */
    public ChatGpt(String apiKey, int maxTokens) {
        this._apiKey = apiKey;
        this._maxTokens = maxTokens;
        this._temperature = 1.0;
    }

    /**
     * Sets the temperature for the ChatGPT response. The temperature is a value between 0 and 1, where 0 is the most
     * conservative (more focused) and 1 is the most creative (more random).
     *
     * @param temp The temperature to set. This value must be between 0 and 1.
     * @throws IllegalArgumentException If the temperature is not between 0 and 1.
     */
    public void setTemperature(double temp) {
        if (temp < 0.0 || temp > 1.0) {
            throw new IllegalArgumentException("The temperature must be between 0 and 1.");
        }

        this._temperature = temp;
    }

    /**
     * Ask a question to the ChatGPT API.
     *
     * @param prompt The question to ask the ChatGPT API.
     * @return The response from the ChatGPT API.
     * @throws IOException     If an error occurs from the request to OpenAI's ChatGPT API.
     * @throws OpenAiException If an error occurred with the request data itself.
     */
    @Override
    public String askQuestion(String prompt) throws IOException, OpenAiException, InterruptedException {
        JSONObject json = new JSONObject();
        json.put("prompt", prompt);
        json.put("model", MODEL);
        json.put("max_tokens", this._maxTokens);
        json.put("temperature", this._temperature);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(API_URI)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this._apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject responseJson = new JSONObject(response.body());
            return responseJson.getJSONArray("choices").getJSONObject(0).getString("text");
        } else {
            throw new OpenAiException(response.body());
        }
    }
}
