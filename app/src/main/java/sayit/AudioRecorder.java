package sayit;

import javax.sound.sampled.*;

/**
 * Represents an AudioRecorder instance, which can be used to record audio.
 */
public class AudioRecorder {
    InternalAudioRecorder recorder;

    /**
     * Creates a new AudioRecorder instance.
     */
    public AudioRecorder() {
        this.recorder = new InternalAudioRecorder();
    }

    /**
     * Starts recording audio.
     */
    public void startRecording() {
        this.recorder.run();
    }

    /**
     * Stops recording audio.
     */
    public void stopRecording() {
        this.recorder.stopRecording();
    }

    /**
     * Gets the internal audio recording instance.
     *
     * @return The internal audio recording instance.
     */
    public AudioInputStream getRecording() {
        return this.recorder.getValue();
    }
}


class InternalAudioRecorder implements Runnable {
    private AudioInputStream value;
    private TargetDataLine targetDataLine;

    /**
     * Gets the audio format used by the AudioRecorder.
     * @return The audio format used by the AudioRecorder.
     */
    private AudioFormat getAudioFormat() {
        // the number of samples of audio per second.
        // 44100 represents the typical sample rate for CD-quality audio.
        float sampleRate = 44100;
        // the number of bits in each sample of a sound that has been digitized.
        int sampleSizeInBits = 16;
        // the number of audio channels in this format (1 for mono, 2 for stereo).
        int channels = 2;
        // whether the data is signed or unsigned.
        boolean signed = true;
        // whether the audio data is stored in big-endian or little-endian order.
        boolean bigEndian = false;
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian
        );
    }

    /**
     * Starts recording audio.
     */
    @Override
    public void run() {
        try {
            AudioFormat audioFormat = this.getAudioFormat();
            // The format of the TargetDataLine
            DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class,
                    audioFormat
            );

            // The TargetDataLine used to capture audio data from the microphone
            this.targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            this.targetDataLine.open(audioFormat);
            this.targetDataLine.start();

            // The AudioInputStream that will be used to write the audio data to a file
            this.value = new AudioInputStream(targetDataLine);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the audio input stream.
     * @return The audio input stream.
     */
    public AudioInputStream getValue() {
        return this.value;
    }

    /**
     * Stops recording audio. Once stopped, the audio input stream can be retrieved using
     * <c>getValue</c>.
     */
    public void stopRecording() {
        this.targetDataLine.stop();
        this.targetDataLine.close();
    }
}
