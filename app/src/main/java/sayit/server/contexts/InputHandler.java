package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItEmailConfiguration;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.IWhisper;
import sayit.server.openai.WhisperCheck;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import static sayit.server.Helper.saveAudioFile;
import static sayit.server.ServerConstants.UNKNOWN_PROMPT_OUTPUT;

/**
 * Handles a request for asking a question.
 * The endpoint will be <c>/ask</c>.
 */
public class InputHandler implements HttpHandler {
    private final IPromptHelper pHelper;
    private final IEmailConfigurationHelper eHelper;
    private final IWhisper whisper;
    private final IChatGpt chatGpt;

    /**
     * Creates a new instance of the <c>InputHandler</c> class.
     *
     * @param pHelper The prompt helper to use.
     * @param whisper The <c>Whisper</c> instance to use.
     * @param chatGpt The <c>ChatGPT</c> instance to use.
     */
    public InputHandler(IPromptHelper pHelper, IEmailConfigurationHelper eHelper, IWhisper whisper, IChatGpt chatGpt) {
        this.pHelper = pHelper;
        this.eHelper = eHelper;
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
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            httpExchange.close();
            return;
        }

        System.out.println("Received POST request for /ask");

        // Get the username
        String username = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.close();
            return;
        }

        System.out.println("\twith username: " + username);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = httpExchange.getRequestBody();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Get bytes from request
        byte[] audioBytes = outputStream.toByteArray();
        outputStream.close();

        // Save the audio bytes as a sound file
        String soundFilePath = saveAudioFile(audioBytes);

        // Access Whisper API
        WhisperCheck whisperCheck = new WhisperCheck(this.whisper, new File(soundFilePath));

        String input = whisperCheck.output();
        String response;
        if (whisperCheck.isExceptionThrown()) {
            response = input;
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.close();
            return;
        }

        JSONObject obj = new JSONObject();
        if (input.toLowerCase().startsWith("question")) {
            if (input.toLowerCase().startsWith("question.")) {
                input = input.substring(10);
            } else {
                input = input.substring(9);
            }

            input = input.trim();

            // if audio is transcribed, pass to Chat GPT
            String answer;

            try {
                answer = this.chatGpt.askQuestion(input);
            } catch (Exception e) {
                response = "ChatGPT Error: " + e.getMessage();
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, response.length());
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.close();
                return;
            }

            long time = System.currentTimeMillis();

            obj.put(SayItPrompt.INPUT_FIELD, input);
            obj.put(SayItPrompt.OUTPUT_FIELD, answer);
            obj.put(UniversalConstants.ID, time);
            obj.put(SayItPrompt.TYPE_FIELD, UniversalConstants.QUESTION);

            SayItPrompt prompt = new SayItPrompt(username, time,
                    UniversalConstants.QUESTION, input, answer);
            this.pHelper.createPrompt(prompt);
            this.pHelper.save();
        } else if (input.toLowerCase().startsWith("delete prompt")) {
            obj.put(SayItPrompt.TYPE_FIELD, UniversalConstants.DELETE_PROMPT);
        } else if (input.toLowerCase().startsWith("clear all")) {
            obj.put(SayItPrompt.TYPE_FIELD, UniversalConstants.CLEAR_ALL);
        } else if (input.toLowerCase().startsWith("setup email")
                || input.toLowerCase().startsWith("set up email")) {
            obj.put(SayItPrompt.TYPE_FIELD, UniversalConstants.SETUP_EMAIL);
        } else if(input.toLowerCase().startsWith("create email")
                || input.toLowerCase().startsWith("create an email")){

                    input = input.trim();
        
                    // if audio is transcribed, pass to Chat GPT
                    String answer;
        
                    try {
                        answer = this.chatGpt.askQuestion(input);
                        
                    } catch (Exception e) {
                        response = "ChatGPT Error: " + e.getMessage();
                        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, response.length());
                        httpExchange.getResponseBody().write(response.getBytes());
                        httpExchange.close();
                        return;
                    }
                    int lastNL = answer.lastIndexOf('\n');
                    SayItEmailConfiguration eConfig = eHelper.getEmailConfiguration(username);
                    
                    if(eConfig == null){
                        response = "Email not setup";
                        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, response.length());
                        httpExchange.getResponseBody().write(response.getBytes());
                        httpExchange.close();
                        return;
                    }
                    String signature = eConfig.getDisplayName();
                    
                    answer = answer.substring(0, lastNL).concat("\n").concat(signature);

                    long time = System.currentTimeMillis();
                    obj.put(SayItPrompt.INPUT_FIELD, input);
                    obj.put(SayItPrompt.OUTPUT_FIELD, answer);
                    obj.put(UniversalConstants.ID, time);
                    obj.put(SayItPrompt.TYPE_FIELD, UniversalConstants.EMAIL_DRAFT);
        
                    SayItPrompt prompt = new SayItPrompt(username, time,
                            UniversalConstants.EMAIL_DRAFT, input, answer);
                    this.pHelper.createPrompt(prompt);
                    this.pHelper.save();

            response = obj.toString();
        } else if(input.toLowerCase().startsWith("send email")){
            //parse email address out of response
            input = input.substring(11);
            String toAddress = input.toLowerCase();
            toAddress = toAddress.replace(" dot ", ".");
            toAddress = toAddress.replace(" at ", "@");
            toAddress = toAddress.replace(" ", "");
            toAddress = toAddress.replace("-", "");
            if (toAddress.endsWith(".")){
                toAddress = toAddress.substring(0, toAddress.length() -1);
            }
            long time = System.currentTimeMillis();

            obj.put(SayItPrompt.INPUT_FIELD, input);
            obj.put(SayItPrompt.OUTPUT_FIELD, toAddress);
            obj.put(SayItPrompt.TYPE_FIELD, UniversalConstants.SEND_EMAIL);
            obj.put(UniversalConstants.ID, time);
            response = obj.toString();
            SayItPrompt prompt = new SayItPrompt(username, time,
                    UniversalConstants.QUESTION, input, UniversalConstants.SEND_EMAIL + ": " + toAddress);
            this.pHelper.createPrompt(prompt);
            this.pHelper.save();
            response = obj.toString();
        } else {
            obj.put(SayItPrompt.TYPE_FIELD, UniversalConstants.ERROR);
            obj.put(SayItPrompt.INPUT_FIELD, input);
            obj.put(SayItPrompt.OUTPUT_FIELD, UNKNOWN_PROMPT_OUTPUT);
        }
        
        response = obj.toString();
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }
}
