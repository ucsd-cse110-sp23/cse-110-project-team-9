package sayit.server;
import java.io.IOException;

import sayit.*;

public class ServerTestHelper {
    public static void main(String args[]) throws IOException{
        AudioFileConverter converter = new AudioFileConverter();
        String base64Data = AudioFileConverter.convertToBase64("app/src/test/java/sayit/server/audio.wav");
        System.out.println(base64Data);
    }
}