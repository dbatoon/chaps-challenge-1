package nz.ac.vuw.ecs.swen225.gp22.app;

import nz.ac.vuw.ecs.swen225.gp22.renderer.SidePanel;
import nz.ac.vuw.ecs.swen225.gp22.renderer.Viewport;
import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This JPanel contains both game panel and side panel.
 *
 * @author Molly
 * @version 1.3
 */
public class PhasePanel extends JPanel {
	/**
	 * Main square panel.
	 */
	JPanel gamePanel;

	/**
	 * Side panel.
	 */
	JPanel sidePanel;

	/**
	 * All components in panel
	 */
	List<JComponent> components = new ArrayList<>();

	/**
	 * Panel to hold game square next to side panel.
	 *
	 * @param gamePanel the panel in the main square
	 * @param sidePanel the panel on the side
	 */
	public PhasePanel(JPanel gamePanel, JPanel sidePanel) {
		this.gamePanel = gamePanel;
		this.sidePanel = sidePanel;
		this.setLayout(new BorderLayout());

		setUpPanels();

		this.setPreferredSize(GameConstants.WINDOW_SIZE);
		components.addAll(List.of(sidePanel, gamePanel));
	}

	/**
	 * Sets up panel sizes and adds them to panel.
	 */
	public void setUpPanels() {
		gamePanel.setPreferredSize(GameConstants.GAME_SIZE);
		sidePanel.setPreferredSize(GameConstants.SIDE_SIZE);

		this.add(BorderLayout.WEST, gamePanel);
		this.add(BorderLayout.EAST, sidePanel);
	}

	/**
	 * If side-panel has timer, update timer.
	 *
	 * @param time time to set panel to
	 */
	public void updateTime(int time) {
		if (this.sidePanel instanceof SidePanel sp) {
			sp.setTime(time);
			this.sidePanel = sp;
		} else {
			throw new ClassCastException("Panel must be Side Panel");
		}
	}

	/**
	 * Stops sound in game window.
	 */
	public void stopSound() {
		if (this.gamePanel instanceof Viewport vp) {
			vp.stopSound();
			this.gamePanel = vp;
		}
	}

	/**
	 * Get all components in window.
	 *
	 * @return list of components
	 */
	public List<JComponent> getAllComponents() {
		return components;
	}
}
