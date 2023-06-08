package sayit.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.common.UniversalConstants;
import sayit.frontend.RequestSender;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.db.doctypes.SayItEmailConfiguration;
import sayit.server.db.store.TsvEmailConfigurationHelper;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sayit.TestConstants.DUMMY_FILE;
import static sayit.TestConstants.PORT;

public class VoicePromptTest {
    private static final String TEST_VOICE_PROMPT_TSV = "test_voice_prompt.tsv";

    @BeforeEach
    public void setUp() {
        var file = new File(TEST_VOICE_PROMPT_TSV);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testQuestionInputSuccess() throws IOException, InterruptedException {
        var whisper = new MockWhisper(false, "Question. Hello world.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "How are you?"))
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.QUESTION, resp.getType());
        assertEquals("Hello world.", resp.getInput().getInputText());
        assertEquals("How are you?", resp.getOutput().getOutputText());
        assertTrue(resp.getID() > 0);

        whisper.setValues(false, "Question, how are you?");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.QUESTION, resp.getType());
        assertEquals("how are you?", resp.getInput().getInputText());

        whisper.setValues(false, "Question? What is 1 + 1?");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.QUESTION, resp.getType());
        assertEquals("What is 1 + 1?", resp.getInput().getInputText());

        whisper.setValues(false, "Question welcome to cse 12 pre-recorded lecture.");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.QUESTION, resp.getType());
        assertEquals("welcome to cse 12 pre-recorded lecture.", resp.getInput().getInputText());

        server.stop();
    }

    @Test
    public void testDeletePrompt() throws InterruptedException, IOException {
        var whisper = new MockWhisper(false, "Delete prompt.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "How are you?"))
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.DELETE_PROMPT, resp.getType());
        server.stop();
    }

    @Test
    public void testClearAllPrompt() throws InterruptedException, IOException {
        var whisper = new MockWhisper(false, "Clear all prompts.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "How are you?"))
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.CLEAR_ALL, resp.getType());

        whisper.setValues(false, "Clear all.");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.CLEAR_ALL, resp.getType());

        whisper.setValues(false, "clear all");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.CLEAR_ALL, resp.getType());

        server.stop();
    }

    @Test
    public void testSetupEmailPrompt() throws IOException, InterruptedException {
        var whisper = new MockWhisper(false, "Setup email.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "How are you?"))
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.SETUP_EMAIL, resp.getType());

        server.stop();
    }

    @Test
    public void testCreateEmailPrompt() throws IOException, InterruptedException {
        var whisper = new MockWhisper(false, "Create email about why Kira is bad");
        var emailConfig = new TsvEmailConfigurationHelper("test.tsv");
        var file = new File("test.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        emailConfig.createEmailConfiguration(new SayItEmailConfiguration(
                "dummy",
                "firstname",
                "lastname",
                "displayname",
                "email",
                "emailpassword",
                "smtp",
                "tlsport"
        ));

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "This is a test. Sincerely,"))
                .setEmailConfigurationHelper(emailConfig)
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.EMAIL_DRAFT, resp.getType());
        assertEquals("This is a test. Sincerely,\ndisplayname", resp.getOutput().getOutputText());

        server.stop();
    }

    @Test
    public void sendEmailInputPrompt() throws IOException, InterruptedException {
        var whisper = new MockWhisper(false, "Send email.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "This is a test. Sincerely,"))
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        whisper.setValues(false, "Send email to a b c at y a h o o dot com");
        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.SEND_EMAIL, resp.getType());
        assertEquals("abc@yahoo.com", resp.getOutput().getOutputText());

        whisper.setValues(false, "Send email to h e l l o at w o r l d dot x y z");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.SEND_EMAIL, resp.getType());
        assertEquals("hello@world.xyz", resp.getOutput().getOutputText());

        server.stop();
    }

    @Test
    public void testSendEmailInputNoPrompt() throws IOException, InterruptedException {
        var whisper = new MockWhisper(false, "Send email to.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "This is a test. Sincerely,"))
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        // "Send email."
        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.ERROR, resp.getType());
        assertEquals(ServerConstants.NO_PROMPT_AFTER_COMMAND, resp.getOutput().getOutputText());

        // "Send email to"
        whisper.setValues(false, "Send email to");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.ERROR, resp.getType());
        assertEquals(ServerConstants.NO_PROMPT_AFTER_COMMAND, resp.getOutput().getOutputText());

        server.stop();
    }

    @Test
    public void testQuestionInputNoPrompt() throws IOException, InterruptedException {
        var whisper = new MockWhisper(false, "Question.");

        Server server = Server.builder()
                .setHost(ServerConstants.SERVER_HOSTNAME)
                .setPort(PORT)
                .setWhisper(whisper)
                .setChatGpt(new MockChatGpt(false, "How are you?"))
                .setPromptHelper(new TsvPromptHelper(TEST_VOICE_PROMPT_TSV))
                .build();

        server.start();

        var requestSender = RequestSender.getInstance(ServerConstants.SERVER_HOSTNAME, PORT);
        // Wait for server to start
        Thread.sleep(500);

        // "Question."
        var resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.ERROR, resp.getType());
        assertEquals(ServerConstants.NO_PROMPT_AFTER_COMMAND, resp.getOutput().getOutputText());

        // "Question. "
        whisper.setValues(false, "Question. ");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.ERROR, resp.getType());
        assertEquals(ServerConstants.NO_PROMPT_AFTER_COMMAND, resp.getOutput().getOutputText());

        // "Question"
        whisper.setValues(false, "Question");
        resp = requestSender.sendRecording(new File(DUMMY_FILE), "dummy");
        assertEquals(UniversalConstants.ERROR, resp.getType());

        server.stop();
    }
}
