package The_Great_Gatsby_rgb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.nio.file.Files;
import javax.swing.ImageIcon;

public class VideoPlayer {
    int width = 480;
    int height = 270;
    double fps = 34;
    int numFrames = 5686;
    //BufferedImage image;
    int[][][] images;
    int buffer_time = (int)fps*2;


    public VideoPlayer(){
        //image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //images = new int[width][height][34*5];

        read_rgb();
    }

    public void read_rgb() {
        try {
            File file = new File("/Users/evelynz/Documents/USC/大五下/576/project/CS576_Project/Project/src/The_Great_Gatsby_rgb/InputVideo.rgb");
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);

            //image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            images = new int[width][height][buffer_time];
            // read in files

            for (int i = 0; i < buffer_time; i++) {
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
                        images[x][y][i] = rgb;

                    }
                }

            }
            channel.close();
            raf.close();


        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public void playVideo(){

        // create the JFrame and JLabel to display the video
        JFrame frame = new JFrame("Video Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(width, height));
        frame.setVisible(true);
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        frame.add(label);

        //try read from the int array for five seconds
        for (int i = 0; i < buffer_time; i++) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = images[x][y][i];
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

        //read the rest of the file
        try{
            File file1 = new File("/Users/evelynz/Documents/USC/大五下/576/project/CS576_Project/Project/src/The_Great_Gatsby_rgb/InputVideo.rgb");
            RandomAccessFile raf = new RandomAccessFile(file1, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);

            for (int i = buffer_time; i < numFrames; i++) {
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

        // read the video file and display each frame
//        try {
//            RandomAccessFile raf = new RandomAccessFile(file, "r");
//            FileChannel channel = raf.getChannel();
//            ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);
//
//            //calculate transition
//            /*double[][][] histogram1 = new double[256][256][256];
//            double[][][] histogram2 = new double[256][256][256];
//
//            for (int i = 0; i < numFrames; i++){
//                buffer.clear();
//                channel.read(buffer);
//                buffer.rewind();
//
//
//
//                for (int y = 0; y < height; y++) {
//                    for (int x = 0; x < width; x++) {
//                        int r = buffer.get() & 0xff;
//                        int g = buffer.get() & 0xff;
//                        int b = buffer.get() & 0xff;
//
//                        if (i == 0){
//
//                            histogram1[r][g][b] += 1;
//                        }
//                        else{
//
//                            histogram2[r][g][b] += 1;
//                        }
//
//
//                    }
//                }
//
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
//                    if (diff >= 0.8){
//
//                        System.out.println("Time Stamp: " + (i / 30 / 60) + " : " + (i / 30 - i / 30 / 60 * 60));
//                    }
//                }
//            } */
//
//            //absolute difference
//            int[][] rgb1 = new int[height][width];
//            int[][] rgb2 = new int[height][width];
//            for (int i = 0; i < numFrames; i++){
//                buffer.clear();
//                channel.read(buffer);
//                buffer.rewind();
//
//
//
//                for (int y = 0; y < height; y++) {
//                    for (int x = 0; x < width; x++) {
//                        int r = buffer.get() & 0xff;
//                        int g = buffer.get() & 0xff;
//                        int b = buffer.get() & 0xff;
//                        int rgb = 0xff000000 |
//                                (r << 16) |
//                                (g << 8) |
//                                (b);
//
//                        if (i == 0){
//
//                            rgb1[y][x] = rgb;
//                        }
//                        else{
//
//                            rgb2[y][x] = rgb;
//                        }
//
//
//                    }
//                }
//
//                if (i != 0){
//
//                    double diff = 0;
//
//                    for (int y = 0; y < height; y++) {
//                        for (int x = 0; x < width; x++) {
//
//                            int r = Math.abs(((rgb2[y][x] - rgb1[y][x]) & 0x00ff0000) >> 16);
//                            int g = Math.abs(((rgb2[y][x] - rgb1[y][x]) & 0x0000ff00) >> 8);
//                            int b = Math.abs(((rgb2[y][x] - rgb1[y][x]) & 0xff));
//                            diff += Math.sqrt(r * r + g * g + b * b);
//                            rgb1[y][x] = rgb2[y][x];
//                            rgb2[y][x] = 0;
//                        }
//                    }
//
//                    //normalization
//                    diff /= 480 * 270;
//                    diff /= Math.sqrt(255 * 255 * 3);
//
//                    if (diff >= 0.62){
//
//                        System.out.println("Time Stamp: " + (i / 30 / 60) + " : " + (i / 30 - i / 30 / 60 * 60));
//                        System.out.println("Diff: " + diff);
//                    }
//
//                }
//            }
//
//
//            for (int i = 0; i < numFrames; i++) {
//                buffer.clear();
//                channel.read(buffer);
//                buffer.rewind();
//                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//                for (int y = 0; y < height; y++) {
//                    for (int x = 0; x < width; x++) {
//                        int r = buffer.get() & 0xff;
//                        int g = buffer.get() & 0xff;
//                        int b = buffer.get() & 0xff;
//                        int rgb = (r << 16) | (g << 8) | b;
//                        image.setRGB(x, y, rgb);
//                    }
//                }
//                label.setIcon(new ImageIcon(image));
//                frame.validate();
//                frame.repaint();
//                try {
//                    Thread.sleep((long) (1000 / fps));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            channel.close();
//            raf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




    }
}
