package The_Great_Gatsby_rgb;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.nio.file.Files;

public class VideoPlayer {
    int width = 480;
    int height = 270;
    double fps = 30;
    int numFrames = 5686;
    //BufferedImage image;
    BufferedImage[] frames = new BufferedImage[numFrames];
    JFrame frame = null;
    JLabel label= null;
    boolean isPaused = false;

    public VideoPlayer(){
        analyze();
    }

    public void analyze(){

        try {
            File file = new File("src/The_Great_Gatsby_rgb/InputVideo.rgb");
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);

            //calculate transition
            double[][][] histogram1 = new double[256][256][256];
            double[][][] histogram2 = new double[256][256][256];

            for (int i = 0; i < numFrames; i++){
                buffer.clear();
                channel.read(buffer);
                buffer.rewind();


                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int r = buffer.get() & 0xff;
                        int g = buffer.get() & 0xff;
                        int b = buffer.get() & 0xff;
                        int rgb = (r << 16) | (g << 8) | b;
                        image.setRGB(x, y, rgb);

                        if (i == 0){

                            histogram1[r][g][b] += 1;
                        }
                        else{

                            histogram2[r][g][b] += 1;
                        }


                    }
                }

                frames[i] = image;

//                if (i != 0){
//
//                    double diff = 0;
//
//                    for (int j = 0; j < histogram1.length; j++){
//
//                        for (int k = 0; k < histogram1[j].length; k++){
//
//                            for (int l = 0; l < histogram1[k].length; l++){
//
//                                diff += Math.abs(histogram2[j][k][l] - histogram1[j][k][l]);
//                                histogram1[j][k][l] = histogram2[j][k][l];
//                                histogram2[j][k][l] = 0;
//                            }
//                        }
//                    }
//
//                    //normalization
//                    diff /= 480 * 270;
//                    if (diff >= 1.0){
//
//                        System.out.println("Time Stamp: " + (i / 30 / 60) + " : " + (i / 30 - i / 30 / 60 * 60));
//                    }
//                }

            }

            frame = new JFrame("Video Display");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(new Dimension(width, height + 200));
            frame.setVisible(true);
            label = new JLabel();
            label.setPreferredSize(new Dimension(width, height));
            frame.add(label);
            JPanel control = new JPanel();
            JButton play = new JButton("Pause");
            play.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // pause/start audio
                    PlaySound.click();
                    // pause/start video
                    isPaused = !isPaused;
                    if (play.getText().equals("Play")){

                        play.setText("Pause");
                    }
                    else{

                        play.setText("Play");
                    }
                }
            });
            control.add(play);
            frame.add(control, BorderLayout.SOUTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void playVideo(){
        for (int i = 0; i < numFrames;){

            if (!isPaused){
                label.setIcon(new ImageIcon(frames[i]));
                frame.validate();
                frame.repaint();
                i++;
            }
            else{


            }


            try {
                Thread.sleep((long) (1000 / fps));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
