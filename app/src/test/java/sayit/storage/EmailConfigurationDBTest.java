package sayit.storage;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.db.doctypes.SayItEmailConfiguration;
import sayit.server.db.doctypes.SayItPrompt;
import sayit.server.db.store.TsvEmailConfigurationHelper;
import sayit.server.db.store.TsvPromptHelper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests email configuration stuff, specifically methods in TsvEmailConfigurationHelper.java.
 */
public class EmailConfigurationDBTest {
    private static final String TEST_FILE = "test_email.tsv";
    private static final String DUMMY_USERNAME1 = "dummy";
    private static final String DUMMY_USERNAME2 = "Bob";
    private static final String DUMMY_USERNAME3 = "Alex";
    private static final String USER_NOT_USED = "Todd";
    private static final String QUESTION = "QUESTION";
    private IEmailConfigurationHelper helper = new TsvEmailConfigurationHelper(TEST_FILE);
    private static final SayItEmailConfiguration COMPLETE_1 = new SayItEmailConfiguration(DUMMY_USERNAME1,
            "Dummy", "me", "Dum", "dum@ucsd.edu", "forDummies",
            "25", "587");
    private static final SayItEmailConfiguration COMPLETE_2 = new SayItEmailConfiguration(DUMMY_USERNAME2,
            "Bob", "Sun", "Bobby", "bob@ucsd.edu", "Bonkers",
            "25", "587");
    private static final SayItEmailConfiguration COMPLETE_CHANGED_1 = new SayItEmailConfiguration(DUMMY_USERNAME1,
            "Dummy", "me", "Dummies", "dum@ucsd.edu", "forDummies",
            "25", "587");
    private static final SayItEmailConfiguration EMPTY = new SayItEmailConfiguration();
    private static final SayItEmailConfiguration ONLY_USERNAME = new SayItEmailConfiguration(DUMMY_USERNAME3,
            null, null, null, null, null, null, null);

    @BeforeEach
    public void setUp() {
        var file = new File(TEST_FILE);
        if (file.exists()) {
            assertTrue(file.delete());
        }
        helper = new TsvEmailConfigurationHelper(TEST_FILE);
    }

    @Test
    public void testAddGetDelete() {
        assertNull(helper.getEmailConfiguration(DUMMY_USERNAME1));
        helper.createEmailConfiguration(EMPTY); // if no error, then "test" passes
        helper.save();

        helper.createEmailConfiguration(ONLY_USERNAME);
        helper.save();
        assertNull(helper.getEmailConfiguration(DUMMY_USERNAME3));

        helper.createEmailConfiguration(COMPLETE_1);
        helper.save();
        assertEquals(COMPLETE_1, helper.getEmailConfiguration(COMPLETE_1.getAccUsername()));

        helper.createEmailConfiguration(COMPLETE_2);
        helper.save();
        assertEquals(COMPLETE_2, helper.getEmailConfiguration(COMPLETE_2.getAccUsername()));
        assertNull(helper.getEmailConfiguration(USER_NOT_USED));

        helper.replaceEmailConfiguration(COMPLETE_CHANGED_1);
        helper.save();
        assertEquals(COMPLETE_CHANGED_1, helper.getEmailConfiguration(COMPLETE_1.getAccUsername()));
        assertNotEquals(COMPLETE_1, helper.getEmailConfiguration(COMPLETE_1.getAccUsername()));

        helper.deleteEmailConfiguration(USER_NOT_USED); // if no error, then "test" passes

        helper.deleteEmailConfiguration(DUMMY_USERNAME1);
        helper.save();
        assertNull(helper.getEmailConfiguration(DUMMY_USERNAME1));
        assertEquals(COMPLETE_2, helper.getEmailConfiguration(DUMMY_USERNAME2));

        helper.deleteEmailConfiguration(DUMMY_USERNAME2);
        helper.save();
        assertNull(helper.getEmailConfiguration(DUMMY_USERNAME2));
    }

    @Test
    public void testLoadData() {

        helper.createEmailConfiguration(COMPLETE_1);
        helper.save();
        helper.createEmailConfiguration(COMPLETE_2);
        helper.save();

        IEmailConfigurationHelper helper2 = new TsvEmailConfigurationHelper(TEST_FILE);
        assertNotNull(helper2);
        assertEquals(COMPLETE_1, helper2.getEmailConfiguration(DUMMY_USERNAME1));
        assertEquals(COMPLETE_2, helper2.getEmailConfiguration(DUMMY_USERNAME2));
        assertNull(helper2.getEmailConfiguration(USER_NOT_USED));

    }

    @Test
    public void testLoadDataEdit() {

        helper.createEmailConfiguration(COMPLETE_1);
        helper.save();
        helper.createEmailConfiguration(COMPLETE_2);
        helper.save();

        //Testing loading from a file that already has two configurations
        IEmailConfigurationHelper helper2 = new TsvEmailConfigurationHelper(TEST_FILE);
        assertNotNull(helper2);
        assertEquals(COMPLETE_1, helper2.getEmailConfiguration(DUMMY_USERNAME1));
        assertEquals(COMPLETE_2, helper2.getEmailConfiguration(DUMMY_USERNAME2));
        assertNull(helper2.getEmailConfiguration(USER_NOT_USED));

        //Delete one configuration from file
        helper2.deleteEmailConfiguration(DUMMY_USERNAME1);
        helper2.save();
        assertNull(helper2.getEmailConfiguration(DUMMY_USERNAME1));

        //Testing loading from a file that only has one of two original configurations
        IEmailConfigurationHelper helper3 = new TsvEmailConfigurationHelper(TEST_FILE);
        assertNotNull(helper3);
        assertNull(helper3.getEmailConfiguration(DUMMY_USERNAME1));
        assertEquals(COMPLETE_2, helper3.getEmailConfiguration(DUMMY_USERNAME2));

        //Add an edited configuration that has the same username as the one originally deleted
        helper3.createEmailConfiguration(COMPLETE_CHANGED_1);
        helper3.save();
        assertEquals(COMPLETE_CHANGED_1, helper3.getEmailConfiguration(DUMMY_USERNAME1));

        //Testing loading from a file that has two configurations, one original, one that has same
        //username but now different fields
        IEmailConfigurationHelper helper4 = new TsvEmailConfigurationHelper(TEST_FILE);
        assertNotNull(helper4);
        assertEquals(COMPLETE_CHANGED_1, helper4.getEmailConfiguration(DUMMY_USERNAME1));
        assertEquals(COMPLETE_2, helper4.getEmailConfiguration(DUMMY_USERNAME2));
        helper4.deleteEmailConfiguration(DUMMY_USERNAME1);
        helper4.save();
        helper4.deleteEmailConfiguration(DUMMY_USERNAME2);
        helper4.save();

        //Testing loading from an empty file.
        IEmailConfigurationHelper helper5 = new TsvEmailConfigurationHelper(TEST_FILE);
        assertNotNull(helper5);
        assertNull(helper5.getEmailConfiguration(DUMMY_USERNAME1));
        assertNull(helper5.getEmailConfiguration(DUMMY_USERNAME2));
    }

    @AfterEach
    public void tearDown() {
        assertTrue(new File(TEST_FILE).delete());
    }

}
