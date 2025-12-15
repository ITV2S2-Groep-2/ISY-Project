package com.isy.gui.components;

import com.isy.util.GameSettings;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundUtils {

    private static Clip backgroundClip;
    public static final String standardPath = "src/main/resources/Sounds/";
    public static GameSettings gs = GameSettings.get();

    public static void playBackgroundMusic(String fileName) {
        stopBackgroundMusic();
        String filePath = standardPath + fileName;

        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            FloatControl gainControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gs.getBackgroundVolume());
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();

            System.out.println("Background music playing: " + filePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void pauseBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public static void resumeBackgroundMusic() {
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.start();
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
    }

    public static void updateBackgroundMusicVolume() {
        if (backgroundClip != null) {
            FloatControl gainControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gs.getBackgroundVolume());
        }
    }

    public static boolean isMusicPlaying() {
        return backgroundClip != null && backgroundClip.isRunning();
    }

    public static void playSoundEffect(String fileName) {
        String filePath = standardPath + fileName;
        new Thread(() -> {
            try {
                File audioFile = new File(filePath);
                if (!audioFile.exists()) {
                    System.err.println("Sound effect not found: " + filePath);
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(gs.getEffectsVolume());
                clip.start();

                // Vrijgeven zodra geluid klaar is
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
