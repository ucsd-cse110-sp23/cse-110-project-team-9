package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import sayit.common.qa.InputOutputEntry;
import sayit.server.db.common.IPromptHelper;
import sayit.server.storage.IStore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

        JSONArray history = new JSONArray();
        for (var item : data.getEntries().entrySet()) {
            JSONObject entry = new JSONObject();
            entry.put("question", item.getValue().getInput().getInputText());
            entry.put("answer", item.getValue().getOutput().getOutputText());
            entry.put("id", item.getKey());
            history.put(entry);
        }

        String response = history.toString();
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(200, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }
}
