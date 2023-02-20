package nz.ac.vuw.ecs.swen225.gp22.app;

import javax.swing.*;

/**
 * Main class. Program runs from here.
 *
 * @author Molly
 * @version 1.2
 */
public class Main {
	/**
	 * The entry point of the program.
	 *
	 * @param args main arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(Base::new);
	}
}
