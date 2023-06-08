package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import sayit.common.UniversalConstants;
import sayit.server.Helper;
import sayit.server.IServer;
import sayit.server.db.doctypes.SayItEmailConfiguration;
import sayit.server.db.doctypes.SayItPrompt;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Handles a request for sending an email
 * The endpoint will be <c>/send_email</c>
 */
public class SendEmailHandler implements HttpHandler {
    private final IServer _server;

    /**
     * Creates a new instance of the <c>SaveEmailConfigurationHandler</c> class.
     *
     * @param server the server instance
     */
    public SendEmailHandler(IServer server) {
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
        //prepare response
        String response;
        JSONObject obj = new JSONObject();

        // Make sure we have a POST request here
        if (!httpExchange.getRequestMethod().equals("POST")) {
            obj.put(UniversalConstants.SEND_SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, UniversalConstants.ERROR);
            response = obj.toString();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();
            return;
        }

        System.out.println("Received POST request for /send_email");

        //extract necessary information from Query
        String username = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.USERNAME);
        String toAddress = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.TO_ADDRESS);
        
        //check ID Query
        String idsString = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.ID);
        String sendID = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.NEW_ID);

        //Id for created email
        long id = Long.parseLong(idsString);

        //ID for send email
        long newID = Long.parseLong(sendID);

        //get prompt information
        SayItPrompt sendItPrompt = this._server.getPromptDb().get(username, id);

        //get email information
        SayItEmailConfiguration config = this._server.getEmailDb().getEmailConfiguration(username);

        //check if selected prompt is an email
        if (!sendItPrompt.getType().equals(UniversalConstants.EMAIL_DRAFT)) {
            System.out.println("\tSelected Prompt not an email");
            obj.put(UniversalConstants.SEND_SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, sendItPrompt.getOutput());
            obj.put(UniversalConstants.ERROR, "The selected prompt is not an email draft. Please select an email "
                    + "draft, or create a new one using the \"Create email\" command.");
            handleErrorCase(httpExchange, obj, username, toAddress, newID);
            return;
        }

        String smtpHost = config.getSmtp();
        String tlsPort = config.getTls();
        String fromAddress = config.getEmail();
        String password = config.getEmailPassword();
        String displayName = config.getDisplayName();

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

        //get subject and body from GPT response
        String emailInputText = sendItPrompt.getOutput();
        String[] lines = emailInputText.split("\n"); // split by newline

        String subjectLine = null;
        StringBuilder bodyBuilder = new StringBuilder();

        for (String line : lines) {
            if (subjectLine == null && line.toLowerCase().startsWith("subject:")) {
                subjectLine = line.substring("subject:".length()).trim();
            } else {
                bodyBuilder.append(line).append("\n");
            }
        }

        String bodyText = bodyBuilder.toString().trim();

        //try to put email together and send
        MessagingException exception;
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress, displayName));
            System.out.println("\tfrom: " + fromAddress);
            System.out.println("\tdisplay name: " + displayName);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            System.out.println("\tto: " + toAddress);
            message.setSubject(subjectLine);
            System.out.println("\twith subject: " + subjectLine);
            message.setText(bodyText);
            System.out.println("\twith body: " + bodyText.replaceAll("\n", "<br>"));
            exception = this._server.getEmailSender().map(message);
        } catch (MessagingException e) {
            exception = e;
        }

        if (exception != null) {
            System.out.println("\terror sending email: " + exception.getMessage());
            obj.put(UniversalConstants.SEND_SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, sendItPrompt.getOutput());
            obj.put(UniversalConstants.ERROR, "Error Sending Email: " + exception.getMessage());
            handleErrorCase(httpExchange, obj, username, toAddress, newID);
            return;
        }

        // Handle response for updating history
        obj.put(UniversalConstants.SEND_SUCCESS, true);
        obj.put(UniversalConstants.OUTPUT, sendItPrompt.getOutput());
        response = obj.toString();

        SayItPrompt prompt = new SayItPrompt(username, newID, UniversalConstants.SEND_EMAIL,
                "Send email to: " + toAddress + " " + UniversalConstants.SUCCESS,
                sendItPrompt.getOutput()
        );

        this._server.getPromptDb().createPrompt(prompt);
        this._server.getPromptDb().save();

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }

    /**
     * Handles the error case for sending an email
     *
     * @param httpExchange the http exchange
     * @param obj          the json object
     * @param username     the username
     * @param toAddress    the address to send to
     * @param newID        the new id
     * @throws IOException if there is an error
     */
    private void handleErrorCase(HttpExchange httpExchange, JSONObject obj,
                                 String username, String toAddress, long newID) throws IOException {
        String response;
        response = obj.toString();

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();

        SayItPrompt prompt = new SayItPrompt(username, newID, UniversalConstants.SEND_EMAIL,
                "Send email to: " + toAddress + " " + UniversalConstants.ERROR,
                obj.getString(UniversalConstants.ERROR)
        );

        this._server.getPromptDb().createPrompt(prompt);
        this._server.getPromptDb().save();
    }

}
