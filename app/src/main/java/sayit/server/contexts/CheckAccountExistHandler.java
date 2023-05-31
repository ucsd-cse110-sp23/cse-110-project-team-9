package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.db.common.IAccountHelper;

import java.io.IOException;

/**
 * Handles a GET request for checking if an account exists.
 * The endpoint will be <c>/check-account</c>.
 */
public class CheckAccountExistHandler  implements HttpHandler {
    private final IAccountHelper _accountHelper;

    /**
     * Creates a new instance of the <c>CheckAccountExistHandler</c> class.
     *
     * @param accountHelper The account helper to use.
     */
    public CheckAccountExistHandler(IAccountHelper accountHelper) {
        this._accountHelper = accountHelper;
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
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }

        System.out.println("Received POST request for /check-account");
        String query = exchange.getRequestURI().getQuery();
        String username = Helper.getQueryParameter(query, UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\twith invalid query: " + query);
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        System.out.println("\twith username: " + username);

        String response = String.valueOf(this._accountHelper.getAccount(username) != null);
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
