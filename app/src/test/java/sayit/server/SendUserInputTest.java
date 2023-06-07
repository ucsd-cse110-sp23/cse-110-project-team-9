package sayit.server;

import org.junit.jupiter.api.Test;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static sayit.TestConstants.DUMMY_FILE;
import static sayit.TestConstants.PORT;

public class SendUserInputTest {
    private static final String DUMMY_USERNAME = "dummy";

    @Test
    public void testAskQuestion() throws Exception {
        var file = new File("testAskQuestion.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        IPromptHelper promptHelper = new TsvPromptHelper("testAskQuestion.tsv");
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Question. Hello world."))
                .setChatGpt(new MockChatGpt(false, "How are you?"))
                .setPromptHelper(promptHelper)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        var resp = requestSender.sendRecording(new File(DUMMY_FILE), DUMMY_USERNAME);
      
        assertEquals("Hello world.", resp.getInput().getInputText());
        assertEquals("How are you?", resp.getOutput().getOutputText());
        assertTrue(resp.getID() > 0);

        server.stop();
        assertEquals(1, promptHelper.clearAllPrompts(DUMMY_USERNAME));
    }

    @Test
    public void testGetHistory() throws Exception {
        var file = new File("testGetHistory.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        IPromptHelper promptHelper = new TsvPromptHelper("testGetHistory.tsv");
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Question. ABC"))
                .setChatGpt(new MockChatGpt(false, "DEF"))
                .setPromptHelper(promptHelper)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        for (int i = 0; i < 15; i++) {
            requestSender.sendRecording(new File(DUMMY_FILE), DUMMY_USERNAME);
            Thread.sleep(100);
        }

        var history = requestSender.getHistory(DUMMY_USERNAME);
        for (var entry : history.values()) {
            assertEquals("ABC", entry.getInput().getInputText());
        }

        server.stop();
        assertEquals(15, promptHelper.clearAllPrompts(DUMMY_USERNAME));
    }

    @Test
    public void testClearAll() throws Exception {
        if (new File("testClearAll.tsv").exists()) {
            assertTrue(new File("testClearAll.tsv").delete());
        }

        IPromptHelper promptHelper = new TsvPromptHelper("testClearAll.tsv");
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Question. ABC"))
                .setChatGpt(new MockChatGpt(false, "DEF"))
                .setPromptHelper(promptHelper)
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        for (int i = 0; i < 10; i++) {
            requestSender.sendRecording(new File(DUMMY_FILE), DUMMY_USERNAME);
            Thread.sleep(100);
        }

        assertEquals(10, requestSender.getHistory(DUMMY_USERNAME).size());
        assertEquals(10, promptHelper.getAllPromptsBy(DUMMY_USERNAME).size());
        assertEquals(10, requestSender.clearHistory(DUMMY_USERNAME));
        assertTrue(requestSender.getHistory(DUMMY_USERNAME).isEmpty());
        assertTrue(promptHelper.getAllPromptsBy(DUMMY_USERNAME).isEmpty());

        server.stop();
        assertEquals(0, promptHelper.clearAllPrompts(DUMMY_USERNAME));
    }



    @Test
    public void testDelete() throws Exception {
        if (new File("testDelete.tsv").exists()) {
            assertTrue(new File("testDelete.tsv").delete());
        }

        IPromptHelper promptHelper = new TsvPromptHelper("testDelete.tsv");
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Question. CSE 110"))
                .setChatGpt(new MockChatGpt(false, "is a class."))
                .setPromptHelper(promptHelper)
                .build();
        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);

        requestSender.sendRecording(new File(DUMMY_FILE), DUMMY_USERNAME);
        requestSender.sendRecording(new File(DUMMY_FILE), DUMMY_USERNAME);

        var history = requestSender.getHistory(DUMMY_USERNAME);
        assertEquals(2, history.size());
        assertEquals(2, promptHelper.getAllPromptsBy(DUMMY_USERNAME).size());

        var entryToDelete = history.entrySet().stream().findFirst();
        assertTrue(entryToDelete.isPresent());
        var entry = entryToDelete.get();
        assertTrue(requestSender.delete(entry.getKey(), DUMMY_USERNAME));
        assertEquals(1, requestSender.getHistory(DUMMY_USERNAME).size());
        assertEquals(1, promptHelper.getAllPromptsBy(DUMMY_USERNAME).size());

        server.stop();
        assertTrue(promptHelper.clearAllPrompts(DUMMY_USERNAME) > 0);
    }
    @Test
    public void testEmailDraft() throws Exception {
        var file = new File("testEmailDraft.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        IPromptHelper promptHelper = new TsvPromptHelper("testEmailDraft.tsv");
        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(new MockWhisper(false, "Create an email to Kelly."))
                .setChatGpt(new MockChatGpt(false, "Hey Kelly \n"))
                .setPromptHelper(promptHelper)
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(2000);
        var emailAcc = requestSender.saveEmailConfiguration("username1", "aa",
                "ab", "ac", "ad", "ae", "af", "ag");
        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "username1");

        //assertEquals("Create an email to Kelly.", resp.getInput().getInputText());
        //assertEquals("Hey Kelly \nac", resp.getOutput().getOutputText());
        //assertTrue(resp.getID() > 0);

        server.stop();
        //assertEquals(1, promptHelper.clearAllPrompts(DUMMY_USERNAME));
    }
}