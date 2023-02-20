package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu bar for game.
 *
 * @author Molly
 * @version 1.4
 */
public class GameMenuBar extends JMenuBar {

	/**
	 * All components on window.
	 */
	List<JComponent> components = new ArrayList<>();

	/**
	 * Width of buttons.
	 */
	int menuItemWidth;

	/**
	 * Base JFrame to connect actions to.
	 */
	Base base;

	/**
	 * Pause/play menuitem.
	 */
	GameMenuItem pause;

	/**
	 * Modular menu bar for the program. Default menu has home button.
	 *
	 * @param base the JFrame that these menu actions connect to
	 */
	public GameMenuBar(Base base) {
		this.base = base;
		menuItemWidth = GameConstants.GAME_WINDOW_SIZE / 7;
		addHomeButton();
		this.setBackground(GameConstants.BUTTON_COLOR);
		this.setAlignmentX(JComponent.LEFT_ALIGNMENT);
	}

	/**
	 * Adds home button to this JMenuBar.
	 */
	private void addHomeButton() {
		ImageIcon icon = getIcon("home");
		GameMenuItem home = new GameMenuItem("Home", e -> base.menuScreen(), menuItemWidth, icon);
		this.add(home);
		components.add(home);
	}

	/**
	 * Game is paused so set pause button to display "un pause".
	 */
	public void setPause() {
		pause.setText("Play");
		pause.changeActionListener(e -> base.unPause());
		ImageIcon imageIcon = getIcon("play");
		pause.changeIcon(imageIcon);
	}

	/**
	 * Game is un-paused so set pause button to display "pause".
	 */
	public void setUnPause() {
		pause.setText("Pause");
		pause.changeActionListener(e -> base.pause());
		ImageIcon imageIcon = getIcon("pause");
		pause.changeIcon(imageIcon);
	}

	/**
	 * Adds buttons to the menu bar that are only relevant to the game window.
	 * Pause, New - level 1, level 2, Save.
	 */
	public void addGameButtons() {
		ImageIcon imageIcon = getIcon("pause");
		pause = new GameMenuItem("Pause", e -> base.pause(), menuItemWidth, imageIcon);
		this.add(pause);
		components.add(pause);

		JMenu newLevel = new JMenu("New");
		newLevel.setBackground(GameConstants.BUTTON_COLOR);
		newLevel.setForeground(GameConstants.TEXT_COLOR);
		newLevel.setPreferredSize(new Dimension(menuItemWidth, 30));

		GameMenuItem levelOne = new GameMenuItem("Level One", e -> base.newGame(1));
		newLevel.add(levelOne);
		components.add(levelOne);

		GameMenuItem levelTwo = new GameMenuItem("Level Two", e -> base.newGame(2));
		newLevel.add(levelTwo);
		components.add(levelTwo);

		this.add(newLevel);
		components.add(newLevel);

		GameMenuItem saveExit = new GameMenuItem("Save and Exit", e -> base.saveExit());
		this.add(saveExit);
		components.add(saveExit);

		GameMenuItem quickSave = new GameMenuItem("Quick Save", e -> base.saveGame());
		this.add(quickSave);
		components.add(quickSave);
	}

	/**
	 * Add load button to menubar.
	 */
	public void addLoadButton() {
		GameMenuItem load = new GameMenuItem("Load", e -> base.loadGame());
		this.add(load);
		components.add(load);
	}

	/**
	 * Add exit button to menubar.
	 */
	public void addExitButton() {
		GameMenuItem exit = new GameMenuItem("Exit", e -> base.exitGame());
		this.add(exit);
		components.add(exit);
	}

	/**
	 * Get icon to put on menuitem.
	 *
	 * @param filename filename for icon
	 * @return Image to put on menu item
	 */
	public ImageIcon getIcon(String filename) {
		URL imagePath = this.getClass().getResource("/UI/" + filename + "_icon.png");
		ImageIcon imageIcon = new ImageIcon(imagePath);
		Image img = imageIcon.getImage();
		Image image = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
}
