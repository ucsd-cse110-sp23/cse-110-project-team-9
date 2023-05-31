package sayit.account;

import org.junit.jupiter.api.Test;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.Server;
import sayit.server.ServerConstants;
import sayit.server.db.store.TsvAccountHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sayit.TestConstants.PORT;

public class CreateAccountTest {
    @Test
    public void testCreateAccountNotExists() throws Exception {
        var file = new File("createAccount.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Hello world."))
                .setChatGpt(new MockChatGpt(false, "Hello there."))
                .setAccountHelper(new TsvAccountHelper("createAccount.tsv"))
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        assertTrue(requestSender.createAccount("testCreateAccount", "testCreateAccount"));
        assertTrue(requestSender.createAccount("testCreateAccount2", "testCreateAccount2"));
        assertTrue(requestSender.createAccount("testCreateAccount3", "testCreateAccount3"));

        assertTrue(requestSender.doesAccountExist("testCreateAccount"));
        assertTrue(requestSender.doesAccountExist("testCreateAccount2"));
        assertTrue(requestSender.doesAccountExist("testCreateAccount3"));
        assertFalse(requestSender.doesAccountExist("testCreateAccount4"));

        server.stop();
        assertTrue(file.delete());
    }

    @Test
    public void testCreateAccountExists() throws Exception {
        var file = new File("createAccount.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Hello world."))
                .setChatGpt(new MockChatGpt(false, "Hello there."))
                .setAccountHelper(new TsvAccountHelper("createAccount.tsv"))
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        assertTrue(requestSender.createAccount("testCreateAccount", "testCreateAccount"));
        assertTrue(requestSender.createAccount("testCreateAccount2", "testCreateAccount2"));
        assertFalse(requestSender.createAccount("testCreateAccount2", "testCreateAccount2"));
        assertTrue(requestSender.createAccount("testCreateAccount3", "testCreateAccount3"));
        assertFalse(requestSender.createAccount("testCreateAccount", "testCreateAccount"));
        assertFalse(requestSender.createAccount("testCreateAccount3", "testCreateAccount3"));

        server.stop();
        assertTrue(file.delete());
    }
}
