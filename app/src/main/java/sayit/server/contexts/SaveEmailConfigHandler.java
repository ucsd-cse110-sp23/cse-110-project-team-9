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
 * Handles a request for saving email configurations. This class only saves
 * an email configuration if all the arguments are nonnull.
 * The endpoint will be <c>/save_email_config</c>
 */
public class SaveEmailConfigHandler implements HttpHandler {
    private final IEmailConfigurationHelper configHelper;

    /**
     * Creates a new instance of the <c>SaveEmailConfigurationHandler</c> class.
     *
     * @param configHelper The email configuration helper to use.
     */
    public SaveEmailConfigHandler(IEmailConfigurationHelper configHelper) {
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

        System.out.println("Received POST request for /save_email_configuration");

        JSONObject json = new JSONObject(new String(httpExchange.getRequestBody().readAllBytes()));
        System.out.println("\twith JSON: " + json);
        String accUsername = json.getString(UniversalConstants.USERNAME);
        String firstName = json.getString(UniversalConstants.FIRST_NAME);
        String lastName = json.getString(UniversalConstants.LAST_NAME);
        String displayName = json.getString(UniversalConstants.DISPLAY_NAME);
        String email = json.getString(UniversalConstants.EMAIL);
        String emailPassword = json.getString(UniversalConstants.EMAIL_PASSWORD);
        String smtp = json.getString(UniversalConstants.SMTP);
        String tls = json.getString(UniversalConstants.TLS);

        if (accUsername == null || firstName == null || lastName == null || displayName == null ||
            email == null || emailPassword == null || smtp == null || tls == null) {
            System.out.println("\tbut is invalid.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.close();
            return;
        }

        SayItEmailConfiguration config = new SayItEmailConfiguration(accUsername, firstName, lastName,
                displayName, email, emailPassword, smtp, tls);
        if (configHelper.getEmailConfiguration(accUsername) == null) {
            configHelper.createEmailConfiguration(config);
        }
        else {
            configHelper.replaceEmailConfiguration(config);
        }
        configHelper.save();

        System.out.println("\tand created email configuration");
        System.out.println("\t\twith username: " + accUsername);
        System.out.println("\t\twith first name: " + firstName);
        System.out.println("\t\twith last name: " + lastName);
        System.out.println("\t\twith display name: " + displayName);
        System.out.println("\t\twith email: " + email);
        System.out.println("\t\twith email password: " + "*".repeat(emailPassword.length()));
        System.out.println("\t\twith smtp port: " + smtp);
        System.out.println("\t\twith tls port: " + tls);

        
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        httpExchange.close();
    }
}
