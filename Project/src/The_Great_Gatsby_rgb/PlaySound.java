package The_Great_Gatsby_rgb;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.*;
import java.util.List;


/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {

    private InputStream waveStream;
	static boolean isPaused = false;
    private final int EXTERNAL_BUFFER_SIZE = 6000; // 128Kb
	//private final int EXTERNAL_BUFFER_SIZE = 2000 * 2; //64kb
	static SourceDataLine dataLine = null;
	static AudioInputStream audioInputStream = null;
	static Clip clip = null;
	static long frame = 0;
	static int length = 0;

	static String wav;

	int threshold = 60;
    /**
     * CONSTRUCTOR
     */

	public PlaySound() {

	}

	public PlaySound(List<Integer> timeStamps, int frames) {
		//analyze(timeStamps, frames);
	}

	public PlaySound(String wav) {

		PlaySound.wav = wav;
	}

	public void analyze(List<Integer> timeStamps, int frames){
		//add the 0 frame, and numFrame

		timeStamps.add(0,0);
		System.out.println(timeStamps);
		File audioFile = new File(wav);
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

			//int[] timestamps = {996, 1687, 1819, 2142, 2721, 2837, 3079, 3711, 3958, 4209, 4328, 4804, 5008};
			// example timestamps (in frame indices)
			int numSegments = timeStamps.size() - 1;
			double[] avgRMS = new double[numSegments];

			for (int i = 0; i < numSegments; i++) {
				int startFrame = timeStamps.get(i);
				int endFrame = timeStamps.get(i+1);
				double sumRMS = 0;
				for (int j = startFrame; j < endFrame; j++) {
					sumRMS += rmsValues[j];
				}
				double segmentLength = endFrame - startFrame;
				avgRMS[i] = sumRMS / segmentLength;
			}


			for (int i = 1; i < numSegments-1; i++) {
				//System.out.println("avgRMS" + i + " = " + avgRMS[i]);

				//if i detect an abrupt change in audio level
				if(avgRMS[i]-avgRMS[i-1] > threshold && avgRMS[i] - avgRMS[i+1] > threshold){
					System.out.println("scene change at " + avgRMS[i] );
				}
				else if(avgRMS[i-1]-avgRMS[i] > threshold && avgRMS[i+1] - avgRMS[i] > threshold){
					System.out.println("scene change at " + avgRMS[i] );
				}
			}


		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}


	public static void setFrame(int frame){

		clip.stop();
		clip.close();
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(wav));
			clip.open(audioInputStream);
			clip.setFramePosition(frame);
			clip.getFramePosition();

		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}


	public static void click(){

		isPaused = !isPaused;

		if (isPaused){

			clip.stop();
		}
		else{

			clip.start();
		}
	}
    public void play(){

		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(wav));
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);



			length = clip.getFrameLength();
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}



}
