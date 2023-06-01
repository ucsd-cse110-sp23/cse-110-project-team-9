package sayit.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PromptDbTest {
    private static final String TEST_FILE = "test.tsv";
    private static final String DUMMY_USERNAME1 = "dummy";
    private static final String DUMMY_USERNAME2 = "Bob";
    private static final String USER_NOT_USED = "Todd";
    private static final String QUESTION = "QUESTION";
    private IPromptHelper helper = new TsvPromptHelper(TEST_FILE);
    private static final SayItPrompt PROMPT_1 = new SayItPrompt
            (DUMMY_USERNAME1, 1, QUESTION, "What is 1 + 1?", "2");
    private static final SayItPrompt PROMPT_2 = new SayItPrompt
            (DUMMY_USERNAME1, 2, QUESTION, "What is 2 + 2?", "4");
    private static final SayItPrompt PROMPT_3 = new SayItPrompt
            (DUMMY_USERNAME2, 3, QUESTION, "What is life?", "ME");

    @BeforeEach
    public void setUp() {
        var file = new File(TEST_FILE);
        if (file.exists()) {
            assertTrue(file.delete());
        }
        helper = new TsvPromptHelper(TEST_FILE);
    }

    @Test
    public void testAddGetDelete() {
        assertEquals(0, helper.getAllPromptsBy(DUMMY_USERNAME1).size());
        helper.createPrompt(PROMPT_1);
        helper.save();
        helper.createPrompt(PROMPT_2);
        helper.save();
        helper.createPrompt(PROMPT_3);
        helper.save();
        assertEquals(2, helper.getAllPromptsBy(DUMMY_USERNAME1).size());
        assertEquals(1, helper.getAllPromptsBy(DUMMY_USERNAME2).size());
        assertEquals(0, helper.getAllPromptsBy(USER_NOT_USED).size());

        assertEquals(PROMPT_1, helper.getAllPromptsBy(DUMMY_USERNAME1).get(0));
        assertEquals(PROMPT_2, helper.getAllPromptsBy(DUMMY_USERNAME1).get(1));
        assertEquals(PROMPT_3, helper.getAllPromptsBy(DUMMY_USERNAME2).get(0));

        assertFalse(helper.deletePrompt(DUMMY_USERNAME1, 10));
        assertFalse(helper.deletePrompt(USER_NOT_USED, 10));

        assertTrue(helper.deletePrompt(DUMMY_USERNAME1, 1));
        helper.save();
        assertEquals(1, helper.getAllPromptsBy(DUMMY_USERNAME1).size());

        assertTrue(helper.deletePrompt(DUMMY_USERNAME1, 2));
        helper.save();
        assertEquals(0, helper.getAllPromptsBy(DUMMY_USERNAME1).size());

        assertTrue(helper.deletePrompt(DUMMY_USERNAME2, 3));
        helper.save();
        assertEquals(0, helper.getAllPromptsBy(DUMMY_USERNAME2).size());


        helper.createPrompt(PROMPT_1);
        helper.save();
        helper.createPrompt(PROMPT_2);
        helper.save();
        helper.createPrompt(PROMPT_3);
        helper.save();

        assertEquals(2, helper.clearAllPrompts(DUMMY_USERNAME1));
        helper.save();
        assertEquals(0, helper.getAllPromptsBy(DUMMY_USERNAME1).size());

        assertEquals(1, helper.clearAllPrompts(DUMMY_USERNAME2));
        helper.save();
        assertEquals(0, helper.getAllPromptsBy(DUMMY_USERNAME2).size());
    }


    @Test
    public void testLoadData() {

        helper.createPrompt(PROMPT_1);
        helper.save();
        helper.createPrompt(PROMPT_2);
        helper.save();
        helper.createPrompt(PROMPT_3);
        helper.save();

        IPromptHelper helper2 = new TsvPromptHelper(TEST_FILE);
        assertNotNull(helper2);
        assertEquals(2, helper2.getAllPromptsBy(DUMMY_USERNAME1).size());
        assertEquals(1, helper2.getAllPromptsBy(DUMMY_USERNAME2).size());
        assertEquals(0, helper2.getAllPromptsBy(USER_NOT_USED).size());

        assertEquals(PROMPT_1, helper2.getAllPromptsBy(DUMMY_USERNAME1).get(0));
        assertEquals(PROMPT_2, helper2.getAllPromptsBy(DUMMY_USERNAME1).get(1));
        assertEquals(PROMPT_3, helper2.getAllPromptsBy(DUMMY_USERNAME2).get(0));

    }

    @Test
    public void testLoadDataEdit() {

        helper.createPrompt(PROMPT_1);
        helper.save();
        helper.createPrompt(PROMPT_2);
        helper.save();
        helper.createPrompt(PROMPT_3);
        helper.save();

        IPromptHelper helper2 = new TsvPromptHelper(TEST_FILE);
        assertNotNull(helper2);
        assertEquals(2, helper2.getAllPromptsBy(DUMMY_USERNAME1).size());
        assertEquals(1, helper2.getAllPromptsBy(DUMMY_USERNAME2).size());
        assertEquals(0, helper2.getAllPromptsBy(USER_NOT_USED).size());

        assertEquals(PROMPT_1, helper2.getAllPromptsBy(DUMMY_USERNAME1).get(0));
        assertEquals(PROMPT_2, helper2.getAllPromptsBy(DUMMY_USERNAME1).get(1));
        assertEquals(PROMPT_3, helper2.getAllPromptsBy(DUMMY_USERNAME2).get(0));

        helper2.deletePrompt(DUMMY_USERNAME1, 1);
        helper2.save();
        helper2.deletePrompt(DUMMY_USERNAME1, 2);
        helper2.save();
        assertEquals(0, helper2.getAllPromptsBy(DUMMY_USERNAME1).size());

        IPromptHelper helper3 = new TsvPromptHelper(TEST_FILE);
        assertNotNull(helper3);
        assertEquals(0, helper3.getAllPromptsBy(DUMMY_USERNAME1).size());
        assertEquals(1, helper3.getAllPromptsBy(DUMMY_USERNAME2).size());
        assertEquals(1, helper3.clearAllPrompts(DUMMY_USERNAME2));
        helper3.save();
        assertEquals(0, helper3.getAllPromptsBy(DUMMY_USERNAME2).size());

        IPromptHelper helper4 = new TsvPromptHelper(TEST_FILE);
        assertNotNull(helper4);
        assertEquals(0, helper4.getAllPromptsBy(DUMMY_USERNAME1).size());
        assertEquals(0, helper4.getAllPromptsBy(DUMMY_USERNAME2).size());
        helper4.createPrompt(PROMPT_1);
        helper4.save();
        assertEquals(1, helper4.getAllPromptsBy(DUMMY_USERNAME1).size());

        IPromptHelper helper5 = new TsvPromptHelper(TEST_FILE);
        assertNotNull(helper5);
        assertEquals(1, helper5.getAllPromptsBy(DUMMY_USERNAME1).size());
        assertEquals(0, helper5.getAllPromptsBy(DUMMY_USERNAME2).size());
        helper5.deletePrompt(DUMMY_USERNAME1, 1);
        helper5.save();
        assertEquals(0, helper5.getAllPromptsBy(DUMMY_USERNAME1).size());
    }

    @Test
    public void testGetAllList() {
        helper.createPrompt(PROMPT_1);
        helper.save();
        helper.createPrompt(PROMPT_2);
        helper.save();
        helper.createPrompt(PROMPT_3);
        helper.save();
        assertEquals(helper.getAllPromptsBy(DUMMY_USERNAME1).get(0), PROMPT_1);
        assertEquals(helper.getAllPromptsBy(DUMMY_USERNAME1).get(1), PROMPT_2);
        assertEquals(helper.getAllPromptsBy(DUMMY_USERNAME2).get(0), PROMPT_3);
    }

    @AfterEach
    public void tearDown() {
        assertTrue(new File(TEST_FILE).delete());
    }

}
