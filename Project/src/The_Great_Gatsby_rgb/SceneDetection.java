package The_Great_Gatsby_rgb;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SceneDetection {
    
    public static void main(String[] args) {


        File audioFile = new File("Project/src/The_Long_Dark_rgb/InputAudio.wav");
        
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            int numChannels = audioStream.getFormat().getChannels();
            int sampleSize = audioStream.getFormat().getSampleSizeInBits();
            float sampleRate = audioStream.getFormat().getSampleRate();
            long numFrames = audioStream.getFrameLength();
            
            // Read audio data into byte array
            byte[] audioData = new byte[(int) numFrames * numChannels * sampleSize / 8];
            int bytesRead = audioStream.read(audioData);
            audioStream.close();
            
            // Calculate RMS for each audio frame
            int frameSize = sampleSize / 8;
            double[] rmsValues = new double[(int) numFrames];
            for (int i = 0; i < numFrames; i++) {
                double sumSquares = 0;
                for (int j = 0; j < numChannels; j++) {
                    int sampleIndex = i * numChannels * frameSize + j * frameSize;
                    short sample = (short) (((audioData[sampleIndex + 1] & 0xff) << 8) | (audioData[sampleIndex] & 0xff));
                    sumSquares += sample * sample;
                }
                double rms = Math.sqrt(sumSquares / numChannels);
                rmsValues[i] = rms;
            }

            int[] timestamps = {0, 60, 313, 620, 798, 927, 1100, 1321, 1411, 1498, 1586, 2005, 2115, 2208, 2386, 2473, 2650, 2826, 3000, 3175, 3410, 3682, 4059, 4285, 4634, 4862, 4957, 5648};
            // example timestamps (in frame indices)
            int numSegments = timestamps.length - 1;
            double[] avgRMS = new double[numSegments];

            for (int i = 0; i < numSegments; i++) {
                int startFrame = timestamps[i];
                int endFrame = timestamps[i+1];
                double sumRMS = 0;
                for (int j = startFrame; j < endFrame; j++) {
                    sumRMS += rmsValues[j];
                }
                double segmentLength = endFrame - startFrame;
                avgRMS[i] = sumRMS / segmentLength;
            }


            for (int i = 0; i < numSegments; i++) {
                System.out.println("Frame " + timestamps[i]);
                System.out.println("avgRMS[i] = " + avgRMS[i]);
            }

            
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Frame 0
avgRMS[i] = 0.0
Frame 60
avgRMS[i] = 0.0
Frame 313
avgRMS[i] = 0.0
Frame 620
avgRMS[i] = 0.0
Frame 798
avgRMS[i] = 0.0
Frame 927
avgRMS[i] = 0.004087322434604321
Frame 1100
avgRMS[i] = 0.0
Frame 1321
avgRMS[i] = 0.0
Frame 1411
avgRMS[i] = 0.0
Frame 1498
avgRMS[i] = 0.0
Frame 1586
avgRMS[i] = 0.003375211366045573
Frame 2005
avgRMS[i] = 0.0
Frame 2115
avgRMS[i] = 0.0
Frame 2208
avgRMS[i] = 0.0
Frame 2386
avgRMS[i] = 0.0
Frame 2473
avgRMS[i] = 0.0
Frame 2650
avgRMS[i] = 0.004017652165832657
Frame 2826
avgRMS[i] = 0.0
Frame 3000
avgRMS[i] = 0.0
Frame 3175
avgRMS[i] = 0.0
Frame 3410
avgRMS[i] = 0.0
Frame 3682
avgRMS[i] = 0.001875614804208349
Frame 4059
avgRMS[i] = 0.0
Frame 4285
avgRMS[i] = 0.0
Frame 4634
avgRMS[i] = 0.0031013455315199454
Frame 4862
avgRMS[i] = 0.0
Frame 4957
avgRMS[i] = 0.0010233093794306044
    *
    * */
    
}
