package nz.ac.vuw.ecs.swen225.gp22.app;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * To map key events to actions.
 *
 * @author Molly
 * @version 1.2
 */
public class Keys implements KeyListener {
	/**
	 * Map for actions on pressed.
	 */
	private final Map<Integer, Runnable> actionsPressed = new HashMap<>();

	/**
	 * Maps for actions on ctrl release.
	 */
	private final Map<Integer, Runnable> actionsCtrlReleased = new HashMap<>();

	/**
	 * Map for actions on release.
	 */
	private final Map<Integer, Runnable> actionsReleased = new HashMap<>(); //

	/**
	 * Set keys for actions that happen on release
	 *
	 * @param keyCode    keycode to set
	 * @param onReleased action on release
	 * @param ctrlDown   should ctrl be pressed
	 */
	public void setAction(int keyCode, Runnable onReleased, boolean ctrlDown) {
		if (ctrlDown) {
			actionsCtrlReleased.put(keyCode, onReleased);
		} else {
			actionsReleased.put(keyCode, onReleased);
		}
	}

	/**
	 * Set keys for actions that happen on press
	 *
	 * @param keyCode   keycode to set
	 * @param onPressed action on press
	 */
	public void setAction(int keyCode, Runnable onPressed) {
		actionsPressed.put(keyCode, onPressed);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		assert SwingUtilities.isEventDispatchThread();
		actionsPressed.getOrDefault(e.getKeyCode(), () -> {
		}).run();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		assert SwingUtilities.isEventDispatchThread();
		if (e.isControlDown()) {
			actionsCtrlReleased.getOrDefault(e.getKeyCode(), () -> {
			}).run();
		} else {
			actionsReleased.getOrDefault(e.getKeyCode(), () -> {
			}).run();
		}

	}
}
