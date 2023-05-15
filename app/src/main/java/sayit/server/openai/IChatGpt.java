package sayit.server.openai;

import java.io.IOException;

public interface IChatGpt {
    /**
     * Ask a question to the ChatGPT API.
     * @param prompt The question to ask the ChatGPT API.
     * @return The response from the ChatGPT API.
     * @throws IOException If an error occurs from the request to OpenAI's ChatGPT API.
     * @throws OpenAiException If an error occurred with the request data itself.
     */
    String askQuestion(String prompt) throws IOException, OpenAiException, InterruptedException;
}
