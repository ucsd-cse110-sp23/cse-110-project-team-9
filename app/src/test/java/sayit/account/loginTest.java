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
import static sayit.ServerConstants.PORT;

public class loginTest {
    @Test
    public void testLoginAccountExist() throws Exception {
        var file = new File("login.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Hello world."))
                .setChatGpt(new MockChatGpt(false, "Hello there."))
                .setAccountHelper(new TsvAccountHelper("login.tsv"))
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        requestSender.createAccount("testLogin", "testLogin");
        requestSender.createAccount("testLogin2", "testLogin2");
        requestSender.createAccount("testLogin3", "testLogin3");

        assertTrue(requestSender.login("testLogin", "testLogin"));
        assertTrue(requestSender.login("testLogin2", "testLogin2"));
        assertFalse(requestSender.login("testLogin3","testLogin2"));

        server.stop();
        assertTrue(file.delete());
    }

    @Test
    public void testLoginAccountNotExists() throws Exception {
        var file = new File("login.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Hello world."))
                .setChatGpt(new MockChatGpt(false, "Hello there."))
                .setAccountHelper(new TsvAccountHelper("login.tsv"))
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        assertFalse(requestSender.login("testLogin", "testLogin"));

        requestSender.createAccount("testLogin", "testLogin");
        requestSender.createAccount("testLogin2", "testLogin2");
        

        assertTrue(requestSender.login("testLogin", "testLogin"));
        assertTrue(requestSender.login("testLogin2", "testLogin2"));
        assertFalse(requestSender.login("testLogin3", "testLogin"));
        assertTrue(requestSender.createAccount("testLogin3", "testLogin3"));
        assertTrue(requestSender.login("testLogin3", "testLogin3"));

        server.stop();
        assertTrue(file.delete());
    }
}
