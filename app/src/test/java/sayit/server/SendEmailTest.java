package sayit.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.NullConversion;

import com.sun.mail.imap.protocol.ID;

import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.store.TsvEmailConfigurationHelper;
import sayit.server.db.store.TsvPromptHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static sayit.TestConstants.*;

public class SendEmailTest {
    private static final String TEST_EMAIL_TSV = "test_email.tsv";

    @Test
    public void testCorrectSend() throws Exception{
        IPromptHelper promptHelper = new TsvPromptHelper("testCreateEmail.tsv");
        IEmailConfigurationHelper configHelper = new TsvEmailConfigurationHelper(TEST_EMAIL_TSV);
        MockSendServer server = MockSendServer.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "create email to Dave asking about the weather"))
                .setChatGpt(new MockChatGpt(false, "Hey Dave how is the weather?"))
                .setPromptHelper(promptHelper)
                .setEmailConfigurationHelper(configHelper)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        requestSender.saveEmailConfiguration("username1", "aa",
                "bb", "ac", "ad", "ae", "af", "ag");

        var response = requestSender.sendEmail("username1", toAddress, dummyID);
        assertTrue(response);

    }
}
