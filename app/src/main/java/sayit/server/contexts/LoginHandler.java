package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.server.db.common.IAccountHelper;
import sayit.server.db.doctypes.SayItAccount;

import java.io.IOException;

/**
 * Handles a POST request for creating an account.
 * The endpoint will be <c>/login</c>.
 */
public class LoginHandler implements HttpHandler {
    private final IAccountHelper _accountHelper;

    /**
     * Creates a new instance of the <c>LoginHandler</c> class.
     *
     * @param accountHelper The account helper to use.
     */
    public LoginHandler(IAccountHelper accountHelper) {
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
        
        if (!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }
        
        System.out.println("Received POST request for /login");

        JSONObject json = new JSONObject(new String(exchange.getRequestBody().readAllBytes()));
        System.out.println("\twith JSON: " + json);
        String username;
        String password;
        try {
            username = json.getString("username");
            password = json.getString("password");
        } catch (Exception e) {
            System.out.println("\tbut is invalid.");
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        SayItAccount acc = this._accountHelper.getAccount(username);
        if (acc == null) {
            System.out.println("\tbut account does not exists.");
            exchange.sendResponseHeaders(409, 0);
            exchange.close();
            return;
        }
        System.out.println(password.compareTo(acc.getPassword()));
        if(!acc.getPassword().equals(password)){
            System.out.println("\tincorrect password");
            exchange.sendResponseHeaders(409, 0);
            exchange.close();
            return;
        }

        System.out.println("\tlogin successful");
        System.out.println("\t\twith username: " + username);
        
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }
}
