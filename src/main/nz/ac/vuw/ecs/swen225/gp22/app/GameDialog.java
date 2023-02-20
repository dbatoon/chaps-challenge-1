package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Creates Pop-up boxes for our game. There are four types of pop-ups.
 * Pause, GameOver, GameComplete, Save
 *
 * @author Molly
 * @version 1.3
 */
public class GameDialog extends JDialog {

	/**
	 * Size of most buttons.
	 */
	private static final Dimension BUTTON_SIZE = new Dimension(180, 30);

	/**
	 * Pop-up width.
	 */
	private int width = 230;

	/**
	 * Pop-up height.
	 */
	private int height;

	/**
	 * X offset from main window.
	 */
	private int xOffset = GameConstants.WINDOW_WIDTH / 2 - width / 2;

	/**
	 * Y offset from main window.
	 */
	private int yOffset = GameConstants.WINDOW_HEIGHT / 4;

	/**
	 * To call actions on.
	 */
	private final Base base;

	/**
	 * Title of pop-up.
	 */
	private JLabel info;

	/**
	 * To time out a pop-up.
	 */
	private Timer timer = new Timer(20, e -> {
	});

	/**
	 * To keep track of how long to time out pop-up.
	 */
	private int timeMS = 0;

	/**
	 * For selecting different pop-ups.
	 */
	public enum PopUp {
		/**
		 * For defining a Pause pop-up.
		 */
		Pause,

		/**
		 * For defining a GameOver pop-up.
		 */
		GameOver,

		/**
		 * For defining a Game Won pop-up.
		 */
		GameCompleted,

		/**
		 * For defining a Save pop-up.
		 */
		Save;
	}

	/**
	 * New Dialog window, can create four types based on type passed in.
	 *
	 * @param base current base
	 * @param type Pause, GameOver, GameComplete, Save
	 */
	public GameDialog(Base base, PopUp type) {
		this.base = base;
		setUpWindow();
		List<JComponent> components = List.of();
		switch (type) {
			case Pause -> components = setUpPause();
			case GameOver -> components = setUpGameOver();
			case GameCompleted -> components = setUpGameCompleted();
			case Save -> components = setUpSave();
		}
		setUpComponents(components);
	}


	/**
	 * For error pop-ups.
	 *
	 * @param base    to set actions on
	 * @param message error message to display
	 */
	public GameDialog(Base base, String message) {
		this.base = base;
		setUpWindow();
		setUpComponents(setUpError(message));
	}

	/**
	 * Pop-up for Next Level.
	 *
	 * @param base  to call actions on
	 * @param level which level is the next level
	 */
	public GameDialog(Base base, int level) {
		this.base = base;
		setUpWindow();
		setUpComponents(setUpNextLevel(level));
	}

	/**
	 * Set up pop-up window.
	 */
	private void setUpWindow() {
		this.setLayout(new GridBagLayout());
		this.getContentPane().setBackground(GameConstants.LIGHT_YELLOW_COLOR);
		this.setResizable(false);
		this.info = new JLabel();
		this.info.setFont(new Font("Arial", Font.BOLD, 14));
		this.info.setForeground(GameConstants.TEXT_COLOR);
	}

