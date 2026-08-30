package view.gui;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Classe qui gére la lecture des sons et musiques
 * 
 * @author MESNILDREY Valentin
 * 
 * @see FloatControl#getClass()
 * @see AudioInputStream#getClass()
 * @see AudioSystem#getClass()
 * @see File#getClass()
 * @see IOException#getClass()
 * @see UnsupportedAudioFileException#getClass()
 * @see LineUnavailableException#getClass()
 * 
 * @since 2.1
 * 
 * @version 2.1 
 */
public class SoundPlayer {
    /**
     * La musique de fond
     */
     Clip clip;
    /**
     * L'effet sonore
     */
     Clip clipSoundClip;
    /**
     * Le volume de la musique de fond
     */
     FloatControl volumeControl;
    /**
     * Le son de marche
     */
     Clip ClipRun;
    /**
     * Le volume de l'effet sonore
     */
     FloatControl volumeControlSoundEffect;

    /**
     * Méthode qui joue la musique de fond à partir du chemin de fichier donné avec un fondu en entrée
     * 
     * @param filePath - chemin du fichier audio
     * @param fadeMillis - durée du fondu en entrée en millisecondes
     * 
     * @see AudioInputStream#getClass()
     * @see AudioSystem#getClass()
     * @see File#getClass()
     * @see IOException#getClass()
     * @see UnsupportedAudioFileException#getClass()
     * @see LineUnavailableException#getClass()
     * 
     * @since 2.1
     */
    public void playBackgroundMusic(String filePath, int fadeMillis) {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioInput);

            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volumeControl.getMinimum());

            clip.loop(Clip.LOOP_CONTINUOUSLY);

            clip.start();

            fadeIn(fadeMillis);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode de fondu en entrée pour la musique de fond
     * 
     * @param fadeMillis - durée du fondu en entrée en millisecondes
     * 
     * @since 2.1
     */
    private void fadeIn(int fadeMillis) {
        float min = volumeControl.getMinimum();
        float max = -20.0f;
        new Thread(() -> {
            try {

                float range = max - min;
                int steps = 50;
                float step = range / steps;
                int sleep = fadeMillis / steps;

                for (int i = 0; i < steps; i++) {
                    float newValue = volumeControl.getValue() + step;
                    volumeControl.setValue(Math.min(newValue, max));
                    Thread.sleep(sleep);
                }

                volumeControl.setValue(max);

            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    /**
     * Méthode qui arrête la musique de fond
     * 
     * @since 2.1
     */
    public void stopMusic() {
        if (clip != null && clip.isRunning())
            clip.stop();
    }

    /**
     * Méthode qui sert à jouer un effet sonore à partir du chemin de fichier donné
     * 
     * @param filePath - chemin du fichier audio
     * 
     * @see AudioInputStream#getClass()
     * @see AudioSystem#getClass()
     * @see File#getClass()
     * @see IOException#getClass()
     * @see UnsupportedAudioFileException#getClass()
     * @see LineUnavailableException#getClass()
     * 
     * @since 2.1
     */
    public void playSoundEffect(String filePath) {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
            clipSoundClip = AudioSystem.getClip();
            clipSoundClip.open(audioInput);

            volumeControlSoundEffect = (FloatControl) clipSoundClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControlSoundEffect.setValue(-20.0f);

            clipSoundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui sert à charger le son de marche
     * 
     * @since 2.1
     */
    public void loadRunSound() {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(
                    new File(getClass().getResource("/sounds/sounds_effects/run.wav").getPath()));
            ClipRun = AudioSystem.getClip();
            ClipRun.open(audioInput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui sert à jouer le son de marche en boucle
     * 
     * @since 2.1
     */
    public void playRunSound() {
        if (ClipRun != null) {
            if (!ClipRun.isRunning()) {
                ClipRun.setFramePosition(0);
                ClipRun.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    /**
     * Méthode qui arrête le son de marche
     */
    public void stopRunSound() {
        if (ClipRun != null && ClipRun.isRunning()) {
            ClipRun.stop();
        }
    }
}
