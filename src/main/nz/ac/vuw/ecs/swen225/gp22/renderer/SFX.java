package nz.ac.vuw.ecs.swen225.gp22.renderer;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class constructs sounds clips to be played in the SFXPlayer class.
 *
 * @author Diana
 * @version 1.2
 */
public class SFX {
    /**
     * A clip to be played.
     */
    private final Clip clip;
    /**
     * The name of the clip to be played.
     */
    private final String clipName;

    /**
     * An enum of the sounds to be played during the game.
     */
    public enum Sounds {
        /**
         * The background music.
         */
        Background,
        /**
         * Music for collecting keys and treasures.
         */
        CollectItem,
        /**
         * Music for unlocking doors and the exit.
         */
        Unlock
    }

    /**
     * Loads a chosen file and initialises a clip from AudioInputStream.
     *
     * @param name Name of the the sound to be played.
     */
    public SFX(String name) {
        clipName = name;

        try {
            AudioInputStream audioIStream = AudioSystem.getAudioInputStream(new File("resources/sfx/" + name + ".wav"));
            AudioFormat format = audioIStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioIStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Input/Output Error: " + e);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Line Unavailable Exception Error: " + e);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            throw new RuntimeException("Sound: Unsupported Audio File: " + e);
        }
    }

    /**
     * @return the SFX clip.
     */
    public Clip getClip() {
        return clip;
    }

    /**
     * @return the SFX clip name.
     */
    public String getClipName() {
        return clipName;
    }

}