	/**
	 * Add components to dialog.
	 *
	 * @param components list of components to add to window
	 */
	private void setUpComponents(List<JComponent> components) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridy = 0;
		for (JComponent component : components) {
			this.add(component, c);
			c.gridy++;
		}
	}

	/**
	 * Set size and position of pop-up based on fields and current base coordinates.
	 */
	public void makeBounds() {
		this.setBounds(base.getX() + xOffset, base.getY() + yOffset, width, height);
	}

	/**
	 * Set pop-up as visible, in focus and reset its bounds
	 * Start timer (only Save pop-up has timer).
	 */
	public void visibleFocus() {
		this.timer.start();
		this.makeBounds();
		this.setVisible(true);
		this.requestFocus();
	}

	/**
	 * Creates "saved game" pop-up.
	 *
	 * @return list of components to be added to box
	 */
	private List<JComponent> setUpSave() {
		width = 100;
		height = 70;
		xOffset = GameConstants.GAME_WINDOW_SIZE - width / 2;
		yOffset = 100;
		this.makeBounds();

		this.addKeyListener(new Controller(base, this));

		this.info.setText("Game Saved");

		timeMS = 0;
		timer = new Timer(250, unused -> {
			assert SwingUtilities.isEventDispatchThread();
			timeMS += 1;
			if (timeMS >= 3) {
				this.setVisible(false);
				timer.stop();
				timeMS = 0;
			}
		});
		return List.of(info);
	}

	/**
	 * Sets up "game won" pop-up.
	 *
	 * @return list of components to be added to window
	 */
	private List<JComponent> setUpGameCompleted() {
		height = 400;
		this.makeBounds();

		this.addKeyListener(new Controller(base, this));

		this.info.setText("You Win!");

		return List.of(info, loadButton(), newOneButton(), newTwoButton(), exitButton());
	}

	/**
	 * Sets up "game over" pop-up.
	 *
	 * @return list of components to be added to window
	 */
	private List<JComponent> setUpGameOver() {
		height = 300;
		this.makeBounds();

		this.addKeyListener(new Controller(base, this));

		this.info.setText("You Died!");

		return List.of(info, loadButton(), newOneButton(), newTwoButton(), exitButton());
	}

	/**
	 * Sets up next level pop-up.
	 *
	 * @param lvl the next level number
	 * @return the list of components to add to pop-up
	 */
	private List<JComponent> setUpNextLevel(int lvl) {
		height = 200;
		this.makeBounds();

		this.addKeyListener(new Controller(base, this));

		this.info.setText("Great Job!");

		return List.of(info, new GameButton("Next Level", BUTTON_SIZE, e -> {
			base.newGame(lvl);
			this.dispose();
		}), exitButton());
	}

	/**
	 * Sets up "pause" pop up.
	 *
	 * @return list of components to be added to pop-up
	 */
	private List<JComponent> setUpPause() {
		height = 400;
		this.makeBounds();

		this.addKeyListener(new Controller(base, this));

		this.info.setText("Game is Paused");

		if (!this.isFocusOwner()) {
			base.unPause();
			this.setVisible(false);
		}

		return List.of(info, playButton(), loadButton(), newOneButton(), newTwoButton(), saveButton(), exitButton());
	}

	/**
	 * Set up error pop up.
	 *
	 * @param message message for error display
	 * @return list of components for to be added to pop-up
	 */
	private List<JComponent> setUpError(String message) {
		height = 130;
		xOffset = GameConstants.GAME_WINDOW_SIZE - width / 2;
		yOffset = 100;
		this.makeBounds();

		this.addKeyListener(new Controller(base, this));

		this.info.setText(message);

		GameButton button = new GameButton("OK", BUTTON_SIZE, e -> {
			this.dispose();
		});

		return List.of(info, button);
	}

	/**
	 * Makes play button. With action to set pop-up invisible again.
	 *
	 * @return play button
	 */
	private GameButton playButton() {
		Runnable action = () -> {
			base.unPause();
			this.setVisible(false);
		};
		GameButton button = new GameButton("Play Game", BUTTON_SIZE, e -> action.run());
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				action.run();
			}
		});
		return button;
	}

	/**
	 * Makes load level button. Closes pop-up when clicked
	 *
	 * @return load button
	 */
	private GameButton loadButton() {
		return new GameButton("Load Previous Game", BUTTON_SIZE, e -> {
			base.loadGame();
			this.setVisible(false);
		});
	}

	/**
	 * Makes load level one button. Closes pop-up when clicked
	 *
	 * @return load level one button
	 */
	private GameButton newOneButton() {
		return new GameButton("Load Level One", BUTTON_SIZE, e -> {
			base.newGame(1);
			this.setVisible(false);
		});
	}

	/**
	 * Makes load level two button. Closes pop-up when clicked
	 *
	 * @return load level two button
	 */
	private GameButton newTwoButton() {
		return new GameButton("Load Level Two", BUTTON_SIZE, e -> {
			base.newGame(2);
			this.setVisible(false);
		});
	}

	/**
	 * Makes save game button
	 *
	 * @return save button
	 */
	private GameButton saveButton() {
		return new GameButton("Save and Exit", BUTTON_SIZE, e -> {
			base.saveExit();
		});
	}

	/**
	 * Makes exit game button. Closes pop-up when clicked
	 *
	 * @return exit button
	 */
	private GameButton exitButton() {
		return new GameButton("Exit Game", BUTTON_SIZE, e -> {
			base.exitGame();
			this.setVisible(false);
		});
	}
}
