package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.common.qa.InputOutputEntry;
import sayit.server.Helper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.storage.IStore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static sayit.server.ServerConstants.UNKNOWN_PROMPT_OUTPUT;

/**
 * Handles a GET request for getting the history of questions and answers.
 * The endpoint will be <c>/history</c>.
 */
public class HistoryHandler implements HttpHandler {
    private final IPromptHelper pHelper;

    /**
     * Creates a new instance of the <c>HistoryHandler</c> class.
     *
     * @param helper The Prompt Helper to use.
     */
    public HistoryHandler(IPromptHelper helper) {
        this.pHelper = helper;
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
        if (!httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
            return;
        }

        System.out.println("Received GET request for /history");

        String username = Helper.getSingleQueryParameter(httpExchange.getRequestURI().getQuery(), "username");
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            httpExchange.sendResponseHeaders(400, 0);
            httpExchange.close();
            return;
        }

        List<SayItPrompt> promptList = pHelper.getAllPromptsBy(username);
        JSONArray history = new JSONArray();
        for (var item : promptList) {
            JSONObject entry = new JSONObject();
            String type = item.getType();
            if (type.equals(UniversalConstants.QUESTION)) {
                entry.put(SayItPrompt.INPUT_FIELD, item.getInput());
                entry.put(SayItPrompt.OUTPUT_FIELD, item.getOutput());
                entry.put(UniversalConstants.ID, item.getTimestamp());
                entry.put(SayItPrompt.TYPE_FIELD, UniversalConstants.QUESTION);
            }
            else if (type.equals(UniversalConstants.DELETE_PROMPT)) {
                entry.put(SayItPrompt.TYPE_FIELD, UniversalConstants.DELETE_PROMPT);
            }
            else if (type.equals(UniversalConstants.CLEAR_ALL)) {
                entry.put(SayItPrompt.TYPE_FIELD, UniversalConstants.CLEAR_ALL);
            }
            else {
                entry.put(SayItPrompt.TYPE_FIELD, UniversalConstants.ERROR);
                entry.put(SayItPrompt.INPUT_FIELD, item.getInput());
                entry.put(SayItPrompt.OUTPUT_FIELD, UNKNOWN_PROMPT_OUTPUT);
            }
            history.put(entry);
        }

        String response = history.toString();
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(200, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }
}
