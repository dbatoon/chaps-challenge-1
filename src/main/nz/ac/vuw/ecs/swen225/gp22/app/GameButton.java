package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * GameButton is button with set colors to make it match consistently across UI.
 *
 * @author Molly
 * @version 1.8
 */
public class GameButton extends JButton {
	/**
	 * Image on button.
	 */
	private Image image;

	/**
	 * Dimension of button.
	 */
	private final Dimension dim;

	/**
	 * Normal button.
	 *
	 * @param name   Text on button
	 * @param dim    Size of button
	 * @param action Action for button
	 */
	public GameButton(String name, Dimension dim, ActionListener action) {
		super(name);
		this.dim = dim;
		setUp(action);
	}

	/**
	 * Button with image or icon.
	 *
	 * @param name     button name
	 * @param dim      size of button
	 * @param action   action of button
	 * @param filename filename for image
	 */
	public GameButton(String name, Dimension dim, ActionListener action, String filename) {
		super(name);
		this.dim = dim;
		setUp(action);
		image = getIcon(filename);
	}

	/**
	 * Standard set-up for all buttons
	 *
	 * @param action action for button
	 */
	private void setUp(ActionListener action) {
		this.setBackground(GameConstants.BUTTON_COLOR);
		this.setForeground(GameConstants.TEXT_COLOR);

		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		this.addActionListener(action);
	}

	/**
	 * Gets the icon to add to the button.
	 *
	 * @param filename filename of image
	 * @return the image to add to button
	 */
	private Image getIcon(String filename) {
		URL imagePath = this.getClass().getResource("/UI/" + filename + ".png");
		ImageIcon imageIcon = new ImageIcon(imagePath);
		return imageIcon.getImage();
	}

	/**
	 * Change icon on button
	 *
	 * @param filename filename of new icon
	 */
	public void changeIcon(String filename) {
		image = getIcon(filename);
	}

	/**
	 * Changes action on button.
	 *
	 * @param actionListener new action listener
	 */
	public void changeActionListener(ActionListener actionListener) {
		this.removeActionListener(this.getActionListeners()[0]);
		this.addActionListener(actionListener);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g.create();
		if (image != null) {
			double scale = 0.45;
			int imgWidth = (int) (image.getWidth(this) * scale);
			int imgHeight = (int) (image.getHeight(this) * scale);
			int x = dim.width / 2 - imgWidth / 2;
			int y = dim.height / 2 - imgHeight / 2;
			g2D.drawImage(image, x, y, imgWidth, imgHeight, this);
		}
	}
}
