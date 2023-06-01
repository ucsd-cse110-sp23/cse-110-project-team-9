package sayit.storage;

import org.junit.jupiter.api.Test;
import sayit.common.UniversalConstants;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the ClearAll functionality of TsvStore, which also affects MainUserInterface's ClearAllButton.
 */
public class ClearAllTest {

    public static final String DUMMY_USERNAME = "dummy2";

    @Test
    public void testClearAll() {
        File file = new File("testClearAll.tsv");
        if (file.exists()) {
            assertTrue(file.delete());
        }

        IPromptHelper store = new TsvPromptHelper("testClearAll.tsv");

        // Tests when the file is empty
        assertEquals(0, store.clearAllPrompts(DUMMY_USERNAME));

        //Tests when the file has one Question-Answer Entry
        store.createPrompt(new SayItPrompt(DUMMY_USERNAME, 123, UniversalConstants.QUESTION,
                "What is 1 + 1?", "2"));
        store.save();

        assertEquals(1, store.clearAllPrompts(DUMMY_USERNAME));

        //Tests when the file has multiple Question-Answer Entries.
        store.createPrompt(new SayItPrompt(DUMMY_USERNAME, 123, UniversalConstants.QUESTION,
                "What is 1 + 1?", "2"));
        store.createPrompt(new SayItPrompt(DUMMY_USERNAME, 123, UniversalConstants.QUESTION,
                "What is 2 + 2?", "4"));

        store.save();
        assertEquals(2, store.clearAllPrompts(DUMMY_USERNAME));
    }
}
