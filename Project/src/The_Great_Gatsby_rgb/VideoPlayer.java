package The_Great_Gatsby_rgb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.ImageIcon;

public class VideoPlayer {

    public static void playVideo(){
        File file = new File("/Users/andy117121/Desktop/CS576/CS576_Project/Project/src/The_Great_Gatsby_rgb/InputVideo.rgb"); // name of the RGB video file
        int width = 480; // width of the video frames
        int height = 270; // height of the video frames
        double fps = 34; // frames per second of the video
        int numFrames = 5686; // number of frames in the video

        // create the JFrame and JLabel to display the video
        JFrame frame = new JFrame("Video Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(width, height));
        frame.setVisible(true);
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        frame.add(label);

        // read the video file and display each frame
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);

            //calculate transition
            /*double[][][] histogram1 = new double[256][256][256];
            double[][][] histogram2 = new double[256][256][256];

            for (int i = 0; i < numFrames; i++){
                buffer.clear();
                channel.read(buffer);
                buffer.rewind();



                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int r = buffer.get() & 0xff;
                        int g = buffer.get() & 0xff;
                        int b = buffer.get() & 0xff;

                        if (i == 0){

                            histogram1[r][g][b] += 1;
                        }
                        else{

                            histogram2[r][g][b] += 1;
                        }


                    }
                }

                if (i != 0){

                    double diff = 0;

                    for (int j = 0; j < histogram1.length; j++){

                        for (int k = 0; k < histogram1[j].length; k++){

                            for (int l = 0; l < histogram1[k].length; l++){

                                diff += Math.abs(histogram2[j][k][l] - histogram1[j][k][l]);
                                histogram1[j][k][l] = histogram2[j][k][l];
                                histogram2[j][k][l] = 0;
                            }
                        }
                    }

                    //normalization
                    diff /= 480 * 270;
                    if (diff >= 0.8){

                        System.out.println("Time Stamp: " + (i / 30 / 60) + " : " + (i / 30 - i / 30 / 60 * 60));
                    }
                }
            } */

            //absolute difference
            int[][] rgb1 = new int[height][width];
            int[][] rgb2 = new int[height][width];
            for (int i = 0; i < numFrames; i++){
                buffer.clear();
                channel.read(buffer);
                buffer.rewind();



                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int r = buffer.get() & 0xff;
                        int g = buffer.get() & 0xff;
                        int b = buffer.get() & 0xff;
                        int rgb = 0xff000000 |
                                (r << 16) |
                                (g << 8) |
                                (b);

                        if (i == 0){

                            rgb1[y][x] = rgb;
                        }
                        else{

                            rgb2[y][x] = rgb;
                        }


                    }
                }

                if (i != 0){

                    double diff = 0;

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {

                            int r = Math.abs(((rgb2[y][x] - rgb1[y][x]) & 0x00ff0000) >> 16);
                            int g = Math.abs(((rgb2[y][x] - rgb1[y][x]) & 0x0000ff00) >> 8);
                            int b = Math.abs(((rgb2[y][x] - rgb1[y][x]) & 0xff));
                            diff += Math.sqrt(r * r + g * g + b * b);
                            rgb1[y][x] = rgb2[y][x];
                            rgb2[y][x] = 0;
                        }
                    }

                    //normalization
                    diff /= 480 * 270;
                    diff /= Math.sqrt(255 * 255 * 3);

                    if (diff >= 0.62){

                        System.out.println("Time Stamp: " + (i / 30 / 60) + " : " + (i / 30 - i / 30 / 60 * 60));
                        System.out.println("Diff: " + diff);
                    }

                }
            }


            for (int i = 0; i < numFrames; i++) {
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
                    }
                }
                label.setIcon(new ImageIcon(image));
                frame.validate();
                frame.repaint();
                try {
                    Thread.sleep((long) (1000 / fps));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            channel.close();
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
