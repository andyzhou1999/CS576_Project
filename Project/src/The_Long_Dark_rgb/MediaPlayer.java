package The_Long_Dark_rgb;


public class MediaPlayer {

    public static void main(String[] args){

        Thread video = new Thread(new Runnable() {
            @Override
            public void run() {
                VideoPlayer.playVideo();
            }
        });

        Thread audio = new Thread(new Runnable() {
            @Override
            public void run() {
                PlaySound ps = new PlaySound();
                try {
                    ps.play();
                } catch (PlayWaveException e) {
                    e.printStackTrace();
                }
            }
        });



        video.start();
        //audio.start();

    }
}
