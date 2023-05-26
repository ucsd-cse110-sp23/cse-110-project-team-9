package sayit;

import org.junit.jupiter.api.Test;
import sayit.common.qa.Answer;
import sayit.common.qa.Question;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.openai.MockChatGpt;
import sayit.openai.MockWhisper;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.OpenAiException;
import sayit.server.openai.WhisperCheck;
import sayit.server.storage.IStore;
import sayit.server.storage.TsvStore;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class WalkthroughTest {
    private static final String TEST_FILE = "test.tsv";

    @Test
    public void testWalkthrough() throws OpenAiException, IOException, InterruptedException {
        String[][] sampleQuestionAnswers = new String[][]{
                {"What is 1 + 1?", "2"},
                {"What is 2 + 2?", "4"},
                {"What is 3 + 3?", "6"},
                {"What is 4 + 4?", "8"},
                {"What is 5 + 5?", "10"},
                {"What is 6 + 6?", "12"},
                {"What is 7 + 7?", "14"},
                {"What is 8 + 8?", "16"},
                {"What is 9 + 9?", "18"},
                {"What is 10 + 10?", "20"},
        };

        // Let's create our store, which can be used to store any questions and answers
        IStore<QuestionAnswerEntry> store = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(store);

        // Iterate over each question and answer, and pretend that we're "asking" them.
        int idx = 0;
        for (String[] qa : sampleQuestionAnswers) {
            MockWhisper whisper = new MockWhisper(false, qa[0]);
            IChatGpt chatGpt = new MockChatGpt(false, qa[1]);
            WhisperCheck check = new WhisperCheck(whisper, null);

            // First, let's transcribe our message.
            String transcription = check.output();

            // Using the transcription, let's ask the question.
            String response = chatGpt.askQuestion(transcription);

            // Okay, let's now save the question and answer to a class.
            Question question = new Question(transcription);
            Answer answer = new Answer(response);
            QuestionAnswerEntry entry = new QuestionAnswerEntry(question, answer);

            // And let's now save the question and answer to the store.
            assertEquals(idx, store.insert(entry));
            idx++;
        }

        // Make sure we have the right number of entries.
        assertEquals(10, store.size());

        // Okay, let's suppose the user wants to look at their response to "What is 3 + 3?"
        // The user will press the "What is 3 + 3?" button, and we'll get the index of that button (2)
        // and use that to get the question and answer.
        assertEquals(new QuestionAnswerEntry(new Question("What is 3 + 3?"), new Answer("6")), store.get(2));

        // Now, let's suppose the user closes the application. Before the app closes, we should
        // probably save the store to disk.
        store.save();

        // Now, let's suppose the user opens the application again. We should load the store from disk.
        IStore<QuestionAnswerEntry> loadedStore = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(loadedStore);

        // Let's make sure the loaded store has the same number of entries as the original store.
        assertEquals(store.size(), loadedStore.size());

        // The user now clicks on the "What is 5 + 5?" button. We'll get the index of that button (4)
        // and use that to get the question and answer.
        assertEquals(new QuestionAnswerEntry(new Question("What is 5 + 5?"), new Answer("10")), loadedStore.get(4));

        // Cool. Maybe the user isn't happy with the 5 + 5 answer. Let's delete it.
        assertTrue(loadedStore.delete(4));
        assertNull(loadedStore.get(4));

        // The user now clicks on the "What is 10 + 10?" button. We'll get the index of that button (9).
        // and use that to get the question and answer.
        assertEquals(new QuestionAnswerEntry(new Question("What is 10 + 10?"), new Answer("20")), loadedStore.get(9));

        // The user now asks "What is 5 + 5?" again.
        MockWhisper whisper = new MockWhisper(false, "What is 5 + 5?");
        IChatGpt chatGpt = new MockChatGpt(false, "It's ten!");
        WhisperCheck check = new WhisperCheck(whisper, null);
        String transcription = check.output();
        String response = chatGpt.askQuestion(transcription);

        // Let's save the new question and answer to the store.
        Question question = new Question(transcription);
        Answer answer = new Answer(response);
        QuestionAnswerEntry entry = new QuestionAnswerEntry(question, answer);

        // And let's now save the question and answer to the store.
        loadedStore.insert(entry);

        // Finally, let's check that the new question and answer was saved.
        assertEquals(new QuestionAnswerEntry(new Question("What is 5 + 5?"), new Answer("It's ten!")), loadedStore.get(10));
        assertNull(loadedStore.get(4));

        // Okay, maybe the user wants to *clear all* of their questions and answers.
        assertTrue(loadedStore.clearAll());

        // And then suppose the user asks "What is the meaning of life?"
        whisper = new MockWhisper(false, "What is the meaning of life?");
        chatGpt = new MockChatGpt(false, "42");
        check = new WhisperCheck(whisper, null);
        transcription = check.output();
        response = chatGpt.askQuestion(transcription);

        // Let's save the new question and answer to the store.
        loadedStore.insert(new QuestionAnswerEntry(new Question(transcription), new Answer(response)));
        // And make sure it's actually there
        assertEquals(new QuestionAnswerEntry(new Question("What is the meaning of life?"), new Answer("42")), loadedStore.get(0));

        // Great. Let's save it once more.
        loadedStore.save();

        // And pretend the user closes the application and then opens it again.
        IStore<QuestionAnswerEntry> reloadedStore = TsvStore.createOrOpenStore(TEST_FILE);
        assertNotNull(reloadedStore);

        // And the user clicks on the "What is the meaning of life?" button.
        assertEquals(new QuestionAnswerEntry(new Question("What is the meaning of life?"), new Answer("42")), reloadedStore.get(0));

        // And the user is happy. :)
        // The end.
        assertTrue(reloadedStore.clearAll());
    }
}
