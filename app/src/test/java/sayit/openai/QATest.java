package sayit.openai;

import org.junit.jupiter.api.Test;
import sayit.common.qa.ProgramOutput;
import sayit.common.qa.UserInput;
import sayit.common.qa.InputOutputEntry;
import sayit.server.openai.WhisperCheck;

import static org.junit.jupiter.api.Assertions.*;

public class QATest {
    @Test
    public void testBasicWalkthrough() {
        MockChatGpt chatGpt = new MockChatGpt(false, "42");
        WhisperCheck check = new WhisperCheck(new MockWhisper(false, "What is the meaning of life?"), null);

        // Step 1: Let's "transcribe" the question.
        String question;
        try {
            question = check.output();
        } catch (Exception e) {
            fail("An exception was thrown when it shouldn't have been.");
            return;
        }

        assertEquals("What is the meaning of life?", question);

        // Step 2: Let's ask a question.
        String response;
        try {
            response = chatGpt.askQuestion(question);
        } catch (Exception e) {
            fail("An exception was thrown when it shouldn't have been.");
            return;
        }

        // Step 3: Let's check the response.
        assertEquals("42", response);

        // Step 4: Let's create the Entry object.
        InputOutputEntry entry = new InputOutputEntry(new UserInput(question), new ProgramOutput(response));
        assertEquals("What is the meaning of life?", entry.getInput().getInputText());
        assertEquals("42", entry.getOutput().getOutputText());
    }

    @Test
    public void testWalkthroughFailTranscribe() {
        WhisperCheck check = new WhisperCheck(new MockWhisper(true, "test error here"), null);
        String answer = check.output();
        assertTrue(check.isExceptionThrown());
        assertEquals("test error here", answer);
    }

    @Test
    public void testWalkthroughFailAskQuestion() {
        MockChatGpt chatGpt = new MockChatGpt(true, "test error here");
        WhisperCheck check = new WhisperCheck(new MockWhisper(false, "What is the meaning of life?"), null);

        String question;
        try {
            question = check.output();
        } catch (Exception e) {
            fail("An exception was thrown when it shouldn't have been.");
            return;
        }

        try {
            chatGpt.askQuestion(question);
        } catch (Exception e) {
            assertEquals("test error here", e.getMessage());
            return;
        }

        fail("An exception should have been thrown.");
    }
}
