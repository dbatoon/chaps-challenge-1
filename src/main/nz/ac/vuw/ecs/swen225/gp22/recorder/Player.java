package nz.ac.vuw.ecs.swen225.gp22.recorder;

import nz.ac.vuw.ecs.swen225.gp22.app.Base;
import nz.ac.vuw.ecs.swen225.gp22.app.GameButton;
import nz.ac.vuw.ecs.swen225.gp22.app.GameDialog;
import nz.ac.vuw.ecs.swen225.gp22.app.PhasePanel;
import nz.ac.vuw.ecs.swen225.gp22.persistency.Load;
import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;
import org.dom4j.DocumentException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.util.List;

/**
 * The player for the recorder. Used to play back recorded actions.
 *
 * @author Chris
 * @version 1.13
 */
public class Player extends JPanel {
    /**
     * The maximum value of playback speed.
     */
    private static final int MAX_SPEED = 5;

    /**
     * The base window.
     */
    private final Base base;

    /**
     * The list of game states to play back.
     */
    private List<GameState> gameStates;

    /**
     * The slider for playback scrubbing.
     */
    private JSlider scrubber;

    /**
     * The current action index.
     */
    private int currentAction = 0;

    /**
     * If the player is currently playing.
     */
    private boolean isPlaying = false;

    /**
     * If the player is currently rewinding.
     */
    private boolean isRewinding = false;

    /**
     * The playback speed.
     */
    private int speed = 1;

    /**
     * The play/pause button.
     */
    private GameButton playPause;

    /**
     * The game panel.
     */
    private PhasePanel gamePanel;

    /**
     * The speed button for increasing playback speed.
     */
    private GameButton speedBtn;

    /**
     * The default button dimensions.
     */
    private static final Dimension BUTTON_DIM = new Dimension(50, 30);

    /**
     * The default long button dimensions.
     */
    private static final Dimension LONG_BTN = new Dimension(BUTTON_DIM.width * 2, BUTTON_DIM.height);

    /**
     * The slider dimensions.
     */
    private static final Dimension SLIDER_DIM = new Dimension(700, 30);

    /**
     * Create a new player.
     *
     * @param base The base jFrame.
     */
    public Player(Base base) {
        assert SwingUtilities.isEventDispatchThread();
        this.base = base;
        load();
        setup();
        setVisible(true);
        scrubber.setValue(0);
    }

