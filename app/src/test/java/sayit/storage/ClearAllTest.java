package sayit.storage;

import java.io.File;
import org.junit.jupiter.api.Test;
import sayit.qa.Answer;
import sayit.qa.Question;
import sayit.qa.QuestionAnswerEntry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the ClearAll functionality of TsvStore, which also affects MainUserInterface's ClearAllButton.
 */
public class ClearAllTest {

    @Test
    public void testClearAll() {

        File file = new File("testClearAll.tsv");
        IStore<QuestionAnswerEntry> store = TsvStore.createOrOpenStore("testClearAll.tsv");

        //Tests when the file is empty
        assertEquals(true, store.clearAll());
        assertEquals(0, file.length());

        //Tests when the file has one Question-Answer Entry

        store.insert(new QuestionAnswerEntry(new Question("What is 1 + 1?"), new Answer("2")));
        store.save();
        // Note: 18 is the default length of file because of header
        assertNotEquals(18, file.length());
        assertEquals(true, store.clearAll());
        assertEquals(0, file.length());

        //Tests when the file has multiple Question-Answer Entries.
        store.insert(new QuestionAnswerEntry(new Question("What is 1 + 1?"), new Answer("2")));
        store.insert(new QuestionAnswerEntry(new Question("What is 2 + 2?"), new Answer("4")));
        store.save();
        assertNotEquals(18, file.length());
        assertEquals(true, store.clearAll());
        assertEquals(0, file.length());

    }
}
