package sayit.server.contexts;

import com.sun.mail.imap.protocol.ID;
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
import java.nio.charset.StandardCharsets;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Handles a request for sending an email  
 * The endpoint will be <c>/send_email</c>
 */
public class SendEmailHandler extends ISendHandler {
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

        //prepare response
        String response;
        JSONObject obj = new JSONObject();
        
        // Make sure we have a POST request here
        if (!httpExchange.getRequestMethod().equals("POST")) {
            obj.put(UniversalConstants.SUCCESS, false);
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
        if (username == null) {
            System.out.println("\tbut is invalid because no username specified.");
            obj.put(UniversalConstants.SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, UniversalConstants.ERROR);
            response = obj.toString();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();
            return;
        }
        
        String toAddress = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.TO_ADDRESS);
        if (toAddress == null) {
            System.out.println("\tbut is invalid because no to address specified.");
            obj.put(UniversalConstants.SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, UniversalConstants.ERROR);
            response = obj.toString();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();;
            return;
        }

        //check ID Query
        String idsString = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
                UniversalConstants.ID);
        if (idsString == null) {
            System.out.println("\tbut is invalid because no prompt ID specified.");
            obj.put(UniversalConstants.SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, UniversalConstants.ERROR);
            response = obj.toString();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();
            return;
        }

        String sendID = Helper.getQueryParameter(httpExchange.getRequestURI().getQuery(),
        UniversalConstants.NEW_ID);

        if (sendID == null) {
            System.out.println("\tbut is invalid because no send ID specified.");
            obj.put(UniversalConstants.SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, UniversalConstants.ERROR);
            response = obj.toString();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();
            return;
        }

        //Id for created email
        long id = Long.parseLong(idsString);

        //ID for send email
        long newID = Long.parseLong(sendID);


        //get prompt information
        SayItPrompt sendItPrompt = promptHelper.get(username, id);

        //get email information
        SayItEmailConfiguration config = configHelper.getEmailConfiguration(username);

        //check if selected prompt is an email
        String checkForEmail = sendItPrompt.getInput();
        if (!checkForEmail.toLowerCase().startsWith("create email")
                && !checkForEmail.toLowerCase().startsWith("create an email")){
            System.out.println("Selected Prompt not an email");

            obj.put(UniversalConstants.SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, sendItPrompt.getOutput());
            obj.put(UniversalConstants.ERROR, "Selected prompt not an email");
            response = obj.toString();

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();

            SayItPrompt prompt = new SayItPrompt(username, newID, UniversalConstants.SEND_EMAIL, 
                "Send email to: " + toAddress + " " + UniversalConstants.ERROR, 
                obj.getString(UniversalConstants.ERROR)
            );

            this.promptHelper.createPrompt(prompt);
            this.promptHelper.save();
            return;
        }

        if(config == null){
            System.out.println("\tEmail not configured");
            obj.put(UniversalConstants.SUCCESS, false);
            obj.put(UniversalConstants.ERROR, "email not configured");
            obj.put(UniversalConstants.OUTPUT, sendItPrompt.getOutput());
            response = obj.toString();
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();

            SayItPrompt prompt = new SayItPrompt(username, newID, UniversalConstants.SEND_EMAIL, 
                "Send email to: " + toAddress + " " + UniversalConstants.ERROR, 
                obj.getString(UniversalConstants.ERROR)
            );

            this.promptHelper.createPrompt(prompt);
            this.promptHelper.save();
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
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress, displayName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject(subjectLine);
            message.setText(bodyText);

            Transport.send(message);

            System.out.println("\tEmail sent successfully");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("\tError sending email");

            obj.put(UniversalConstants.SUCCESS, false);
            obj.put(UniversalConstants.OUTPUT, sendItPrompt.getOutput());
            obj.put(UniversalConstants.ERROR, "Error Sending Email");
            response = obj.toString();

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();

            SayItPrompt prompt = new SayItPrompt(username, newID, UniversalConstants.SEND_EMAIL, 
                "Send email to: " + toAddress + " " + UniversalConstants.ERROR, 
                obj.getString(UniversalConstants.ERROR)
            );

            this.promptHelper.createPrompt(prompt);
            this.promptHelper.save();
            return;
        }

        //handle response for updating history
        

        obj.put(UniversalConstants.SUCCESS, true);
        obj.put(UniversalConstants.OUTPUT, sendItPrompt.getOutput());
        response = obj.toString();

        SayItPrompt prompt = new SayItPrompt(username, newID, UniversalConstants.SEND_EMAIL, 
            "Send email to: " + toAddress + " " + UniversalConstants.SUCCESS, 
            sendItPrompt.getOutput()
            );

        this.promptHelper.createPrompt(prompt);
        this.promptHelper.save();

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
        httpExchange.getResponseBody().write(bytes);
        httpExchange.close();
    }
    
}
