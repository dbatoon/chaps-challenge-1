package nz.ac.vuw.ecs.swen225.gp22.renderer;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Plays a sound depending on the event happening in the game.
 *
 * @author Diana
 * @version 1.3
 */
public class SFXPlayer implements LineListener {
    /**
     * ArrayList that holds the sounds to be played in the game.
     */
    private final List<Clip> sounds = new ArrayList<>();
    /**
     * The current sound being played.
     */
    private Clip currentSound;
    /**
     * The priority level of the current sound which determines the sound to be prioritised and played.
     */
    private int currentSoundPriority;
    /**
     * Returns a boolean of whether a sound is currently playing.
     */
    private boolean isPlaying = true;

    /**
     * Plays a wav file depending on the name of the sound being passed.
     *
     * @param sfx           SFX object to be get the clip.
     * @param priorityLevel To determine which sound should be played over others.
     */
    public void playSound(SFX sfx, int priorityLevel) {
        // stop the song with the smaller priority
        if (priorityLevel > currentSoundPriority) {
            isPlaying = false;
        }

        // play a new sound on top of the other when collecting items or unlocking doors/exit
        if (sfx.getClipName().equals("collectItem") || sfx.getClipName().equals("Unlock")) {
            currentSoundPriority = 2;
        }

        // play a new sound when the current one stops
        if (!isPlaying) {
            currentSound = sfx.getClip();
            currentSound.addLineListener(this);
            currentSound.setFramePosition(0);
            currentSound.start();
            sounds.add(currentSound); // add to the list of current sounds
        }
    }

    /**
     * Stops all the current sounds at the creation or deletion of a level.
     */
    public void stopSFX() {
        sounds.forEach(Clip::stop);
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();

        // sent when a line begins to engage in active input/output of audio data in response to a start request
        if (type == LineEvent.Type.START) {
            isPlaying = true;
        }
        // sent when a line stops active input/output of audio data in response to a stop request
        else if (type == LineEvent.Type.STOP) {
            isPlaying = false;
            currentSoundPriority = 0;
            currentSound.stop();
        }
    }

}
