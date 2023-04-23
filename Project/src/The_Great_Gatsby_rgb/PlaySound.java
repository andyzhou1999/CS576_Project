package The_Great_Gatsby_rgb;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.*;

/**
 * 
 * <Replace this with a short description of the class.>
 * 
 * @author Giulio
 */
public class PlaySound {

    private InputStream waveStream;
	private static boolean isPaused = false;
    private final int EXTERNAL_BUFFER_SIZE = 6000; // 128Kb
	//private final int EXTERNAL_BUFFER_SIZE = 2000 * 2; //64kb
	static SourceDataLine dataLine = null;
	static AudioInputStream audioInputStream = null;
	static Clip clip = null;
	static long frame = 0;
	static int length = 0;
    /**
     * CONSTRUCTOR
     */

	public PlaySound() {

	}

	public static void setFrame(int frame){

		clip.stop();
		clip.close();
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File("src/The_Great_Gatsby_rgb/InputAudio.wav"));
			clip.open(audioInputStream);
			clip.setFramePosition(frame);

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
			audioInputStream = AudioSystem.getAudioInputStream(new File("src/The_Great_Gatsby_rgb/InputAudio.wav"));
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
