package Ready_Player_One_rgb;


import The_Long_Dark_rgb.PlayWaveException;

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



            }
        });


        audio.start();
        video.start();

    }
}