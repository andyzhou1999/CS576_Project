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
    /**
     * CONSTRUCTOR
     */
    public PlaySound(InputStream waveStream) {
	this.waveStream = waveStream;
    }

	public PlaySound(){
		try {
			waveStream = new FileInputStream("src/The_Great_Gatsby_rgb/InputAudio.wav");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void click(){

		isPaused = !isPaused;
	}
    public void play() throws PlayWaveException {

	AudioInputStream audioInputStream = null;
	try {
	    //audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
		
		//add buffer for mark/reset support, modified by Jian
		InputStream bufferedIn = new BufferedInputStream(this.waveStream);
	    audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		
	} catch (UnsupportedAudioFileException e1) {
	    throw new PlayWaveException(e1);
	} catch (IOException e1) {
	    throw new PlayWaveException(e1);
	}

	// Obtain the information about the AudioInputStream
	AudioFormat audioFormat = audioInputStream.getFormat();
	Info info = new Info(SourceDataLine.class, audioFormat);

	// opens the audio channel

	try {

	    dataLine = (SourceDataLine) AudioSystem.getLine(info);
	    dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
	} catch (LineUnavailableException e1) {
	    throw new PlayWaveException(e1);
	}

		// Starts the music :P
	dataLine.start();

	int readBytes = 0;
	byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

	System.out.println("Audio length: " + audioInputStream.getFrameLength());

	try {
	    while (readBytes != -1) {

			if (!isPaused){
				dataLine.start();
				//if (dataLine.isActive()){
					readBytes = audioInputStream.read(audioBuffer, 0,
							audioBuffer.length);
					if (readBytes >= 0){
						dataLine.write(audioBuffer, 0, readBytes);
					}
				//}
			}
			else{

				dataLine.stop();
			}





	    }
	} catch (IOException e1) {
		e1.printStackTrace();
	} finally {
	    // plays what's left and and closes the audioChannel
	    dataLine.drain();
	    dataLine.close();
	}

    }
}
