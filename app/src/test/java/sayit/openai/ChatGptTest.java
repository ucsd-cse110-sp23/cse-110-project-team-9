package sayit.openai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChatGptTest {
    @Test
    public void testAskQuestionNoException() {
        IChatGpt chatGpt = new MockChatGpt(false);
        try {
            String response = chatGpt.askQuestion("some prompt");
            assertEquals("some response", response);
        } catch (Exception e) {
            fail("An exception was thrown when it shouldn't have been.");
        }
    }

    @Test
    public void testAskQuestionException() {
        IChatGpt chatGpt = new MockChatGpt(true);
        assertThrows(OpenAiException.class, () -> chatGpt.askQuestion("some prompt"));
    }
}
