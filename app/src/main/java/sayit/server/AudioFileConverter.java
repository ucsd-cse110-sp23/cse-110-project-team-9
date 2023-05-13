package sayit.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

//Simple class to convert an Audio file into Base64 so we can send it in an HTTP Request
public class AudioFileConverter {
    public static String convertToBase64(String audioFilePath) throws IOException {
        byte[] audioBytes = Files.readAllBytes(Path.of(audioFilePath));
        String base64Data = Base64.getEncoder().encodeToString(audioBytes);
        return base64Data;
    }
}
