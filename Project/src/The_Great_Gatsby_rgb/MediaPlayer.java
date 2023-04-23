package The_Great_Gatsby_rgb;

public class MediaPlayer {

    public static void main(String[] args){

        VideoPlayer vp = new VideoPlayer();

        Thread video = new Thread(new Runnable() {
            @Override
            public void run() {
                vp.playVideo();
            }
        });

        Thread audio = new Thread(new Runnable() {
            @Override
            public void run() {
                PlaySound ps = new PlaySound();
                ps.play();
            }
        });



        video.start();
        audio.start();

    }
}
