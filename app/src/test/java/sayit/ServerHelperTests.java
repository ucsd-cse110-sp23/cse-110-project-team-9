package sayit;

import org.junit.jupiter.api.Test;
import sayit.server.Helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ServerHelperTests {

    @Test
    public void testPromptExtractor() {
        assertEquals("What is 1 + 1?", Helper.extractPrompt(new String[]{
                "question"
        }, "Question. What is 1 + 1?"));

        assertEquals("about \"Welcome to CSE 12.\"", Helper.extractPrompt(new String[] {
                "create email",
                "create an email"
        }, "Create an email about \"Welcome to CSE 12.\""));

        assertEquals("about \"Welcome to CSE 12.\"", Helper.extractPrompt(new String[] {
                "create email",
                "create an email"
        }, "Create email about \"Welcome to CSE 12.\""));
    }

    @Test
    public void testNullPromptExtractor() {
        assertNull(Helper.extractPrompt(new String[]{
                "question"
        }, "Question."));

        assertNull(Helper.extractPrompt(new String[] {
                "create email",
                "create an email"
        }, "Create an email."));

        assertNull(Helper.extractPrompt(new String[] {
                "create email",
                "create an email"
        }, "Create email."));
    }
}
