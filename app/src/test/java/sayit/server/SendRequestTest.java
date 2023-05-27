package sayit.server;

import org.junit.jupiter.api.*;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.storage.IStore;
import sayit.server.storage.TsvStore;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class SendRequestTest {
    @Test
    public void testAskQuestion() throws Exception {
        var file = new File("testAskQuestion.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        IStore<QuestionAnswerEntry> store = TsvStore.createOrOpenStore("testAskQuestion.tsv");
        assertNotNull(store);
        Server server = new Server(store,
                ServerConstants.SERVER_HOSTNAME,
                8222,
                new MockWhisper(false, "Hello world."),
                new MockChatGpt(false, "Hello there."));
        new Thread(server::start).start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, 8222);
        // Wait for server to start
        Thread.sleep(2000);

        var resp = requestSender.sendRecording(new File("build.gradle.kts"));
        assertEquals("Hello world.", resp.getSecond().getQuestion().getQuestionText());
        assertEquals("Hello there.", resp.getSecond().getAnswer().getAnswerText());
        assertEquals(0, resp.getFirst());

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
        Server server = new Server(store,
                ServerConstants.SERVER_HOSTNAME,
                8176,
                new MockWhisper(false, "ABC"),
                new MockChatGpt(false, "DEF"));
        new Thread(server::start).start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, 8176);
        // Wait for server to start
        Thread.sleep(2000);

        for (int i = 0; i < 15; i++) {
            requestSender.sendRecording(new File("build.gradle.kts"));
            Thread.sleep(100);
        }

        var history = requestSender.getHistory();
        for (int i = 0; i < 15; i++) {
            assertTrue(history.containsKey(i));
        }

        assertTrue(store.clearAll());
    }
}