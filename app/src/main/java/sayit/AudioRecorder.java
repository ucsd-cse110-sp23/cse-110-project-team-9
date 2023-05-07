package sayit;

import javax.sound.sampled.*;

public class AudioRecorder {
    InternalAudioRecorder recorder;

    public AudioRecorder(){
        recorder = new InternalAudioRecorder();
    }

    public void startRecording(){
        recorder.run();
    }

    public void stopRecording() {
        recorder.stopRecording();
    }

    public AudioInputStream getRecording(){
      return recorder.getValue();
    }
}


class InternalAudioRecorder implements Runnable{
    private AudioInputStream value;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

    @Override
     public void run() {
        try {
            // the format of the TargetDataLine
            DataLine.Info dataLineInfo = new DataLine.Info(
              TargetDataLine.class,
              audioFormat
            );
            // the TargetDataLine used to capture audio data from the microphone
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
      
            // the AudioInputStream that will be used to write the audio data to a file
            value = new AudioInputStream(targetDataLine);

          } catch (Exception ex) {
            ex.printStackTrace();
          }
     }

     public AudioInputStream getValue() {
         return value;
     }
    
    public void stopRecording() {
        targetDataLine.stop();
        targetDataLine.close();
      }
}
