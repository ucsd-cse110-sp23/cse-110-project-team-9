public class AudioRecorder {
    InternalAudioRecorder recorder;

    public AudioRecorder(){
        recorder = new InternalAudioRecorder();
    }

    private AudioInputStream startRecording(){
        recorder.run();
    }

    private void stopRecording() {
        recorder.stopRecording();
    }

    private AudioInputStream getRecording(){
      recorder.getValue();
    }
}


private class InternalAudioRecorder implements Runnable{
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
            recordingLabel.setVisible(true);
      
            // the AudioInputStream that will be used to write the audio data to a file
            AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
      
            // the file that will contain the audio data
            File audioFile = new File("recording.wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
            recordingLabel.setVisible(false);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
     }

     public AudioInputStream getValue() {
         return value;
     }
    
      private void stopRecording() {
        targetDataLine.stop();
        targetDataLine.close();
      }
}
