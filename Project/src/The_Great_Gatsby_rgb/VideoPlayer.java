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
import java.util.*;
import java.util.List;

public class VideoPlayer {
    int width = 480;
    int height = 270;
    double fps = 30;
    int numFrames = 5686;
    //BufferedImage image;
    BufferedImage[] frames;
    JFrame frame = null;
    JLabel label= null;
    boolean isPaused = false;
    boolean isStopped = false;
    List<Integer> timeStamps = new ArrayList<>();
    List<Double> timeStampsDiff = new ArrayList<>();

    JButton lastClicked = null;
    Map<Integer, JButton> buttons = new HashMap<>();

    //ptr for marking video starting frame
    int location = 0;

    double threshold = 0.75;

    String rgb;


    public VideoPlayer(String rgb){

        this.rgb = rgb;
        analyze();
        //calculateTone();
    }

    public void analyze(){

        try {
            File file = new File(rgb);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(width * height * 3);

            numFrames = (int) (channel.size() / (width * height * 3));
            frames = new BufferedImage[numFrames];
            System.out.println("Frames: " + channel.size() / (width * height * 3));
            //calculate transition
            double[][][] histogram1 = new double[256][256][256];
            double[][][] histogram2 = new double[256][256][256];
            double prev = -1;
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
                    if (diff >= threshold){


                        if (prev < 0){

                            prev = i;
                            timeStamps.add(i);
                            System.out.println("Scene");
                            System.out.println("Frame: " + i);
                            System.out.println("Time Stamp: " + (i / 30 / 60) + " : " + (i / 30 - i / 30 / 60 * 60));
                            System.out.println("Diff: " + diff);

                            timeStampsDiff.add(diff);
                        }
                        else{

                            //differs more than 80 frames
                            if (i - prev > 60) {

                                System.out.println("Scene");
                                timeStamps.add(i);
                                System.out.println("Frame: " + i);
                                System.out.println("Time Stamp: " + (i / 30 / 60) + " : " + (i / 30 - i / 30 / 60 * 60));
                                System.out.println("Diff: " + diff);

                                timeStampsDiff.add(diff);
                                prev = i;
                            }

                        }
                    }
                }

            }

            //video play gui
            frame = new JFrame("Video Display");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(new Dimension(800, 600));

            JPanel video = new JPanel();
            JPanel scroll = new JPanel();
            label = new JLabel();
            label.setPreferredSize(new Dimension(width, height));
            video.add(label);

            // control panel
            JPanel control = new JPanel();
            control.setSize(new Dimension(width, 100));
            JButton pause = new JButton("Pause");
            pause.setSize(50, 20);
            pause.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // pause/start audio
                    if (!isPaused && !isStopped){

                        PlaySound.click();
                        isPaused = true;
                    }
                }
            });


            JButton play = new JButton("Play");
            play.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    if (isPaused){

                        isPaused = false;
                        PlaySound.click();
                    }
                    else if (isStopped){

                        //reset to start point
                        location = 0;
                        PlaySound.setFrame(0);

                        //repaint frame to frame 0
                        label.setIcon(new ImageIcon(frames[timeStamps.get(0)]));
                        frame.validate();
                        frame.repaint();

                        //start sound and video
                        isStopped = false;
                        isPaused = false;
                        PlaySound.clip.start();

                    }
                }
            });

            JButton stop = new JButton("Stop");
            stop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    //go back to the beginning of the segment
                    lastClicked.doClick();
                }
            });
            control.add(play);
            control.add(pause);
            control.add(stop);
            frame.add(control, BorderLayout.SOUTH);

            frame.add(video, BorderLayout.EAST);

            //timestamps

            scroll.setSize(width, height + 150);
            JScrollPane scrollPane = new JScrollPane(scroll, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
            scrollPane.setWheelScrollingEnabled(true);

            timeStampsDiff.add(0, 0.0);
            List<Double> copy = new ArrayList<>(timeStampsDiff);
            Collections.sort(copy);
            timeStamps.add(0, 0);

            double sceneDiff = 0, shotDiff = 0;

            if (timeStamps.size() <= 16){

                sceneDiff = copy.get((int) (Math.ceil(0.65 * copy.size()) - 1));
                shotDiff = copy.get((int) (Math.ceil(0.30 * copy.size()) - 1));
            }
            else{
                sceneDiff = copy.get((int) (Math.ceil(0.75 * copy.size()) - 1));
                shotDiff = copy.get((int) (Math.ceil(0.25 * copy.size()) - 1));
            }


            System.out.println("Scene cut-off: " + sceneDiff);
            System.out.println("Shot cut-off: " + shotDiff);
            int currScene = 0, currShot = 0, currSubShot = 0;
            for (int i = 0; i < timeStamps.size(); i++){


                if (i == 0 || timeStampsDiff.get(i) >= sceneDiff){

                    JLabel scene = new JLabel("Scene " + ++currScene);
                    scroll.add(scene);
                    JLabel shot = new JLabel("Shot 1" );
                    scroll.add(shot);
                    currShot = 1;
                    currSubShot = 0;
                }
                else if (timeStampsDiff.get(i) >= shotDiff){

                    JLabel shot = new JLabel("Shot " + ++currShot);
                    scroll.add(shot);
                    currSubShot = 0;

                }
                else{

                    JLabel subShot = new JLabel("Subshot " + ++currSubShot);
                    scroll.add(subShot);
                }




                JButton stamp = new JButton("Frame " + timeStamps.get(i));
                //add event listener for clicking time stamp, so video and audio will be set to correct location
                int finalI = i;

                buttons.put(timeStamps.get(i), stamp);
                stamp.setBackground(new Color(255, 255,255));
                stamp.setForeground(new Color(0, 0, 0));
                stamp.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        //1. pause video if not paused already
                        pause.doClick();

                        //2. set video to correct frame
                        label.setIcon(new ImageIcon(frames[timeStamps.get(finalI)]));
                        frame.validate();
                        frame.repaint();
                        location = timeStamps.get(finalI);


                        //3. set audio to correct bytes
                        int time = (int) (timeStamps.get(finalI) / ((double)(numFrames)) * PlaySound.length);
                        PlaySound.setFrame(time);

                        if (lastClicked != null){

                            lastClicked.setBackground(new Color(255, 255,255));
                            lastClicked.setForeground(new Color(0, 0, 0));
                        }

                        stamp.setBackground(new Color(0, 0, 0));
                        stamp.setForeground(new Color(255, 255,255));



                        lastClicked = stamp;

                    }
                });





                scroll.add(stamp);
            }


            frame.add(scrollPane, BorderLayout.WEST);
            frame.validate();
            frame.setVisible(true);

            //update the status


        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void playVideo(){
        for (;location < numFrames + 1;){


            if (!isPaused && !isStopped && location < numFrames){





                if (buttons.containsKey(location)){

                    if (lastClicked != null){

                        lastClicked.setBackground(new Color(255, 255,255));
                        lastClicked.setForeground(new Color(0,0,0));
                    }

                    lastClicked = buttons.get(location);
                    lastClicked.setForeground(new Color(255, 255, 255));
                    lastClicked.setBackground(new Color(0,0,0));
                }


                label.setIcon(new ImageIcon(frames[location]));
                frame.validate();
                frame.repaint();
                location++;
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
