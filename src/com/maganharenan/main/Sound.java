package com.maganharenan.main;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

public class Sound extends JFrame {
    private Clip clip;

    public static Clips music = load("/level1.wav", 1);
    public static Clips getItem = load("/getItem.wav", 1);
    public static Clips hurt = load("/hurt.wav", 1);
    public static Clips shoot = load("/shoot.wav", 1);

    public static class Clips {
        public Clip[] clips;
        private int p;
        private int count;

        public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
            if (buffer == null) {
                return;
            }

            clips = new Clip[count];
            this.count = count;

            for (int index = 0; index < count; index++) {
                clips[index] = AudioSystem.getClip();
                clips[index].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
            }
        }

        public void play() {
            if (clips == null) {
                return;
            }

            clips[p].stop();
            clips[p].setFramePosition(0);
            clips[p].start();
            p++;

            if (p >= count) {
                p = 0;
            }
        }

        public void loop() {
            if (clips == null) {
                return;
            }
            clips[p].loop(1000);
        }
    }

    private static Clips load(String name, int count) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataInputStream dataInputStream = new DataInputStream(Sound.class.getResourceAsStream(name));

            byte[] buffer = new byte[1024];
            int read = 0;

            while ((read = dataInputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer,0,read);
            }

            dataInputStream.close();
            byte[] data = byteArrayOutputStream.toByteArray();

            return new Clips(data, count);
        } catch (Exception e) {
            try {
                return new Clips(null, 0);
            } catch (LineUnavailableException ex) {
                ex.printStackTrace();
                return null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            } catch (UnsupportedAudioFileException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    /*
    public void play(String name, int loop) {
        URL url = this.getClass().getClassLoader().getResource(name);
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(loop);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

     */
}
