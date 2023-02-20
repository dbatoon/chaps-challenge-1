package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.domain.Entity;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Controller that connects key actions to actions.
 *
 * @author Molly
 * @version 1.4
 */
public class Controller extends Keys {
	/**
	 * Current Base, to call actions on.
	 */
	private final Base b;

	/**
	 * Menu commands, exiting, replaying, making new levels.
	 *
	 * @param b Base that actions happen
	 */
	public Controller(Base b) {
		this.b = b;
		constantCommand();
	}

	/**
	 * Controller for when game is running.
	 *
	 * @param b      base which commands get run on
	 * @param paused is the game paused?
	 */
	public Controller(Base b, boolean paused) {
		this.b = b;
		constantCommand();
		setAction(KeyEvent.VK_S, b::saveExit, true); //ctrl s
		setAction(KeyEvent.VK_Q, b::saveGame, true); //ctrl q

		if (paused) {
			setAction(KeyEvent.VK_ESCAPE, b::unPause, false); //esc
		} else {
			setAction(KeyEvent.VK_SPACE, b::pause, false); //space

			setAction(KeyEvent.VK_UP, () -> b.movePlayer(Entity.Direction.Up));
			setAction(KeyEvent.VK_DOWN, () -> b.movePlayer(Entity.Direction.Down));
			setAction(KeyEvent.VK_LEFT, () -> b.movePlayer(Entity.Direction.Left));
			setAction(KeyEvent.VK_RIGHT, () -> b.movePlayer(Entity.Direction.Right));
		}
	}

	/**
	 * Controller for Pop-up window.
	 *
	 * @param b      current base
	 * @param dialog current pop-up
	 */
	public Controller(Base b, JDialog dialog) {
		this.b = b;

		setAction(KeyEvent.VK_X, () -> {
			dialog.setVisible(false);
			b.exitGame();
		}, true); //ctrl X

		setAction(KeyEvent.VK_R, () -> {
			dialog.setVisible(false);
			b.loadGame();
		}, true); //ctrl r

		setAction(KeyEvent.VK_1, () -> {
			dialog.setVisible(false);
			b.newGame(1);
		}, true); //ctrl 1

		setAction(KeyEvent.VK_2, () -> {
			dialog.setVisible(false);
			b.newGame(2);
		}, true); //ctrl 2

		setAction(KeyEvent.VK_S, b::saveExit, true); //ctrl s

		setAction(KeyEvent.VK_Q, b::saveGame, true); //ctrl q

		setAction(KeyEvent.VK_ESCAPE, () -> {
			dialog.setVisible(false);
			b.resetFocus();
			b.unPause();
		}, false); //esc
	}


	/**
	 * Actions that exist in most windows.
	 */
	private void constantCommand() {
		setAction(KeyEvent.VK_X, b::exitGame, true); //ctrl X
		setAction(KeyEvent.VK_R, b::loadGame, true); //ctrl r
		setAction(KeyEvent.VK_1, () -> b.newGame(1), true); //ctrl 1
		setAction(KeyEvent.VK_2, () -> b.newGame(2), true); //ctrl 2

	}

}