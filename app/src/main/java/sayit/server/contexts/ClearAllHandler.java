package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.storage.IStore;

import java.io.IOException;

/**
 * Handles a DELETE request for clearing all questions.
 * The endpoint will be <c>/delete-question</c>.
 */
public class ClearAllHandler implements HttpHandler {
    private final IStore<QuestionAnswerEntry> data;

    /**
     * Creates a new instance of the <c>AskQuestionHandler</c> class.
     *
     * @param data The store to use.
     */
    public ClearAllHandler(IStore<QuestionAnswerEntry> data) {
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
        if (!httpExchange.getRequestMethod().equals("DELETE")) {
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
            return;
        }

        System.out.println("Received DELETE request for /clear-all");

        String result = String.valueOf(data.clearAll());
        data.save();
        httpExchange.sendResponseHeaders(200, result.length());
        httpExchange.getResponseBody().write(result.getBytes());
        httpExchange.close();
    }
}
