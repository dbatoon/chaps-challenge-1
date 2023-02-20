package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.domain.ColorableTile;
import nz.ac.vuw.ecs.swen225.gp22.domain.EnemyEntity;
import nz.ac.vuw.ecs.swen225.gp22.domain.Entity;
import nz.ac.vuw.ecs.swen225.gp22.domain.Maze;
import nz.ac.vuw.ecs.swen225.gp22.persistency.Load;
import nz.ac.vuw.ecs.swen225.gp22.persistency.Save;
import nz.ac.vuw.ecs.swen225.gp22.recorder.Action;
import nz.ac.vuw.ecs.swen225.gp22.recorder.Player;
import nz.ac.vuw.ecs.swen225.gp22.recorder.Recorder;
import nz.ac.vuw.ecs.swen225.gp22.renderer.SidePanel;
import nz.ac.vuw.ecs.swen225.gp22.renderer.Viewport;
import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Action.Interaction.ActionType.*;

/**
 * Base is the base window that all actions occur on.
 *
 * @author Molly
 * @version 1.12
 */
public class Base extends JFrame {
	/**
	 * Components on JFrame.
	 */
	private final List<JComponent> components = new ArrayList<>();

	/**
	 * Time in milliseconds.
	 */
	private int timeMS = 0;

	/**
	 * Time in seconds.
	 */
	private static int timeSec = 60;

	/**
	 * Timer loop for game.
	 */
	private Timer gameTimer = new Timer(20, null);

	/**
	 * Recorder player.
	 */
	private Recorder recorder;

	/**
	 * Menu bar for game.
	 */
	private GameMenuBar currentMenuBar;

	/**
	 * For setting key-listener on.
	 */
	private PhasePanel currentPanel;

	/**
	 * Pop-up for pause.
	 */
	private final GameDialog pauseDialog;

	/**
	 * Pop-up for save.
	 */
	private final GameDialog saveDialog;

	/**
	 * Pop-up for game over.
	 */
	private final GameDialog gameOverDialog;

	/**
	 * Pop-up for game win.
	 */
	private final GameDialog gameWinDialog;

	/**
	 * Delay in timer.
	 */
	private final int delay = 20;

	/**
	 * Current level.
	 */
	private static int level = 1;

	/**
	 * Current pop-up.
	 */
	private GameDialog currentPopUp;

