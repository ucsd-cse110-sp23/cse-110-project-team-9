package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.storage.IStore;

import java.io.IOException;

/**
 * Handles a DELETE request for deleting a question.
 * The endpoint will be <c>/delete-question</c>.
 */
public class DeleteHandler implements HttpHandler {
    private final IStore<QuestionAnswerEntry> data;

    /**
     * Creates a new instance of the <c>AskQuestionHandler</c> class.
     *
     * @param data The store to use.
     */
    public DeleteHandler(IStore<QuestionAnswerEntry> data) {
        this.data = data;
    }

    /**
     * Handles the request.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("DELETE")) {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }

        System.out.println("Received DELETE request for /delete-question");

        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            System.out.println("\tNo query string found.");
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        String[] querySplit = query.split("=");
        if (querySplit.length != 2 || !querySplit[0].equals("id")) {
            System.out.println("\tInvalid query string: " + query);
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(querySplit[1]);
        } catch (NumberFormatException e) {
            System.out.println("\tInvalid query string: " + query);
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        System.out.println("\twith ID: " + id);
        if (!data.getEntries().containsKey(id)) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }

        String result = String.valueOf(data.delete(id));
        data.save();
        exchange.sendResponseHeaders(200, result.length());
        exchange.getResponseBody().write(result.getBytes());
        exchange.close();
    }
}
