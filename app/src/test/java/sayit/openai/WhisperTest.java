package sayit.openai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WhisperTest {
    @Test
    public void testMockWhisperNoException() {
        MockWhisper whisper = new MockWhisper(false);
        try {
            assertEquals(whisper.transcribe(null), "Welcome to CSE 12 Pre-Recorded Lecture");
        } catch (Exception e) {
            fail("An exception was thrown when it should not have been thrown.");
        }
    }

    @Test
    public void testMockWhisperWithException() {
        MockWhisper whisper = new MockWhisper(true);
        assertThrows(OpenAiException.class, () -> whisper.transcribe(null));
    }
}
