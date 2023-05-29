package sayit.storage;

import org.junit.jupiter.api.Test;
import sayit.common.qa.UserInput;
import sayit.common.qa.ProgramOutput;
import sayit.common.qa.InputOutputEntry;
import sayit.server.storage.IStore;
import sayit.server.storage.TsvStore;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the ClearAll functionality of TsvStore, which also affects MainUserInterface's ClearAllButton.
 */
public class ClearAllTest {

    @Test
    public void testClearAll() {

        File file = new File("testClearAll.tsv");
        IStore<InputOutputEntry> store = TsvStore.createOrOpenStore("testClearAll.tsv");

        //Tests when the file is empty
        assert store != null;
        assertTrue(store.clearAll());
        assertEquals(0, file.length());

        //Tests when the file has one Question-Answer Entry

        store.insert(new InputOutputEntry(new UserInput("What is 1 + 1?"), new ProgramOutput("2")));
        store.save();
        // Note: 18 is the default length of file because of header
        assertNotEquals(18, file.length());
        assertTrue(store.clearAll());
        assertEquals(0, file.length());

        //Tests when the file has multiple Question-Answer Entries.
        store.insert(new InputOutputEntry(new UserInput("What is 1 + 1?"), new ProgramOutput("2")));
        store.insert(new InputOutputEntry(new UserInput("What is 2 + 2?"), new ProgramOutput("4")));
        store.save();
        assertNotEquals(18, file.length());
        assertTrue(store.clearAll());
        assertEquals(0, file.length());
    }
}
