package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.common.qa.ProgramOutput;
import sayit.common.qa.UserInput;
import sayit.common.qa.InputOutputEntry;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.db.mongo.MongoPromptHelper;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.IWhisper;
import sayit.server.openai.WhisperCheck;
import sayit.server.storage.IStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static sayit.server.Helper.saveAudioFile;

/**
 * Handles a request for asking a question.
 * The endpoint will be <c>/ask</c>.
 */
public class AskQuestionHandler implements HttpHandler {
    private final IPromptHelper pHelper;
    private final IWhisper whisper;
    private final IChatGpt chatGpt;

    /**
     * Creates a new instance of the <c>AskQuestionHandler</c> class.
     *
     * @param pHelper    The store to use.
     * @param whisper The <c>Whisper</c> instance to use.
     * @param chatGpt The <c>ChatGPT</c> instance to use.
     */
    public AskQuestionHandler(IPromptHelper pHelper, IWhisper whisper, IChatGpt chatGpt) {
        this.pHelper = pHelper;
        this.whisper = whisper;
        this.chatGpt = chatGpt;
    }

    /**
     * Handles the request.
     *
     * @param httpExchange the exchange containing the request from the
     *                     client and used to send the response
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // Make sure we have a POST request here
        if (!httpExchange.getRequestMethod().equals("POST")) {
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
            return;
        }

        System.out.println("Received POST request for /ask");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = httpExchange.getRequestBody();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        //get bytes from request
        byte[] audioBytes = outputStream.toByteArray();
        outputStream.close();

        // Save the audio bytes as a sound file
        String soundFilePath = saveAudioFile(audioBytes);

        // access Whisper API
        WhisperCheck whisperCheck = new WhisperCheck(this.whisper, new File(soundFilePath));

        String input = whisperCheck.output();
        String response;

        if (whisperCheck.isExceptionThrown()) {
            response = input;
            httpExchange.sendResponseHeaders(400, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.close();
            return;
        }

        if (input.toLowerCase().startsWith("question")) {
            //if audio is transcribed, pass to Chat GPT
            String answer;

            try {
                answer = this.chatGpt.askQuestion(input);
            } catch (Exception e) {
                response = "ChatGPT Error: " + e.getMessage();
                httpExchange.sendResponseHeaders(400, response.length());
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.close();
                return;
            }

            long time = System.currentTimeMillis();

            JSONObject obj = new JSONObject();
            obj.put("question", input);
            obj.put("answer", answer);
            obj.put("id", time);
            obj.put("type", "QUESTION");

            response = obj.toString();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(200, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();

            SayItPrompt prompt = new SayItPrompt()???



            return;
        }

        else if (input.toLowerCase().startsWith("delete prompt")) {

        }
    }
}
