package sayit.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.common.UniversalConstants;
import sayit.frontend.RequestSender;
import sayit.server.db.store.TsvEmailConfigurationHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static sayit.TestConstants.PORT;

public class EmailEndpointTest {
    private static final String TEST_EMAIL_TSV = "test_email.tsv";

    @BeforeEach
    public void setUp() {
        var file = new File(TEST_EMAIL_TSV);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testExistEmailConfig()  throws InterruptedException, IOException, URISyntaxException {
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setEmailConfigurationHelper(new TsvEmailConfigurationHelper(TEST_EMAIL_TSV))
                .build();

        server.start();
        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        var resp = requestSender.saveEmailConfiguration("username1", "aa",
                "ab", "ac", "ad", "ae", "af", "ag");
        assertTrue(resp);
        assertNotNull(requestSender.getEmailConfiguration("username1"));
        assertNull(requestSender.getEmailConfiguration("username2"));
        server.stop();
    }

    @Test
    public void createEmailConfig() throws InterruptedException, IOException, URISyntaxException {
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setEmailConfigurationHelper(new TsvEmailConfigurationHelper(TEST_EMAIL_TSV))
                .build();

        server.start();
        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        var resp = requestSender.saveEmailConfiguration("username1", "aa",
                "ab", "ac", "ad", "ae", "af", "ag");
        assertTrue(resp);
        var data = requestSender.getEmailConfiguration("username1");
        assertNotNull(data);
        assertEquals("aa", data.get(UniversalConstants.FIRST_NAME));
        assertEquals("ab", data.get(UniversalConstants.LAST_NAME));
        assertEquals("ac", data.get(UniversalConstants.DISPLAY_NAME));
        assertEquals("ad", data.get(UniversalConstants.EMAIL));
        assertEquals("ae", data.get(UniversalConstants.EMAIL_PASSWORD));
        assertEquals("af", data.get(UniversalConstants.SMTP));
        assertEquals("ag", data.get(UniversalConstants.TLS));

        server.stop();
    }

    @Test
    public void createAndReplaceEmailConfig() throws InterruptedException, IOException, URISyntaxException {
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setEmailConfigurationHelper(new TsvEmailConfigurationHelper(TEST_EMAIL_TSV))
                .build();

        server.start();
        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        var resp = requestSender.saveEmailConfiguration("username1", "aa",
                "ab", "ac", "ad", "ae", "af", "ag");
        assertTrue(resp);
        var data = requestSender.getEmailConfiguration("username1");
        assertNotNull(data);
        assertEquals("aa", data.get(UniversalConstants.FIRST_NAME));
        assertEquals("ab", data.get(UniversalConstants.LAST_NAME));
        assertEquals("ac", data.get(UniversalConstants.DISPLAY_NAME));
        assertEquals("ad", data.get(UniversalConstants.EMAIL));
        assertEquals("ae", data.get(UniversalConstants.EMAIL_PASSWORD));
        assertEquals("af", data.get(UniversalConstants.SMTP));
        assertEquals("ag", data.get(UniversalConstants.TLS));

        resp = requestSender.saveEmailConfiguration("username1", "ba",
                "bb", "bc", "bd", "be", "bf", "bg");
        assertTrue(resp);
        data = requestSender.getEmailConfiguration("username1");
        assertNotNull(data);
        assertEquals("ba", data.get(UniversalConstants.FIRST_NAME));
        assertEquals("bb", data.get(UniversalConstants.LAST_NAME));
        assertEquals("bc", data.get(UniversalConstants.DISPLAY_NAME));
        assertEquals("bd", data.get(UniversalConstants.EMAIL));
        assertEquals("be", data.get(UniversalConstants.EMAIL_PASSWORD));
        assertEquals("bf", data.get(UniversalConstants.SMTP));
        assertEquals("bg", data.get(UniversalConstants.TLS));

        server.stop();
    }
}
