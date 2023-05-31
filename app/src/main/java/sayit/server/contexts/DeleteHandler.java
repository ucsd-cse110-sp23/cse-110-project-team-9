package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.db.common.IPromptHelper;

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
        String username = Helper.getQueryParameter(query, UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        String idString = Helper.getQueryParameter(query, UniversalConstants.ID);
        if (idString == null) {
            System.out.println("\tbut is invalid because no ID specified.");
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        long id;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            System.out.println("\tInvalid query string: " + query);
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        System.out.println("\twith ID: " + id);
        boolean deleteSuccess = pHelper.deletePrompt(username, id);
        pHelper.save();

        String result = String.valueOf(deleteSuccess);
        exchange.sendResponseHeaders(200, result.length());
        exchange.getResponseBody().write(result.getBytes());
        exchange.close();
    }
}
