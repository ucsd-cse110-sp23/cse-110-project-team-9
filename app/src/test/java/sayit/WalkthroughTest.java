package sayit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.common.UniversalConstants;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.Server;
import sayit.server.ServerConstants;
import sayit.server.db.store.TsvAccountHelper;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sayit.TestConstants.DUMMY_FILE;
import static sayit.TestConstants.PORT;

public class WalkthroughTest {
    private static final String TEST_PROMPT_TSV = "test_prompt.tsv";
    private static final String TEST_ACCOUNT_TSV = "test_account.tsv";

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
        // TODO

        // Greg Miranda then asks a new question.
        var resp1 = requestSender.sendRecording(new File(DUMMY_FILE), "gmiranda");
        assertEquals(UniversalConstants.QUESTION, resp1.getType());
        assertEquals("What is 1 + 1?", resp1.getInput().getInputText());
        // Make sure the answer is correct.
        assertEquals("The answer is 2.", resp1.getOutput().getOutputText());

        server.stop();
    }
}
