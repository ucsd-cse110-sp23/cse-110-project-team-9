package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.IServer;
import sayit.server.db.doctypes.SayItEmailConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Handles a GET request for getting the email configuration.
 * The endpoint will be <c>get_email_config</c>.
 */
public class GetEmailConfigurationHandler implements HttpHandler {
    private final IServer _server;


    /**
     * Creates a new instance of the <c>GetEmailConfigurationHandler</c> class.
     *
     * @param server the server instance
     */
    public GetEmailConfigurationHandler(IServer server) {
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
        if (!httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            httpExchange.close();
            return;
        }

        System.out.println("Received GET request for /get_email_config");
        String username = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.close();
            return;
        }

        SayItEmailConfiguration config = this._server.getEmailDb().getEmailConfiguration(username);
        if (config == null) {
            System.out.println("\tbut no email configuration for  " + username + " was found.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            httpExchange.close();
            return;
        }

        System.out.println("\tand found email configuration for " + username + ".");

        JSONObject emailConfig = new JSONObject();
        emailConfig.put(SayItEmailConfiguration.ACC_USERNAME_FIELD, config.getAccUsername());
        emailConfig.put(SayItEmailConfiguration.FIRST_NAME_FIELD, config.getFirstName());
        emailConfig.put(SayItEmailConfiguration.LAST_NAME_FIELD, config.getLastName());
        emailConfig.put(SayItEmailConfiguration.DISPLAY_NAME_FIELD, config.getDisplayName());
        emailConfig.put(SayItEmailConfiguration.EMAIL_FIELD, config.getEmail());
        emailConfig.put(SayItEmailConfiguration.EMAIL_PASSWORD_FIELD, config.getEmailPassword());
        emailConfig.put(SayItEmailConfiguration.SMTP_FIELD, config.getSmtp());
        emailConfig.put(SayItEmailConfiguration.TLS_FIELD, config.getTls());

        String response = emailConfig.toString();
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }
}
