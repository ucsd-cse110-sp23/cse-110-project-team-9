package sayit.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.qa.Answer;
import sayit.qa.Question;
import sayit.qa.QuestionAnswerEntry;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TsvStoreTest {
    private static final String TEST_FILE = "test.tsv";
    private IStore<QuestionAnswerEntry> _store;

    @BeforeEach
    public void setUp() {
        _store = TsvStore.createOrOpenStore(TEST_FILE);
    }

    @Test
    public void testAddGetDelete() {
        _store.insert(new QuestionAnswerEntry(new Question("What is 1 + 1?"), new Answer("2")));
        _store.insert(new QuestionAnswerEntry(new Question("What is 2 + 2?"), new Answer("4")));
        _store.insert(new QuestionAnswerEntry(new Question("What is 3 + 3?"), new Answer("6")));

        assertEquals(3, _store.size());

        assertEquals(new QuestionAnswerEntry(new Question("What is 1 + 1?"), new Answer("2")), _store.get(0));
        assertEquals(new QuestionAnswerEntry(new Question("What is 2 + 2?"), new Answer("4")), _store.get(1));
        assertEquals(new QuestionAnswerEntry(new Question("What is 3 + 3?"), new Answer("6")), _store.get(2));

        assertTrue(_store.delete(0));
        assertEquals(2, _store.size());
        assertNull(_store.get(0));
        assertEquals(new QuestionAnswerEntry(new Question("What is 2 + 2?"), new Answer("4")), _store.get(1));
        assertEquals(new QuestionAnswerEntry(new Question("What is 3 + 3?"), new Answer("6")), _store.get(2));
    }

    @Test
    public void testLoadData() {
        _store.insert(new QuestionAnswerEntry(new Question("Greg"), new Answer("Miranda")));
        _store.insert(new QuestionAnswerEntry(new Question("Joe"), new Answer("Politz")));
        _store.insert(new QuestionAnswerEntry(new Question("Paul"), new Answer("Cao")));
        _store.insert(new QuestionAnswerEntry(new Question("Niema"), new Answer("Moshiri")));
        _store.insert(new QuestionAnswerEntry(new Question("Daniel"), new Answer("Kane")));
        _store.insert(new QuestionAnswerEntry(new Question("Miles"), new Answer("Jones")));

        assertTrue(_store.save());

        IStore<QuestionAnswerEntry> store2 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store2);
        assertEquals(6, store2.size());
        assertEquals(new QuestionAnswerEntry(new Question("Greg"), new Answer("Miranda")), store2.get(0));
        assertEquals(new QuestionAnswerEntry(new Question("Joe"), new Answer("Politz")), store2.get(1));
        assertEquals(new QuestionAnswerEntry(new Question("Paul"), new Answer("Cao")), store2.get(2));
        assertEquals(new QuestionAnswerEntry(new Question("Niema"), new Answer("Moshiri")), store2.get(3));
        assertEquals(new QuestionAnswerEntry(new Question("Daniel"), new Answer("Kane")), store2.get(4));
        assertEquals(new QuestionAnswerEntry(new Question("Miles"), new Answer("Jones")), store2.get(5));
    }

    @Test
    public void testLoadDataEdit() {
        _store.insert(new QuestionAnswerEntry(new Question("CSE"), new Answer("Computer Science & Engineering")));
        _store.insert(new QuestionAnswerEntry(new Question("ECE"), new Answer("Electrical & Computer Engineering")));
        _store.insert(new QuestionAnswerEntry(new Question("MAE"), new Answer("Mechanical & Aerospace Engineering")));
        _store.insert(new QuestionAnswerEntry(new Question("BENG"), new Answer("Bioengineering")));
        _store.insert(new QuestionAnswerEntry(new Question("NANO"), new Answer("Nanoengineering")));
        _store.insert(new QuestionAnswerEntry(new Question("CENG"), new Answer("Chemical Engineering")));
        _store.insert(new QuestionAnswerEntry(new Question("SE"), new Answer("Structural Engineering")));
        _store.insert(new QuestionAnswerEntry(new Question("MATH"), new Answer("Mathematics")));
        _store.insert(new QuestionAnswerEntry(new Question("COGS"), new Answer("Cognitive Science")));
        _store.insert(new QuestionAnswerEntry(new Question("POLI"), new Answer("hello")));

        assertTrue(_store.save());

        IStore<QuestionAnswerEntry> store2 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store2);
        assertEquals(10, store2.size());
        assertEquals(new QuestionAnswerEntry(new Question("CSE"), new Answer("Computer Science & Engineering")), store2.get(0));
        assertEquals(new QuestionAnswerEntry(new Question("ECE"), new Answer("Electrical & Computer Engineering")), store2.get(1));
        assertEquals(new QuestionAnswerEntry(new Question("MAE"), new Answer("Mechanical & Aerospace Engineering")), store2.get(2));
        assertEquals(new QuestionAnswerEntry(new Question("BENG"), new Answer("Bioengineering")), store2.get(3));
        assertEquals(new QuestionAnswerEntry(new Question("NANO"), new Answer("Nanoengineering")), store2.get(4));
        assertEquals(new QuestionAnswerEntry(new Question("CENG"), new Answer("Chemical Engineering")), store2.get(5));
        assertEquals(new QuestionAnswerEntry(new Question("SE"), new Answer("Structural Engineering")), store2.get(6));
        assertEquals(new QuestionAnswerEntry(new Question("MATH"), new Answer("Mathematics")), store2.get(7));
        assertEquals(new QuestionAnswerEntry(new Question("COGS"), new Answer("Cognitive Science")), store2.get(8));
        assertEquals(new QuestionAnswerEntry(new Question("POLI"), new Answer("hello")), store2.get(9));

        assertTrue(store2.delete(0));
        assertTrue(store2.delete(1));
        assertTrue(store2.delete(2));
        assertTrue(store2.save());

        IStore<QuestionAnswerEntry> store3 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store3);
        assertEquals(7, store3.size());
        assertNull(store2.get(0));
        assertNull(store2.get(1));
        assertNull(store2.get(2));
        assertEquals(new QuestionAnswerEntry(new Question("BENG"), new Answer("Bioengineering")), store3.get(3));
        assertEquals(new QuestionAnswerEntry(new Question("NANO"), new Answer("Nanoengineering")), store3.get(4));
        assertEquals(new QuestionAnswerEntry(new Question("CENG"), new Answer("Chemical Engineering")), store3.get(5));
        assertEquals(new QuestionAnswerEntry(new Question("SE"), new Answer("Structural Engineering")), store3.get(6));
        assertEquals(new QuestionAnswerEntry(new Question("MATH"), new Answer("Mathematics")), store3.get(7));
        assertEquals(new QuestionAnswerEntry(new Question("COGS"), new Answer("Cognitive Science")), store3.get(8));
        assertEquals(new QuestionAnswerEntry(new Question("POLI"), new Answer("hello")), store3.get(9));

        assertFalse(store3.delete(0));
        assertFalse(store3.delete(1111));

        assertTrue(store3.save());

        IStore<QuestionAnswerEntry> store4 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store4);
        assertEquals(7, store4.size());
        assertNull(store4.get(0));
        assertNull(store4.get(1));
        assertNull(store4.get(2));
        assertEquals(new QuestionAnswerEntry(new Question("BENG"), new Answer("Bioengineering")), store4.get(3));
        assertEquals(new QuestionAnswerEntry(new Question("NANO"), new Answer("Nanoengineering")), store4.get(4));
        assertEquals(new QuestionAnswerEntry(new Question("CENG"), new Answer("Chemical Engineering")), store4.get(5));
        assertEquals(new QuestionAnswerEntry(new Question("SE"), new Answer("Structural Engineering")), store4.get(6));
        assertEquals(new QuestionAnswerEntry(new Question("MATH"), new Answer("Mathematics")), store4.get(7));
        assertEquals(new QuestionAnswerEntry(new Question("COGS"), new Answer("Cognitive Science")), store4.get(8));
        assertEquals(new QuestionAnswerEntry(new Question("POLI"), new Answer("hello")), store4.get(9));

        assertTrue(store4.delete(9));
        assertEquals(10, store4.insert(new QuestionAnswerEntry(new Question("POLI"), new Answer("Political Science"))));
        assertTrue(store4.delete(4));

        assertTrue(store4.save());

        IStore<QuestionAnswerEntry> store5 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store5);
        assertEquals(6, store5.size());
        assertNull(store5.get(0));
        assertNull(store5.get(1));
        assertNull(store5.get(2));
        assertEquals(new QuestionAnswerEntry(new Question("BENG"), new Answer("Bioengineering")), store5.get(3));
        assertNull(store5.get(4));
        assertEquals(new QuestionAnswerEntry(new Question("CENG"), new Answer("Chemical Engineering")), store5.get(5));
        assertEquals(new QuestionAnswerEntry(new Question("SE"), new Answer("Structural Engineering")), store5.get(6));
        assertEquals(new QuestionAnswerEntry(new Question("MATH"), new Answer("Mathematics")), store5.get(7));
        assertEquals(new QuestionAnswerEntry(new Question("COGS"), new Answer("Cognitive Science")), store5.get(8));
        assertNull(store5.get(9));
        assertEquals(new QuestionAnswerEntry(new Question("POLI"), new Answer("Political Science")), store5.get(10));
    }

    @Test
    public void testEmptyStore() {
        assertEquals(0, _store.size());
        assertNull(_store.get(0));
        assertNull(_store.get(1));

        assertFalse(_store.delete(0));
        assertFalse(_store.delete(1));

        assertTrue(_store.save());

        IStore<QuestionAnswerEntry> store2 = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store2);
        assertEquals(0, store2.size());

        assertNull(store2.get(0));
        assertNull(store2.get(1));
    }

    @Test
    public void testGetAllMap() {
        _store.insert(new QuestionAnswerEntry(new Question("CSE"), new Answer("Computer Science & Engineering")));
        _store.insert(new QuestionAnswerEntry(new Question("ECE"), new Answer("Electrical & Computer Engineering")));

        var map = _store.getEntries();
        assertEquals(2, map.size());
        assertEquals(new QuestionAnswerEntry(new Question("CSE"), new Answer("Computer Science & Engineering")), map.get(0));
        assertEquals(new QuestionAnswerEntry(new Question("ECE"), new Answer("Electrical & Computer Engineering")), map.get(1));
    }

    @AfterEach
    public void tearDown() {
        _store.clearAll();
        assertFalse(new File(TEST_FILE).exists());
    }
}
