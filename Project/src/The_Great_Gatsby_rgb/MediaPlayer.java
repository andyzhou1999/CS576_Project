package The_Great_Gatsby_rgb;

public class MediaPlayer {

    public static void main(String[] args){

        VideoPlayer vp = new VideoPlayer();
        PlaySound ps = new PlaySound(vp.timeStamps,vp.numFrames);

        for (int i = 0; i < vp.timeStamps.size(); i++) {
            System.out.print(vp.timeStamps.get(i) + " ");
        }

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
