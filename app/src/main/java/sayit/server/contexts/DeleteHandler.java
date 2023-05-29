package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.qa.InputOutputEntry;
import sayit.server.Helper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.storage.IStore;

import java.io.IOException;

/**
 * Handles a DELETE request for deleting a question.
 * The endpoint will be <c>/delete-question</c>.
 */
public class DeleteHandler implements HttpHandler {
    private final IPromptHelper pHelper;

    /**
     * Creates a new instance of the <c>AskQuestionHandler</c> class.
     *
     * @param helper The Prompt Helper to use.
     */
    public DeleteHandler(IPromptHelper helper) {
        this.pHelper = helper;
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
        String idString = Helper.getSingleQueryParameter(query, "id");
        if (idString == null) {
            System.out.println("\tInvalid query string: " + query);
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idString);
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
