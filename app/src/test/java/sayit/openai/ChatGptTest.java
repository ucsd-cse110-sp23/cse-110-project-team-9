package sayit.openai;

import org.junit.jupiter.api.Test;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.OpenAiException;

import static org.junit.jupiter.api.Assertions.*;

public class ChatGptTest {
    @Test
    public void testAskQuestionNoException() {
        IChatGpt chatGpt = new MockChatGpt(false, "some response");
        try {
            String response = chatGpt.askQuestion("some prompt");
            assertEquals("some response", response);
        } catch (Exception e) {
            fail("An exception was thrown when it shouldn't have been.");
        }
    }

    @Test
    public void testAskQuestionException() {
        IChatGpt chatGpt = new MockChatGpt(true, "some error");
        assertThrows(OpenAiException.class, () -> chatGpt.askQuestion("some prompt"));
    }
}
