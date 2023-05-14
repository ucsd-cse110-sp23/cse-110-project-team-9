package sayit.server;

import java.io.File;
import java.io.IOException;

public class ScenarioTest {
    public static void main(String[] args) throws IOException{
        File testFile = new File("C:/Users/cmast/Desktop/CSE110/cse-110-project-team-9/app/src/test/java/sayit/server/test.wav");
        String response = RequestSender.sendPostRequest("/ask", testFile);
        System.out.println(response);
        
    }
    
}
