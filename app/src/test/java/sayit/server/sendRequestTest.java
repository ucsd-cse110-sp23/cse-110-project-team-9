package sayit.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

//test the request sender with mock server and handler
//check for successfull requests and error handling
public class sendRequestTest{


    @Test
    public void testGet() throws IOException{
        assertEquals("GET", RequestSender.sendGetRequest("/history"));
        assertEquals("Invalid GET request", RequestSender.sendGetRequest("/entry"));
    }

    @Test
    public void testPost() throws IOException{
        File testFile = new File("app/src/test/java/sayit/server/test.wav");
        assertEquals("POST", RequestSender.sendPostRequest("/ask", testFile));
        assertEquals("Invalid POST request", RequestSender.sendPostRequest("/send", null));
    }

    @Test
    public void testDelete() throws IOException{
        assertEquals("DELETE{5}", RequestSender.sendDeleteRequest("/delete-question", "5"));
        assertEquals("CLEAR", RequestSender.sendDeleteRequest("/clear-all", "5"));
        assertEquals("Invalid DELETE request", RequestSender.sendDeleteRequest("/delete-question", "potato"));
        assertEquals("Invalid DELETE request", RequestSender.sendDeleteRequest("/delete", "5"));
    }

}