package sayit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.common.UniversalConstants;
import sayit.common.qa.InputOutputEntry;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.Server;
import sayit.server.ServerConstants;
import sayit.server.db.store.TsvAccountHelper;
import sayit.server.db.store.TsvEmailConfigurationHelper;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static sayit.TestConstants.DUMMY_FILE;
import static sayit.TestConstants.PORT;

public class WalkthroughTest {
    private static final String TEST_PROMPT_TSV = "test_prompt.tsv";
    private static final String TEST_ACCOUNT_TSV = "test_account.tsv";
    private static final String TEST_EMAIL_TSV = "test_email.tsv";

    @BeforeEach
    public void setUp() {
        var file = new File(TEST_PROMPT_TSV);
        if (file.exists()) {
            assertTrue(file.delete());
        }
        file = new File(TEST_ACCOUNT_TSV);
        if (file.exists()) {
            assertTrue(file.delete());
        }
        file = new File(TEST_EMAIL_TSV);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testAppIteration1WalkThrough() throws InterruptedException, IOException, URISyntaxException {
        MockWhisper whisper = new MockWhisper(false, "Question. What is 1 + 1?");
        MockChatGpt chatGpt = new MockChatGpt(false, "The answer is 2.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(chatGpt)
                .setPromptHelper(new TsvPromptHelper(TEST_PROMPT_TSV))
                .setAccountHelper(new TsvAccountHelper(TEST_ACCOUNT_TSV))
                .build();

        server.start();
        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);


        // Time to run through the application. Greg Miranda begins by creating a new account
        // with username "gmiranda" and password "cse110isbad".
        assertTrue(requestSender.createAccount("gmiranda", "cse110isbad"));
        // Let's make sure the account exists.
        assertTrue(requestSender.doesAccountExist("gmiranda"));
        // And then let's try to log into the account.
        assertTrue(requestSender.login("gmiranda", "cse110isbad"));
        // Greg Miranda then asks a new question.
        var resp1 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.QUESTION, resp1.getType());
        assertEquals("What is 1 + 1?", resp1.getInput().getInputText());
        // Make sure the answer is correct.
        assertEquals("The answer is 2.", resp1.getOutput().getOutputText());

        Thread.sleep(250);
        // Greg Miranda then asks another question.
        whisper.setValues(false, "Question. What is 2 + 2?");
        chatGpt.setValues(false, "The answer is 4.");
        var resp2 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.QUESTION, resp2.getType());
        assertEquals("What is 2 + 2?", resp2.getInput().getInputText());
        // Make sure the answer is correct.
        assertEquals("The answer is 4.", resp2.getOutput().getOutputText());

