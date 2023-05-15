package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.common.qa.Answer;
import sayit.common.qa.Question;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.IWhisper;
import sayit.server.openai.WhisperCheck;
import sayit.server.storage.IStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static sayit.server.Helper.saveAudioFile;

/**
 * Handles a POST request for asking a question.
 * The endpoint will be <c>/ask</c>.
 */
public class AskQuestionHandler implements HttpHandler {
    private final IStore<QuestionAnswerEntry> data;
    private final IWhisper whisper;
    private final IChatGpt chatGpt;

    /**
     * Creates a new instance of the <c>AskQuestionHandler</c> class.
     *
     * @param data    The store to use.
     * @param whisper The <c>Whisper</c> instance to use.
     * @param chatGpt The <c>ChatGPT</c> instance to use.
     */
    public AskQuestionHandler(IStore<QuestionAnswerEntry> data, IWhisper whisper, IChatGpt chatGpt) {
        this.data = data;
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

        String question = whisperCheck.output();

        String response;
        if (whisperCheck.isExceptionThrown()) {
            response = question;
            httpExchange.sendResponseHeaders(400, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.close();
            return;
        }

        //if audio is transcribed, pass to Chat GPT
        String answer;

        try {
            answer = this.chatGpt.askQuestion(question);
        } catch (Exception e) {
            response = "ChatGPT Error: " + e.getMessage();
            httpExchange.sendResponseHeaders(400, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.close();
            return;
        }

        // Create a new question/answer pair and insert it into the database
        Question q = new Question(question);
        Answer a = new Answer(answer);
        QuestionAnswerEntry entry = new QuestionAnswerEntry(q, a);
        int newID = data.insert(entry);
        data.save();

        JSONObject obj = new JSONObject();
        obj.put("question", question);
        obj.put("answer", answer);
        obj.put("id", newID);

        response = obj.toString();
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(200, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }
}
