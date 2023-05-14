package sayit.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//test the request sender with mock server and handler
public class sendRequestTest{


    @Test
    public void testGet(){
        RequestSender.sendGetRequest(null);
    }

    @Test
    public void testPost(){
        RequestSender.sendGetRequest(null);
    }

    @Test
    public void testDelete(){
        RequestSender.sendGetRequest(null);
    }

    @Test
    public void testPut(){
        RequestSender.sendGetRequest(null);
    }
}