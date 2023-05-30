package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
        String idString = Helper.getQueryParameter(query, "id");
        String username = Helper.getQueryParameter(query, "username");

        if (idString == null) {
            System.out.println("\tInvalid query string: " + query);
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
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
        Boolean deleteSuccess = pHelper.deletePrompt(username, id);
        if (!deleteSuccess) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }
        pHelper.save();

        //TODO: Look at below, not sure if result is what it should be

        String result = String.valueOf(deleteSuccess);
        exchange.sendResponseHeaders(200, result.length());
        exchange.getResponseBody().write(result.getBytes());
        exchange.close();
    }
}
