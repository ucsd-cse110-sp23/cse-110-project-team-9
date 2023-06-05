package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.doctypes.SayItEmailConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handles a request for sending an email  
 * The endpoint will be <c>/send_email</c>
 */
public class SendEmailHandler implements HttpHandler {
    private final IEmailConfigurationHelper configHelper;

    /**
     * Creates a new instance of the <c>SaveEmailConfigurationHandler</c> class.
     *
     * @param configHelper The email configuration helper to use.
     */
    public SendEmailHandler(IEmailConfigurationHelper configHelper) {
        this.configHelper = configHelper;
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
        // Make sure we have a POST request here
        if (!httpExchange.getRequestMethod().equals("POST")) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            httpExchange.close();
            return;
        }

        System.out.println("Received POST request for /send_email");

        JSONObject json = new JSONObject(new String(httpExchange.getRequestBody().readAllBytes()));
        System.out.println("\twith JSON: " + json);

        String fromAddress = "";
        String toAddress = "";
        

        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        httpExchange.close();
    }
}
