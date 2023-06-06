package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItEmailConfiguration;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.db.mongo.*;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Handles a request for sending an email  
 * The endpoint will be <c>/send_email</c>
 */
public class SendEmailHandler implements HttpHandler {
    private final IEmailConfigurationHelper configHelper;
    private final IPromptHelper promptHelper;

    /**
     * Creates a new instance of the <c>SaveEmailConfigurationHandler</c> class.
     *
     * @param configHelper The email configuration helper to use.
     */
    public SendEmailHandler(IEmailConfigurationHelper configHelper, IPromptHelper promptHelper) {
        this.configHelper = configHelper;
        this.promptHelper = promptHelper;
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

        //extract necessary information from Query
        String username = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.USERNAME);
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.close();
            return;
        }
        
        String toAddress = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.TO_ADDRESS);
        if (toAddress == null) {
            System.out.println("\tbut is invalid because no to address specified.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.close();
            return;
        }

        String idsString = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.ID);
        if (idsString == null) {
            System.out.println("\tbut is invalid because no prompt ID specified.");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            httpExchange.close();
            return;
        }

        long id = Long.parseLong(idsString);

        //get email information
        SayItEmailConfiguration config = configHelper.getEmailConfiguration(username);

        if(config == null){
            System.out.println("Email not configured");
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            httpExchange.close();
            return;
        }

        String smtpHost = config.getSmtp();
        String tlsPort = config.getTls();
        String fromAddress = config.getEmail();
        String password = config.getEmailPassword();
        String displayName = config.getDisplayName();

        SayItPrompt sendItPrompt = promptHelper.get(username, id);

        //check if selected prompt is an email
        // String checkForEmail = sendItPrompt.getInput();
        // if (!checkForEmail.toLowerCase().startsWith("create email")
        //         && !checkForEmail.toLowerCase().startsWith("create an email")){
        //     System.out.println("Selected Prompt not an email");
        //     httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        //     httpExchange.close();
        //     return;
        // }


        //send email
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", tlsPort);

        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromAddress, password);
                }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress, displayName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject("Test Subject");
            message.setText("Test");

            Transport.send(message);

            System.out.println("Email sent successfully");

        } catch (Exception e) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            httpExchange.close();
            return;
        }

        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        httpExchange.close();
    }
}
