package sayit.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TsvStoreTest {
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

        assertEquals(false, helper.deletePrompt(DUMMY_USERNAME1, 10));
        assertEquals(false, helper.deletePrompt(USER_NOT_USED, 10));

        assertEquals(true, helper.deletePrompt(DUMMY_USERNAME1, 1));
        helper.save();
        assertEquals(1, helper.getAllPromptsBy(DUMMY_USERNAME1).size());

        assertEquals(true, helper.deletePrompt(DUMMY_USERNAME1, 2));
        helper.save();
        assertEquals(0, helper.getAllPromptsBy(DUMMY_USERNAME1).size());

        assertEquals(true, helper.deletePrompt(DUMMY_USERNAME2, 3));
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


    /**
    @Test
    public void testLoadDataEdit() {
        _store.insert(new InputOutputEntry(new UserInput("CSE"), new ProgramOutput("Computer Science & Engineering")));
        _store.insert(new InputOutputEntry(new UserInput("ECE"), new ProgramOutput("Electrical & Computer Engineering")));
        _store.insert(new InputOutputEntry(new UserInput("MAE"), new ProgramOutput("Mechanical & Aerospace Engineering")));
        _store.insert(new InputOutputEntry(new UserInput("BENG"), new ProgramOutput("Bioengineering")));
        _store.insert(new InputOutputEntry(new UserInput("NANO"), new ProgramOutput("Nanoengineering")));
        _store.insert(new InputOutputEntry(new UserInput("CENG"), new ProgramOutput("Chemical Engineering")));
        _store.insert(new InputOutputEntry(new UserInput("SE"), new ProgramOutput("Structural Engineering")));
        _store.insert(new InputOutputEntry(new UserInput("MATH"), new ProgramOutput("Mathematics")));
        _store.insert(new InputOutputEntry(new UserInput("COGS"), new ProgramOutput("Cognitive Science")));
        _store.insert(new InputOutputEntry(new UserInput("POLI"), new ProgramOutput("hello")));

        assertTrue(_store.save());

        IStore<InputOutputEntry> store2 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store2);
        assertEquals(10, store2.size());
        assertEquals(new InputOutputEntry(new UserInput("CSE"), new ProgramOutput("Computer Science & Engineering")), store2.get(0));
        assertEquals(new InputOutputEntry(new UserInput("ECE"), new ProgramOutput("Electrical & Computer Engineering")), store2.get(1));
        assertEquals(new InputOutputEntry(new UserInput("MAE"), new ProgramOutput("Mechanical & Aerospace Engineering")), store2.get(2));
        assertEquals(new InputOutputEntry(new UserInput("BENG"), new ProgramOutput("Bioengineering")), store2.get(3));
        assertEquals(new InputOutputEntry(new UserInput("NANO"), new ProgramOutput("Nanoengineering")), store2.get(4));
        assertEquals(new InputOutputEntry(new UserInput("CENG"), new ProgramOutput("Chemical Engineering")), store2.get(5));
        assertEquals(new InputOutputEntry(new UserInput("SE"), new ProgramOutput("Structural Engineering")), store2.get(6));
        assertEquals(new InputOutputEntry(new UserInput("MATH"), new ProgramOutput("Mathematics")), store2.get(7));
        assertEquals(new InputOutputEntry(new UserInput("COGS"), new ProgramOutput("Cognitive Science")), store2.get(8));
        assertEquals(new InputOutputEntry(new UserInput("POLI"), new ProgramOutput("hello")), store2.get(9));

        assertTrue(store2.delete(0));
        assertTrue(store2.delete(1));
        assertTrue(store2.delete(2));
        assertTrue(store2.save());

        IStore<InputOutputEntry> store3 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store3);
        assertEquals(7, store3.size());
        assertNull(store2.get(0));
        assertNull(store2.get(1));
        assertNull(store2.get(2));
        assertEquals(new InputOutputEntry(new UserInput("BENG"), new ProgramOutput("Bioengineering")), store3.get(3));
        assertEquals(new InputOutputEntry(new UserInput("NANO"), new ProgramOutput("Nanoengineering")), store3.get(4));
        assertEquals(new InputOutputEntry(new UserInput("CENG"), new ProgramOutput("Chemical Engineering")), store3.get(5));
        assertEquals(new InputOutputEntry(new UserInput("SE"), new ProgramOutput("Structural Engineering")), store3.get(6));
        assertEquals(new InputOutputEntry(new UserInput("MATH"), new ProgramOutput("Mathematics")), store3.get(7));
        assertEquals(new InputOutputEntry(new UserInput("COGS"), new ProgramOutput("Cognitive Science")), store3.get(8));
        assertEquals(new InputOutputEntry(new UserInput("POLI"), new ProgramOutput("hello")), store3.get(9));

        assertFalse(store3.delete(0));
        assertFalse(store3.delete(1111));

        assertTrue(store3.save());

        IStore<InputOutputEntry> store4 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store4);
        assertEquals(7, store4.size());
        assertNull(store4.get(0));
        assertNull(store4.get(1));
        assertNull(store4.get(2));
        assertEquals(new InputOutputEntry(new UserInput("BENG"), new ProgramOutput("Bioengineering")), store4.get(3));
        assertEquals(new InputOutputEntry(new UserInput("NANO"), new ProgramOutput("Nanoengineering")), store4.get(4));
        assertEquals(new InputOutputEntry(new UserInput("CENG"), new ProgramOutput("Chemical Engineering")), store4.get(5));
        assertEquals(new InputOutputEntry(new UserInput("SE"), new ProgramOutput("Structural Engineering")), store4.get(6));
        assertEquals(new InputOutputEntry(new UserInput("MATH"), new ProgramOutput("Mathematics")), store4.get(7));
        assertEquals(new InputOutputEntry(new UserInput("COGS"), new ProgramOutput("Cognitive Science")), store4.get(8));
        assertEquals(new InputOutputEntry(new UserInput("POLI"), new ProgramOutput("hello")), store4.get(9));

        assertTrue(store4.delete(9));
        assertEquals(10, store4.insert(new InputOutputEntry(new UserInput("POLI"), new ProgramOutput("Political Science"))));
        assertTrue(store4.delete(4));

        assertTrue(store4.save());

        IStore<InputOutputEntry> store5 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store5);
        assertEquals(6, store5.size());
        assertNull(store5.get(0));
        assertNull(store5.get(1));
        assertNull(store5.get(2));
        assertEquals(new InputOutputEntry(new UserInput("BENG"), new ProgramOutput("Bioengineering")), store5.get(3));
        assertNull(store5.get(4));
        assertEquals(new InputOutputEntry(new UserInput("CENG"), new ProgramOutput("Chemical Engineering")), store5.get(5));
        assertEquals(new InputOutputEntry(new UserInput("SE"), new ProgramOutput("Structural Engineering")), store5.get(6));
        assertEquals(new InputOutputEntry(new UserInput("MATH"), new ProgramOutput("Mathematics")), store5.get(7));
        assertEquals(new InputOutputEntry(new UserInput("COGS"), new ProgramOutput("Cognitive Science")), store5.get(8));
        assertNull(store5.get(9));
        assertEquals(new InputOutputEntry(new UserInput("POLI"), new ProgramOutput("Political Science")), store5.get(10));
    }

    @Test
    public void testEmptyStore() {
        assertEquals(0, _store.size());
        assertNull(_store.get(0));
        assertNull(_store.get(1));

        assertFalse(_store.delete(0));
        assertFalse(_store.delete(1));

        assertTrue(_store.save());

        IStore<InputOutputEntry> store2 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store2);
        assertEquals(0, store2.size());

        assertNull(store2.get(0));
        assertNull(store2.get(1));
    }

    @Test
    public void testGetAllMap() {
        _store.insert(new InputOutputEntry(new UserInput("CSE"), new ProgramOutput("Computer Science & Engineering")));
        _store.insert(new InputOutputEntry(new UserInput("ECE"), new ProgramOutput("Electrical & Computer Engineering")));

        var map = _store.getEntries();
        assertEquals(2, map.size());
        assertEquals(new InputOutputEntry(new UserInput("CSE"), new ProgramOutput("Computer Science & Engineering")), map.get(0));
        assertEquals(new InputOutputEntry(new UserInput("ECE"), new ProgramOutput("Electrical & Computer Engineering")), map.get(1));
    }
     **/


    @AfterEach
    public void tearDown() {
        assertTrue(new File(TEST_FILE).delete());
        assertFalse(new File(TEST_FILE).exists());
    }

}
