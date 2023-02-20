package nz.ac.vuw.ecs.swen225.gp22.util;

import java.awt.*;

/**
 * This class stores the constants needed for the dimensions of
 * the objects within the maze, as well as the colors used and
 * sizes of the window they will be displayed on.
 *
 * @author Diana
 * @version 1.3
 */
public class GameConstants {
    // MAZE DIMENSIONS
    /**
     * Width and height of each tile.
     */
    public static final int TILE_SIZE = 60;

    /**
     * Number of tiles across the screen.
     */
    public static final int NUM_GAME_TILE = 9;

    /**
     * Focus area of game.
     */
    public static final int FOCUS_AREA = NUM_GAME_TILE / 2;

    /**
     * Number of tiles in inventory.
     */
    public static final int NUM_INVENTORY_TILES = 12;


    // WINDOW DIMENSIONS
    /**
     * Width of side panel.
     */
    public static final int SIDEBAR_WIDTH = 5 * TILE_SIZE;

    /**
     * Height of window.
     */
    public static final int WINDOW_HEIGHT = NUM_GAME_TILE * TILE_SIZE;

    /**
     * Width of window.
     */
    public static final int WINDOW_WIDTH = NUM_GAME_TILE * TILE_SIZE + SIDEBAR_WIDTH;

    /**
     * Width and Height of game panel.
     */
    public static final int GAME_WINDOW_SIZE = WINDOW_HEIGHT;

    /**
     * Dimension of full window.
     */
    public static final Dimension WINDOW_SIZE = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);

    /**
     * Dimension of square game panel.
     */
    public static final Dimension GAME_SIZE = new Dimension(GAME_WINDOW_SIZE, WINDOW_HEIGHT);

    /**
     * Dimension of side panel.
     */
    public static final Dimension SIDE_SIZE = new Dimension(SIDEBAR_WIDTH, WINDOW_HEIGHT);

    //COLOR CONSTANTS

    /**
     * Pink button colour.
     */
    public static final Color BUTTON_COLOR = new Color(220, 170, 200);

    /**
     * Lighter blue background colour.
     */
    public static final Color BG_COLOR_LIGHTER = new Color(228, 213, 242);

    /**
     * Darker blue background colour.
     */
    public static final Color BG_COLOR_DARKER = new Color(200, 190, 230);

    /**
     * Blue background colour.
     */
    public static final Color BG_COLOR = BG_COLOR_DARKER;

    /**
     * Brown text colour.
     */
    public static final Color TEXT_COLOR = new Color(81, 45, 28);

    /**
     * Light yellow colour.
     */
    public static final Color LIGHT_YELLOW_COLOR = new Color(228, 216, 170);

}