        Thread.sleep(250);
        // Greg Miranda then asks another question.
        whisper.setValues(false, "Question. Why is CSE 110 a bad class?");
        chatGpt.setValues(false, "Because it is.");
        var resp3 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.QUESTION, resp3.getType());
        assertEquals("Why is CSE 110 a bad class?", resp3.getInput().getInputText());
        // Make sure the answer is correct.
        assertEquals("Because it is.", resp3.getOutput().getOutputText());

        // Greg Miranda now wants to select the second question (What is 2 + 2)
        var history1 = requestSender.getHistory("gmiranda");
        assertEquals(3, history1.size());
        var secondQ = history1.values().stream()
                .filter(x -> x.getInput().getInputText().equals("What is 2 + 2?"))
                .findFirst()
                .orElse(null);
        assertNotNull(secondQ);

        // Now Greg Miranda wants to delete the question.
        whisper.setValues(false, "Delete prompt.");
        var resp4 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.DELETE_PROMPT, resp4.getType());
        assertTrue(requestSender.delete(secondQ.getID(), "gmiranda"));
        // Make sure the question is deleted.
        var history2 = requestSender.getHistory("gmiranda");
        assertEquals(2, history2.size());
        var secondQ2 = history2.values().stream()
                .filter(x -> x.getInput().getInputText().equals("What is 2 + 2?"))
                .findFirst()
                .orElse(null);
        assertNull(secondQ2);

        // Greg Miranda now wants to ask another question
        whisper.setValues(false, "Question. How do I pre-record a CSE 110 lecture?");
        chatGpt.setValues(false, "Welcome to CSE 110 pre-recorded lecture.");
        var resp5 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.QUESTION, resp5.getType());
        assertEquals("How do I pre-record a CSE 110 lecture?", resp5.getInput().getInputText());
        // Make sure the answer is correct.
        assertEquals("Welcome to CSE 110 pre-recorded lecture.", resp5.getOutput().getOutputText());

        // Ensure that the question is added to the history.
        var history3 = requestSender.getHistory("gmiranda");
        assertEquals(3, history3.size());

        // Greg Miranda now wants to log out.
        // Greg's friend, Billy, now wants to log in, but naturally forgot his password.
        assertFalse(requestSender.login("billy", "cse110isgood"));
        // So, Billy creates a new account
        assertTrue(requestSender.createAccount("billy", "cse11isbad"));
        // and then tries to log into it
        assertTrue(requestSender.login("billy", "cse11isbad"));
        // Billy then asks a question.
        whisper.setValues(false, "Question. Am I a monkey?");
        chatGpt.setValues(false, "Yes.");
        var resp6 = requestSender.sendRecording(new File(DUMMY_FILE), "billy");
        assertEquals(UniversalConstants.QUESTION, resp6.getType());
        assertEquals("Am I a monkey?", resp6.getInput().getInputText());
        // Make sure the answer is correct.
        assertEquals("Yes.", resp6.getOutput().getOutputText());

        // Billy asks another question.
        whisper.setValues(false, "Question. Can you tell Greg to go away during lecture?");
        chatGpt.setValues(false, "Yes.");
        var resp7 = requestSender.sendRecording(new File(DUMMY_FILE), "billy");
        assertEquals(UniversalConstants.QUESTION, resp7.getType());
        assertEquals("Can you tell Greg to go away during lecture?", resp7.getInput().getInputText());
        // Make sure the answer is correct.
        assertEquals("Yes.", resp7.getOutput().getOutputText());

        // Billy logs out and Greg now wants to log in.
        // Naturally, Greg forgets his password
        assertFalse(requestSender.login("gmiranda", "cse110isgood"));
        // But, Greg remembers it!
        assertTrue(requestSender.login("gmiranda", "cse110isbad"));
        // Greg looks at the questions that he has asked.
        var history4 = requestSender.getHistory("gmiranda");
        assertEquals(3, history4.size());
        var idGregAsked = history4.values().stream()
                .map(InputOutputEntry::getID)
                .toList();
        var idGregAskedBefore = history3.values().stream()
                .map(InputOutputEntry::getID)
                .toList();
        assertEquals(idGregAskedBefore, idGregAsked);

        // Greg now wants to clear all of his questions.
        // But, of course, he forgets the command.
        whisper.setValues(false, "Clear questions");
        var resp8 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.ERROR, resp8.getType());

        // Greg now remembers the command.
        whisper.setValues(false, "Clear all");
        var resp9 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.CLEAR_ALL, resp9.getType());
        assertEquals(3, requestSender.clearHistory("gmiranda"));
        // Make sure that all of Greg's questions are deleted.
        var history5 = requestSender.getHistory("gmiranda");
        assertEquals(0, history5.size());

        // Billy never touches the app again.
        // Greg has since retired from teaching.
        // The end.

        server.stop();
    }

    @Test
    public void testEmailCreationWalkThrough() throws InterruptedException, IOException, URISyntaxException {
        MockWhisper whisper = new MockWhisper(false, "Question. What is 1 + 1?");
        MockChatGpt chatGpt = new MockChatGpt(false, "The answer is 2.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(chatGpt)
                .setPromptHelper(new TsvPromptHelper(TEST_PROMPT_TSV))
                .setAccountHelper(new TsvAccountHelper(TEST_ACCOUNT_TSV))
                .setEmailConfigurationHelper(new TsvEmailConfigurationHelper(TEST_EMAIL_TSV))
                .build();

        server.start();
        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        // First, Greg Miranda says "Setup email."
        whisper.setValues(false, "Setup email.");
        assertEquals(UniversalConstants.SETUP_EMAIL,
                requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda").getType());

        // Because Greg never set up an email, nothing should be returned when he asks for his email configuration.
        assertNull(requestSender.getEmailConfiguration("gmiranda"));

        // Let's pretend that the UI for email setup is working.
        String firstName = "Greg";
        String lastName = "Miranda";
        String email = "gmiranda@yahoo.com";
        String password = "cse110isgood";
        String smtpServer = "smtp.mail.yahoo.com";
        String tlsPort = "587";
        String displayName = "Greg Miranda";

        // Greg enters his information into the UI.
        // The UI then sends a request to the server to set up his email.
        assertTrue(requestSender.saveEmailConfiguration("gmiranda", firstName, lastName, displayName,
                email, password, smtpServer, tlsPort));
        // Cool, it's been saved. Now, let's check if the correct values have been saved.
        var emailConfig = requestSender.getEmailConfiguration("gmiranda");
        assertNotNull(emailConfig);
        assertEquals(firstName, emailConfig.get(UniversalConstants.FIRST_NAME));
        assertEquals(lastName, emailConfig.get(UniversalConstants.LAST_NAME));
        assertEquals(email, emailConfig.get(UniversalConstants.EMAIL));
        assertEquals(password, emailConfig.get(UniversalConstants.EMAIL_PASSWORD));
        assertEquals(smtpServer, emailConfig.get(UniversalConstants.SMTP));
        assertEquals(tlsPort, emailConfig.get(UniversalConstants.TLS));

        // Greg now wants to edit his email
        // He enters his new information into the UI.
        // The UI then sends a request to the server to edit his email.
        String newFirstName = "Gregory";
        assertTrue(requestSender.saveEmailConfiguration("gmiranda", newFirstName, lastName, displayName,
                email, password, smtpServer, tlsPort));
        // Cool, it's been saved. Now, let's check if the correct values have been saved.
        var newEmailConfig = requestSender.getEmailConfiguration("gmiranda");
        assertNotNull(newEmailConfig);
        assertEquals(newFirstName, newEmailConfig.get(UniversalConstants.FIRST_NAME));
        assertEquals(lastName, newEmailConfig.get(UniversalConstants.LAST_NAME));
        assertEquals(email, newEmailConfig.get(UniversalConstants.EMAIL));
        assertEquals(password, newEmailConfig.get(UniversalConstants.EMAIL_PASSWORD));
        assertEquals(smtpServer, newEmailConfig.get(UniversalConstants.SMTP));
        assertEquals(tlsPort, newEmailConfig.get(UniversalConstants.TLS));

        server.stop();
    }
}
