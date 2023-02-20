package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Game menu item to add to menu bar. With consistent colors for UI.
 *
 * @author Molly
 * @version 1.8
 */
public class GameMenuItem extends JMenuItem {

	/**
	 * Menu item with icon.
	 *
	 * @param name       name of item
	 * @param actionList action for item
	 * @param width      width of item
	 * @param imageIcon  icon for item
	 */
	public GameMenuItem(String name, ActionListener actionList, int width, ImageIcon imageIcon) {
		super(name);
		setUp(actionList);

		this.setMaximumSize(new Dimension(width, 30));

		this.setIcon(imageIcon);
	}

	/**
	 * Menu item with name and action.
	 *
	 * @param name       name for item
	 * @param actionList action for item
	 */
	public GameMenuItem(String name, ActionListener actionList) {
		super(name);
		setUp(actionList);
	}

	/**
	 * Set up menu item.
	 *
	 * @param actionList item action
	 */
	private void setUp(ActionListener actionList) {
		this.addActionListener(actionList);
		this.setBackground(GameConstants.BUTTON_COLOR);
		this.setForeground(GameConstants.TEXT_COLOR);
		this.setHorizontalAlignment(SwingConstants.LEFT);
	}

	/**
	 * Change current action listener for new one.
	 *
	 * @param action new action for item
	 */
	public void changeActionListener(ActionListener action) {
		this.removeActionListener(this.getActionListeners()[0]);
		this.addActionListener(action);
	}

	/**
	 * Change icon on item.
	 *
	 * @param imageIcon new icon for item
	 */
	public void changeIcon(ImageIcon imageIcon) {
		this.setIcon(imageIcon);
	}
}