	/**
	 * Begin program here. Run menu phase.
	 */
	public Base() {
		assert SwingUtilities.isEventDispatchThread();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		menuScreen();

		pauseDialog = new GameDialog(this, GameDialog.PopUp.Pause);
		saveDialog = new GameDialog(this, GameDialog.PopUp.Save);
		gameOverDialog = new GameDialog(this, GameDialog.PopUp.GameOver);
		gameWinDialog = new GameDialog(this, GameDialog.PopUp.GameCompleted);

		setVisible(true);
		setResizable(false);
		pack();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				runClosePhase();
			}
		});
	}

	/**
	 * Remove all components in window, clears list and stops timer.
	 */
	public void runClosePhase() {
		for (JComponent component : this.components) {
			remove(component);
		}
		components.clear();
		gameTimer.stop();
	}

	/**
	 * Sets current level.
	 *
	 * @param lvl current level
	 */
	public static void setLevel(int lvl) {
		level = lvl;
	}

	/**
	 * Sets current game time.
	 *
	 * @param t time in seconds
	 */
	public static void setTime(int t) {
		timeSec = t;
	}

	/**
	 * Returns current level.
	 *
	 * @return current level number
	 */
	public static int getLevel() {
		return level;
	}

	/**
	 * Returns current game time.
	 *
	 * @return game time in seconds
	 */
	public static int getTime() {
		return timeSec;
	}

	/**
	 * When you click start button, check for last save file
	 * and run the level.
	 */
	public void startGame() {
		if (Load.previousGamePresent()) {
			Load.previousGame();
			loadLevel();
			recorder = new Recorder(level, timeMS);
		} else {
			newGame(1);
		}
	}

	/**
	 * Pauses game.
	 */
	public void pause() {
		closePopUp();
		gameTimer.stop();
		if (currentMenuBar == null) {
			return;
		}
		currentMenuBar.setPause();

		changeKeyListener(new Controller(this, true));
		pauseDialog.visibleFocus();
		currentPopUp = pauseDialog;
	}

	/**
	 * Un-pauses game.
	 */
	public void unPause() {
		closePopUp();
		gameTimer.start();
		if (currentMenuBar == null) {
			return;
		}
		currentMenuBar.setUnPause();
		pauseDialog.setVisible(false);
		currentPopUp = null;
		changeKeyListener(new Controller(this, false));
	}

	/**
	 * Creates and runs re-player window.
	 */
	public void replayPhase() {
		try {
			Player playerWindow = new Player(this);
			runClosePhase();

			add(BorderLayout.CENTER, playerWindow);
			setMinimumSize(new Dimension(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT + 150));
			pack();
			playerWindow.requestFocus();

			components.add(playerWindow);
		} catch (RuntimeException ignored) {
		}
	}

	/**
	 * Load a game from file.
	 */
	public void loadGame() {
		Load.resumeGame();
		loadLevel();
		recorder = new Recorder(level, timeMS);
	}

	/**
	 * Loads new level, makes new recorder.
	 *
	 * @param lvl level number to load
	 */
	public void newGame(int lvl) {

		level = lvl;
		Load.loadLevel(lvl);
		loadLevel();
		recorder = new Recorder(lvl, timeMS);
	}

	/**
	 * Save the current game.
	 */
	public void saveGame() {
		closePopUp();
		Save.saveGame();
		recorder.save();
		saveDialog.visibleFocus();
	}

	/**
	 * Resets focus to Base window (not pop-up).
	 */
	public void resetFocus() {
		currentPanel.requestFocus();
	}

	/**
	 * Saves the game and then exits.
	 */
	public void saveExit() {
		this.saveGame();
		this.exitGame();
	}

	/**
	 * Saves recorder, pops up game over screen.
	 */
	public void playerDied() {
		closePopUp();
		recorder.save();
		gameOverDialog.visibleFocus();
		gameTimer.stop();
		changeKeyListener(null);
	}

	/**
	 * Saves recorder, pops up game won screen.
	 */
	public void playerWon() {
		closePopUp();
		recorder.save();
		gameWinDialog.visibleFocus();
		currentPopUp = gameWinDialog;
		gameTimer.stop();
		changeKeyListener(null);
	}

	/**
	 * Displays next level pop-up, stops game.
	 *
	 * @param lvl the next level
	 */
	public void nextLevel(int lvl) {
		closePopUp();
		recorder.save();
		GameDialog nextLevelDialog = new GameDialog(this, lvl);
		nextLevelDialog.visibleFocus();
		currentPopUp = nextLevelDialog;
		changeKeyListener(null);
		gameTimer.stop();
	}

	/**
	 * Exit the game.
	 */
	public void exitGame() {
		runClosePhase();
		System.exit(0);
	}

	/**
	 * When key is pressed, move player and tell recorder.
	 *
	 * @param dir direction player moves
	 */
	public void movePlayer(Entity.Direction dir) {
		try {
			Maze.player.moveAndTurn(dir);
		} catch (IllegalArgumentException e) {
			Maze.player.setDir(dir);
		}
	}

	/**
	 * Gets the game window.
	 *
	 * @return game window
	 */
	public PhasePanel getGameWindow() {
		assert Maze.player != null;
		Viewport game = new Viewport();
		SidePanel side = new SidePanel(timeSec, level);
		return new PhasePanel(game, side);
	}

	/**
	 * Run and display menu.
	 */
	public void menuScreen() {

		runClosePhase();
		setJMenuBar(null);
		if (currentPanel != null) {
			currentPanel.stopSound();
		}
		ImagePanel imagePanel = new ImagePanel("MenuSidePanel", GameConstants.SIDE_SIZE, 0.8);
		PhasePanel menu = new PhasePanel(new MenuMainPanel(this), imagePanel);

		currentPanel = menu;
		changeKeyListener(new Controller(this));

		add(BorderLayout.CENTER, menu);
		components.add(menu);
		components.addAll(menu.getAllComponents());

		setMinimumSize(GameConstants.WINDOW_SIZE);
		pack();
		menu.requestFocus();
	}

	/**
	 * Undoes action.
	 *
	 * @param actions List of Recorder actionRecords.
	 */
	public void undo(List<Action> actions) {
		Maze.undo(actions.stream().map(this::getAction).toList());
	}

	/**
	 * Apply action.
	 *
	 * @param actions List of Recorder actionRecords.
	 */
	public void apply(List<Action> actions) {
		Maze.apply(actions.stream().map(this::getAction).toList());
	}

	/**
	 * Transforms Recorder Action into Domain Action.
	 *
	 * @param action Recorder action.
	 * @return Domain Action.
	 */
	public Entity.Action getAction(Action action) {
		int entity = action.entityID();
		Entity.Action.Interaction.ActionType actionType = getActionType(action.actionType());
		Entity.Direction oldDir = getDirection(action.prevDir());
		Entity.Direction newDir = getDirection(action.currDir());
		ColorableTile.Color color = getColor(action.color());
		Maze.Point point = new Maze.Point(action.x(), action.y());
		Entity.Action.Interaction interaction = new Entity.Action.Interaction(actionType, color);
		return new Entity.Action(entity, point, oldDir, newDir, interaction);
	}

	/**
	 * Gets Domain direction from String
	 *
	 * @param dir direction from Recorder
	 * @return Direction from Domain
	 */
	private Entity.Direction getDirection(String dir) {
		return switch (dir) {
			case "Up" -> Entity.Direction.Up;
			case "Down" -> Entity.Direction.Down;
			case "Left" -> Entity.Direction.Left;
			case "Right" -> Entity.Direction.Right;
			default -> throw new IllegalStateException("Unexpected direction: " + dir);
		};
	}

	/**
	 * Gets Domain Color from String.
	 *
	 * @param color color from Recorder
	 * @return Color from Domain
	 */
	private ColorableTile.Color getColor(String color) {
		return switch (color) {
			case "Red" -> ColorableTile.Color.Red;
			case "Yellow" -> ColorableTile.Color.Yellow;
			case "Green" -> ColorableTile.Color.Green;
			case "Blue" -> ColorableTile.Color.Blue;
			case "None" -> ColorableTile.Color.None;
			default -> throw new IllegalStateException("Unexpected Color: " + color);
		};
	}

	/**
	 * Gets Domain action from String.
	 *
	 * @param actionType action from Recorder
	 * @return Domain action
	 */
	private Entity.Action.Interaction.ActionType getActionType(String actionType) {
		return switch (actionType) {
			case "Pinged" -> Pinged;
			case "PickupKey" -> PickupKey;
			case "PickupTreasure" -> PickupTreasure;
			case "UnlockDoor" -> UnlockDoor;
			case "UnlockExit" -> UnlockExit;
			case "None" -> None;
			default -> throw new IllegalStateException("Unexpected action type: " + actionType);
		};
	}

	/**
	 * Create, run and draw new level.
	 */
	public void loadLevel() {
		assert Maze.player != null;
		if (currentPanel != null) {
			currentPanel.stopSound();
		}
		closePopUp();
		runClosePhase();

		Viewport game = new Viewport();
		SidePanel side = new SidePanel(timeSec, level);
		side.setTime(timeSec);
		final PhasePanel level = new PhasePanel(game, side);

		timeMS = 0;
		gameTimer = new Timer(delay, unused -> {
			assert SwingUtilities.isEventDispatchThread();
			level.repaint();
			timeMS += delay;
			if (timeMS % 1000 == 0) {
				timeSec--;
				side.setTime(timeSec);
			}

			Maze.entities.stream()
					.filter(e -> e instanceof EnemyEntity<?> ee && timeMS % ee.getSpeed() == 0)
					.forEach(Entity::ping);

			List<Entity.Action> actions = Maze.getChangeMap();
			transformActions(actions).forEach(a -> recorder.addAction(a, timeMS));
			game.setAction(actions.stream().map(a -> a.interaction().type()).collect(Collectors.toList()));

			if (timeSec <= 0 || Maze.isGameLost()) {
				playerDied();
			} else if (Maze.gameWon()) {
				if (Maze.gameComplete()) {
					playerWon();
				} else {
					assert Maze.getNextLevel() != -1;
					nextLevel(Maze.getNextLevel());
				}
			}
		});
		gameTimer.start();


		currentPanel = level;
		changeKeyListener(new Controller(this, false));

		add(BorderLayout.CENTER, level);
		components.add(level);
		currentMenuBar = new GameMenuBar(this);
		currentMenuBar.addGameButtons();
		currentMenuBar.addLoadButton();
		currentMenuBar.addExitButton();
		setJMenuBar(currentMenuBar);

		pack();
		level.requestFocus();
	}

	/**
	 * Transforms list of Domain actions into list of Recorder actions.
	 *
	 * @param actions list of Domain actions
	 * @return list of Recorder actions
	 */
	private List<Action> transformActions(List<Entity.Action> actions) {
		List<Action> actionRecords = new ArrayList<>();
		for (Entity.Action action : actions) {
			String actionType = action.interaction().type().toString();
			String oldDir = action.oldDir().toString();
			String newDir = action.newDir().toString();
			String color = action.interaction().color().toString();
			int entityHash = action.id();
			int vx = action.moveVector().x();
			int vy = action.moveVector().y();
			actionRecords.add(new Action(entityHash, actionType, vx, vy, oldDir, newDir, color));
		}
		return actionRecords;
	}

	/**
	 * Removes current key listener, adds new one.
	 * Use to ensure there is only one key listener being
	 * used at any given time.
	 *
	 * @param keyListener new key-listener to use
	 */
	public void changeKeyListener(KeyListener keyListener) {
		if (currentPanel.getKeyListeners().length > 0) {
			currentPanel.removeKeyListener(currentPanel.getKeyListeners()[0]);
		}
		currentPanel.addKeyListener(keyListener);
		currentPanel.setFocusable(true);
	}

	/**
	 * Closes current pop-up if it exists.
	 */
	private void closePopUp() {
		if (currentPopUp != null) {
			currentPopUp.setVisible(false);
		}
	}
}
