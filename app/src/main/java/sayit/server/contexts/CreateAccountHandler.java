package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.frontend.helpers.Pair;
import sayit.server.Helper;
import sayit.server.IServer;
import sayit.server.db.doctypes.SayItAccount;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handles a POST request for creating an account.
 * The endpoint will be <c>/create-account</c>.
 */
public class CreateAccountHandler implements HttpHandler {
    private final IServer _server;

    /**
     * Creates a new instance of the <c>CreateAccountHandler</c> class.
     *
     * @param server the server instance
     */
    public CreateAccountHandler(IServer server) {
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
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            exchange.close();
            return;
        }

        System.out.println("Received POST request for /create-account");

        JSONObject json = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
        System.out.println("\twith JSON: " + json);
        Pair<String, String> credentials = Helper.extractUsernamePassword(json);
        if (credentials == null) {
            System.out.println("\tbut is invalid.");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            exchange.close();
            return;
        }
        String username = credentials.getFirst();
        String password = credentials.getSecond();

        if (this._server.getAccountDb().getAccount(username) != null) {
            System.out.println("\tbut account already exists.");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_CONFLICT, 0);
            exchange.close();
            return;
        }

        System.out.println("\tand created account");
        System.out.println("\t\twith username: " + username);
        System.out.println("\t\twith password: " + "*".repeat(password.length()));

        this._server.getAccountDb().createAccount(new SayItAccount(username, password));
        this._server.getAccountDb().save();
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        exchange.close();
    }
}
