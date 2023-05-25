package sayit.server;

import org.junit.jupiter.api.Test;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.db.store.TsvAccountHelper;
import sayit.server.storage.IStore;
import sayit.server.storage.TsvStore;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class SendRequestTest {
    private static final int PORT = 9172;
    private static final String DUMMY_FILE = "build.gradle.kts";

    @Test
    public void testCreateAccount() throws Exception {
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

    @Test
    public void testAskQuestion() throws Exception {
        var file = new File("testAskQuestion.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        IStore<QuestionAnswerEntry> store = TsvStore.createOrOpenStore("testAskQuestion.tsv");
        assertNotNull(store);
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Hello world."))
                .setChatGpt(new MockChatGpt(false, "Hello there."))
                .setStorage(store)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        var resp = requestSender.askQuestion(new File(DUMMY_FILE));
        assertEquals("Hello world.", resp.getSecond().getQuestion().getQuestionText());
        assertEquals("Hello there.", resp.getSecond().getAnswer().getAnswerText());
        assertEquals(0, resp.getFirst());

        server.stop();
        assertTrue(store.clearAll());
    }

    @Test
    public void testGetHistory() throws Exception {
        var file = new File("testGetHistory.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        IStore<QuestionAnswerEntry> store = TsvStore.createOrOpenStore("testGetHistory.tsv");
        assertNotNull(store);
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "ABC"))
                .setChatGpt(new MockChatGpt(false, "DEF"))
                .setStorage(store)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        for (int i = 0; i < 15; i++) {
            requestSender.askQuestion(new File(DUMMY_FILE));
            Thread.sleep(100);
        }

        var history = requestSender.getHistory();
        for (int i = 0; i < 15; i++) {
            assertTrue(history.containsKey(i));
        }

        server.stop();
        assertTrue(store.clearAll());
    }

    @Test
    public void testClearAll() throws Exception {
        IStore<QuestionAnswerEntry> store = TsvStore.createOrOpenStore("testClearAll.tsv");
        assertNotNull(store);
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "ABC"))
                .setChatGpt(new MockChatGpt(false, "DEF"))
                .setStorage(store)
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        for (int i = 0; i < 100; i++) {
            requestSender.askQuestion(new File(DUMMY_FILE));
            Thread.sleep(100);
        }

        assertEquals(100, requestSender.getHistory().size());
        assertEquals(100, store.size());
        assertTrue(requestSender.clearHistory());
        assertTrue(requestSender.getHistory().isEmpty());
        assertTrue(store.getEntries().isEmpty());

        server.stop();
        assertTrue(store.clearAll());
    }

    @Test
    public void testDelete() throws Exception {
        IStore<QuestionAnswerEntry> store = TsvStore.createOrOpenStore("testDelete.tsv");
        assertNotNull(store);
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "CSE 110"))
                .setChatGpt(new MockChatGpt(false, "is a class."))
                .setStorage(store)
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, 8273);
        // Wait for server to start
        Thread.sleep(2000);

        requestSender.askQuestion(new File(DUMMY_FILE));
        requestSender.askQuestion(new File(DUMMY_FILE));

        assertEquals(2, requestSender.getHistory().size());
        assertEquals(2, store.size());
        assertTrue(requestSender.delete(0));
        assertEquals(1, requestSender.getHistory().size());
        assertEquals(1, store.size());

        server.stop();
        assertTrue(store.clearAll());
    }
}