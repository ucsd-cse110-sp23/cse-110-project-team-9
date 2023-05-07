package sayit.openai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WhisperTest {

    @Test
    public void testMockWhisperNoException() {
        MockWhisper whisper = new MockWhisper(false);
        whisperCheck check = new whisperCheck(whisper, null);
        try {
            assertEquals(check.output(), "Welcome to CSE 12 Pre-Recorded Lecture");
        } catch (Exception e) {
            fail("An exception was thrown when it should not have been thrown.");
        }
    }

    @Test
    public void testMockWhisperWithException() {
        MockWhisper whisper = new MockWhisper(true);
        whisperCheck check = new whisperCheck(whisper, null);
        assertThrows(OpenAiException.class, () -> check.output());
    }
}
