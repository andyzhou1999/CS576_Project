package The_Great_Gatsby_rgb;

public class MediaPlayer {

    public static void main(String[] args){

        String rgb = args[0];
        String wav = args[1];
        VideoPlayer vp = new VideoPlayer(rgb);
        PlaySound ps = new PlaySound(wav);


        Thread video = new Thread(new Runnable() {
            @Override
            public void run() {
                vp.playVideo();
            }
        });

        Thread audio = new Thread(new Runnable() {
            @Override
            public void run() {
                ps.play();
            }
        });



        video.start();
        audio.start();

    }
}
