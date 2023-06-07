package sayit.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sayit.common.UniversalConstants;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.store.TsvEmailConfigurationHelper;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sayit.TestConstants.*;

public class SendEmailTest {
    private static final String TEST_EMAIL_TSV = "test_email.tsv";

    @BeforeEach
    public void setUp() {
        var file = new File(TEST_EMAIL_TSV);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testCorrectSend() throws Exception{
        IPromptHelper promptHelper = new TsvPromptHelper(TEST_EMAIL_TSV);
        IEmailConfigurationHelper configHelper = new TsvEmailConfigurationHelper(TEST_EMAIL_TSV);
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "create email to Dave asking about the weather"))
                .setChatGpt(new MockChatGpt(false, "Hey Dave how is the weather?"))
                .setPromptHelper(promptHelper)
                .setEmailConfigurationHelper(configHelper)
                .setEmailSender(x -> true)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        requestSender.saveEmailConfiguration("username1", "aa",
                "bb", "ac", "ad", "ae", "af", "ag");

        var recording = requestSender.sendRecording(new File(DUMMY_FILE), "username1");
        var response = requestSender.sendEmail("username1", toAddress, recording.getID(), dummyID);
        assertTrue(response.getInput().toString().contains(UniversalConstants.SUCCESS));
        server.stop();
    }


    @Test
    public void testWrongPromptType() throws Exception{
        IPromptHelper promptHelper = new TsvPromptHelper(TEST_EMAIL_TSV);
        IEmailConfigurationHelper configHelper = new TsvEmailConfigurationHelper(TEST_EMAIL_TSV);
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "question to Dave asking about the weather"))
                .setChatGpt(new MockChatGpt(false, "Hey Dave how is the weather?"))
                .setPromptHelper(promptHelper)
                .setEmailConfigurationHelper(configHelper)
                .setEmailSender(x -> true)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        requestSender.saveEmailConfiguration("username0", "aa",
                "bb", "ac", "ad", "ae", "af", "ag");

        var recording = requestSender.sendRecording(new File(DUMMY_FILE), "username0");
        var response = requestSender.sendEmail("username0", toAddress, recording.getID(), dummyID);
        assertFalse(response.getInput().toString().contains(UniversalConstants.SUCCESS));
        server.stop();
    }
}