    /**
     * Set up the player.
     */
    private void setup() {
        gamePanel = base.getGameWindow();

        JButton stepBack = new GameButton("", BUTTON_DIM, e -> {
            scrubber.setValue(currentAction - 1);
            isPlaying = false;
            isRewinding = false;
        }, "stepback");

        scrubber = gameStates == null ? new JSlider() : new JSlider(0, gameStates.size());
        scrubber.setPreferredSize(SLIDER_DIM);
        scrubber.setValue(0);
        scrubber.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                scrub(source.getValue());
            }
        });
        scrubber.setBackground(GameConstants.BG_COLOR);
        scrubber.setUI(new BasicSliderUI(scrubber) {
            @Override
            public void paintThumb(Graphics g) {
                g.setColor(GameConstants.BG_COLOR_LIGHTER);
                g.fillOval(thumbRect.x, thumbRect.y, thumbRect.height - 2, thumbRect.height - 2);
                g.setColor(Color.GRAY);
                g.drawOval(thumbRect.x, thumbRect.y, thumbRect.height - 2, thumbRect.height - 2);
            }
        });

        JButton home = new GameButton("", BUTTON_DIM, e -> {
            base.menuScreen();
            isRewinding = false;
            isPlaying = false;
        }, "home");

        JButton load = new GameButton("Load", new Dimension(100, 30), e -> {
            try { load(); } catch (RuntimeException ignored) { } // If the user cancels the load, ignore it.
            if (gameStates != null) scrubber.setMaximum(gameStates.size());
        });

        JButton stepForward = new GameButton("", BUTTON_DIM, e -> {
            scrubber.setValue(currentAction + 1);
            isPlaying = false;
            isRewinding = false;
        }, "stepforward");

        JButton rewind = new GameButton("", LONG_BTN, e -> {
            playPause.changeIcon("pause");
            rewind();
        }, "rewind");

        playPause = new GameButton("", BUTTON_DIM, e -> {
            updatePlayBtn();
        }, "play");


        speedBtn = new GameButton("", LONG_BTN, e -> {
            updateSpeed();
        }, "speed" + this.speed);


        setLayout(new FlowLayout());
        add(gamePanel);
        add(stepBack);
        add(scrubber);
        add(stepForward);
        add(home);
        add(load);
        add(rewind);
        add(playPause);
        add(speedBtn);

        setPreferredSize(new Dimension(800, 520));
        setBackground(GameConstants.BG_COLOR);
    }

    /**
     * Update the speed of the replay.
     */
    private void updateSpeed() {
        speed = speed == MAX_SPEED ? 1 : speed + 1;
        speedBtn.changeIcon("speed" + this.speed);
    }

    /**
     * Update the play button icon.
     */
    private void updatePlayBtn() {
        if (isPlaying || isRewinding) {
            isPlaying = false;
            isRewinding = false;
            playPause.changeIcon("play");
        } else {
            isPlaying = true;
            playPause.changeIcon("pause");
            play();
        }
    }

    /**
     * Load a file.
     *
     * @throws RuntimeException If the user cancels the load.
     */
    private void load() throws RuntimeException {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/resources/recordings");
        fileChooser.setDialogTitle("Select a recording to play");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Replay File (xml)", "xml");
        fileChooser.setFileFilter(filter);
        fileChooser.showOpenDialog(this);

        // Only load if a file was selected
        if (fileChooser.getSelectedFile() != null) {
            try {
                Parser parser = new Parser(fileChooser.getSelectedFile());
                gameStates = parser.getStates();
                Load.loadLevel(parser.getLevel());
                if (scrubber != null) {
                    scrubber.setMaximum(gameStates.size() - 1);
                    currentAction = 0;
                    scrubber.setValue(0);
                    gamePanel.repaint();
                }
            } catch (DocumentException e) {
                new GameDialog(base, "Error: Invalid file.").visibleFocus();
                throw new IllegalArgumentException("Invalid file");
            }

            return;
        }
        throw new RuntimeException("No file selected");
    }

    /**
     * Scrub to a position.
     *
     * @param position the position to scrub to
     */
    private void scrub(int position) {
        if (gameStates == null) return;
        if (position > currentAction) {
            // scrub forward
            for (int i = currentAction; i < position; i++) {
                gameStates.get(i).apply(base);
                gamePanel.updateTime(60 - gameStates.get(i).getTime() / 1000);
                gamePanel.repaint();
            }
        } else if (position < currentAction) {
            // scrub backward
            for (int i = currentAction - 1; i >= position; i--) {
                gameStates.get(i).undo(base);
                gamePanel.updateTime(60 - gameStates.get(i).getTime() / 1000);
                gamePanel.repaint();
            }
        }
        currentAction = Math.min(position, gameStates.size());
    }

    /**
     * Rewind.
     */
    private void rewind() {
        isRewinding = true;
        isPlaying = false;
        new Thread(() -> {
            for (int i = currentAction; i >= 0; i--) {
                if (!isPlaying && !isRewinding) break;
                if (progress(i, isRewinding)) break;
            }
            isRewinding = false;
            playPause.changeIcon("play");
        }).start();
    }

    /**
     * Play the recording.
     */
    private void play() {
        isPlaying = true;
        isRewinding = false;
        new Thread(() -> {
            for (int i = currentAction; i <= gameStates.size(); i++) {
                if (!isPlaying && !isRewinding) break;
                if (progress(i, isPlaying)) break;
            }
            isPlaying = false;
            playPause.changeIcon("play");
        }).start();
    }

    /**
     * Used for playing and rewinding.
     *
     * @param i             The current action.
     * @param isProgressing If the player is progressing.
     * @return true if the player should stop progressing.
     */
    private boolean progress(int i, boolean isProgressing) {
        scrubber.setValue(i);
        try {
            Thread.sleep(1000 / speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return !isProgressing;
    }
}