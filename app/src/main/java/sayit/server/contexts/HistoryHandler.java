package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.storage.IStore;

import java.io.IOException;

/**
 * Handles a GET request for getting the history of questions and answers.
 * The endpoint will be <c>/history</c>.
 */
public class HistoryHandler implements HttpHandler {
    private final IStore<QuestionAnswerEntry> data;

    /**
     * Creates a new instance of the <c>HistoryHandler</c> class.
     *
     * @param data The store to use.
     */
    public HistoryHandler(IStore<QuestionAnswerEntry> data) {
        this.data = data;
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

        JSONArray history = new JSONArray();
        for (var item : data.getEntries().entrySet()) {
            JSONObject entry = new JSONObject();
            entry.put("question", item.getValue().getQuestion().getQuestionText());
            entry.put("answer", item.getValue().getAnswer().getAnswerText());
            entry.put("id", item.getKey());
            history.put(entry);
        }

        httpExchange.sendResponseHeaders(200, history.toString().length());
        httpExchange.getResponseBody().write(history.toString().getBytes());
        httpExchange.close();
    }
}
