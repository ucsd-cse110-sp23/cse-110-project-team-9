package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.IServer;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handles a DELETE request for clearing all questions.
 * The endpoint will be <c>/delete-question</c>.
 */
public class ClearAllHandler implements HttpHandler {
    private final IServer _server;

    /**
     * Creates a new instance of the <c>AskQuestionHandler</c> class.
     *
     * @param server The server instance
     */
    public ClearAllHandler(IServer server) {
        this._server = server;
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
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            httpExchange.close();
            return;
        }

        System.out.println("Received DELETE request for /clear-all");

        String username = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(), UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.close();
            return;
        }

        long numDeleted = this._server.getPromptDb().clearAllPrompts(username);
        this._server.getPromptDb().save();

        String result = String.valueOf(numDeleted);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, result.length());
        httpExchange.getResponseBody().write(result.getBytes());
        httpExchange.close();
    }
}
