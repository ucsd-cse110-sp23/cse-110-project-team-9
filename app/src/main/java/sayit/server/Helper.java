package sayit.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class Helper {
    /*
     * method to take bytes of audio and convert to a file
     * @param audioBytes, byte array as created from server erquest
     * @throws IOException if there is a problem with the output stream
     * @return a string for the Path of the file which will be turned into a new file
     */
    public static String saveAudioFile(byte[] audioBytes) throws IOException {
        String soundFilePath = "question.wav"; // Provide the desired file path
        try (OutputStream outStream = new FileOutputStream(soundFilePath)) {
            outStream.write(audioBytes);
        }
        return soundFilePath;
    }
}
