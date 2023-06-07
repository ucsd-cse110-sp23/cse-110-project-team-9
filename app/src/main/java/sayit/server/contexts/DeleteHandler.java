package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.IServer;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handles a DELETE request for deleting a question.
 * The endpoint will be <c>/delete-question</c>.
 */
public class DeleteHandler implements HttpHandler {
    private final IServer _server;

    /**
     * Creates a new instance of the <c>AskQuestionHandler</c> class.
     *
     * @param server The server instance
     */
    public DeleteHandler(IServer server) {
        this._server = server;
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
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            exchange.close();
            return;
        }

        System.out.println("Received DELETE request for /delete-question");

        String query = exchange.getRequestURI().getQuery();
        String username = Helper.getQueryParameter(query, UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.close();
            return;
        }

        String idString = Helper.getQueryParameter(query, UniversalConstants.ID);
        if (idString == null) {
            System.out.println("\tbut is invalid because no ID specified.");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.close();
            return;
        }

        long id;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            System.out.println("\tInvalid query string: " + query);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.close();
            return;
        }

        System.out.println("\twith ID: " + id);
        boolean deleteSuccess = this._server.getPromptDb().deletePrompt(username, id);
        this._server.getPromptDb().save();

        String result = String.valueOf(deleteSuccess);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, result.length());
        exchange.getResponseBody().write(result.getBytes());
        exchange.close();
    }
}
