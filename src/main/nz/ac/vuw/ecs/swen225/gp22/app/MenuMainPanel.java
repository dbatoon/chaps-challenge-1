package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.persistency.Load;
import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Panel containing menu information.
 *
 * @author Molly
 * @version 1.3
 */
public class MenuMainPanel extends JPanel {
	/**
	 * Actions go to this base JFrame.
	 */
	private final Base base;

	/**
	 * Gap between buttons.
	 */
	private final Dimension buttonGap = new Dimension(10, 10);

	/**
	 * Size of half buttons.
	 */
	private final Dimension halfButtonSize = new Dimension(110, 30);

	/**
	 * Size of full button.
	 */
	private final Dimension fullButtonSize = new Dimension(halfButtonSize.width * 2 + buttonGap.width, halfButtonSize.height); //

	/**
	 * Creates menu panel, connecting base actions to buttons on panel.
	 *
	 * @param base current base
	 */
	public MenuMainPanel(Base base) {
		this.base = base;

		this.setBackground(GameConstants.BG_COLOR);

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;

		c.gridy = 0;
		this.add(titlePanelForMenu(), c);

		c.gridy = 1;
		Component vertSpace = Box.createRigidArea(new Dimension(0, 20));
		this.add(vertSpace, c);

		c.gridy = 2;
		this.add(buttonPanelForMenu(), c);
	}

	/**
	 * Creates title for menu page.
	 *
	 * @return JLabel for title
	 */
	private JPanel titlePanelForMenu() {
		Dimension dim = new Dimension(400, 100);
		ImagePanel panel = new ImagePanel("Title", dim, 0.8);
		panel.setPreferredSize(dim);
		return panel;
	}

	/**
	 * Button panel for menu.
	 *
	 * @return panel with buttons on it
	 */
	private JPanel buttonPanelForMenu() {
		JPanel buttons = new JPanel();
		buttons.setBackground(GameConstants.BG_COLOR);
		buttons.setPreferredSize(new Dimension(fullButtonSize.width + 3 * buttonGap.width, 4 * fullButtonSize.height + 10 * buttonGap.height));
		buttons.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.insets = new Insets(buttonGap.height, buttonGap.width, buttonGap.height, buttonGap.width);
		c.gridx = 0;
		c.gridy = 0;
		buttonsRowOne(buttons, c);

		c.gridy = 1;
		buttonsRowTwo(buttons, c);

		c.gridy = 2;
		buttonsRowThree(buttons, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		GameButton exit = new GameButton("Exit", fullButtonSize, e -> base.exitGame());
		buttons.add(exit, c);

		return buttons;
	}

	/**
	 * Buttons in row one of menu.
	 *
	 * @param buttons panel to add buttons to
	 * @param c       constraint parameter
	 */
	private void buttonsRowOne(JPanel buttons, GridBagConstraints c) {
		c.gridx = 0;

		GameButton start;
		if (Load.previousGamePresent()) {
			start = new GameButton("Resume", halfButtonSize, e -> base.startGame());
		} else {
			start = new GameButton("Start", halfButtonSize, e -> base.startGame());
		}

		buttons.add(start, c);

		c.gridx = 1;
		JLabel info = new JLabel(Load.previousGameInfo());
		info.setForeground(GameConstants.TEXT_COLOR);
		buttons.add(info, c);
	}

	/**
	 * Row two of buttons.
	 *
	 * @param buttons panel to add buttons to
	 * @param c       constraint parameter
	 */
	private void buttonsRowTwo(JPanel buttons, GridBagConstraints c) {
		c.gridx = 0;
		final GameButton newGame = new GameButton("New Game", halfButtonSize, e -> base.newGame(1));
		buttons.add(newGame, c);

		c.gridx = 1;
		String[] choices = {"Level One", "Level Two"};
		JComboBox<String> dropDownMenu = new JComboBox<>(choices);
		dropDownMenu.setBackground(GameConstants.BUTTON_COLOR);
		dropDownMenu.setForeground(GameConstants.TEXT_COLOR);
		dropDownMenu.setPreferredSize(halfButtonSize);
		dropDownMenu.setSelectedIndex(0);
		dropDownMenu.addActionListener(e -> {
			if (dropDownMenu.getSelectedItem().equals(choices[0])) {
				newGame.changeActionListener(f -> base.newGame(1));
			} else {
				newGame.changeActionListener(f -> base.newGame(2));
			}
		});
		buttons.add(dropDownMenu, c);
	}

	/**
	 * Row three buttons.
	 *
	 * @param buttons panel to add buttons to.
	 * @param c       constraint parameter.
	 */
	private void buttonsRowThree(JPanel buttons, GridBagConstraints c) {
		c.gridx = 0;
		GameButton load = new GameButton("Load Save", halfButtonSize, e -> base.loadGame());
		buttons.add(load, c);

		c.gridx = 1;
		GameButton replay = new GameButton("Replay Game", halfButtonSize, e -> base.replayPhase());
		buttons.add(replay, c);
	}
}
