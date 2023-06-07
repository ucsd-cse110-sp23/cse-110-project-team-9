package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.IServer;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handles a GET request for checking if an account exists.
 * The endpoint will be <c>/check-account</c>.
 */
public class CheckAccountExistHandler  implements HttpHandler {
    private final IServer _server;

    /**
     * Creates a new instance of the <c>CheckAccountExistHandler</c> class.
     *
     * @param server the server instance
     */
    public CheckAccountExistHandler(IServer server) {
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
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            exchange.close();
            return;
        }

        System.out.println("Received POST request for /check-account");
        String query = exchange.getRequestURI().getQuery();
        String username = Helper.getQueryParameter(query, UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\twith invalid query: " + query);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.close();
            return;
        }

        System.out.println("\twith username: " + username);
        String response = String.valueOf(this._server.getAccountDb().getAccount(username) != null);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
