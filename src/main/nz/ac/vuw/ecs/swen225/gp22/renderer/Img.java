package nz.ac.vuw.ecs.swen225.gp22.renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * This stores an enum with the names of all the images to be used in the game.
 * It creates a new instance of the image by loading the object and returning
 * a BufferedImage object from a png file with its name.
 *
 * @author Diana
 * @version 1.2
 */
public enum Img {
    /**
     * The title card for the side panel.
     */
    Title,
    /**
     * The blue key image.
     */
    BlueKey,
    /**
     * The blue locked door image.
     */
    BlueLockedDoor,
    /**
     * The green key image.
     */
    GreenKey,
    /**
     * The green locked door image.
     */
    GreenLockedDoor,
    /**
     * The red key image.
     */
    RedKey,
    /**
     * The red locked door image.
     */
    RedLockedDoor,
    /**
     * The yellow key image.
     */
    YellowKey,
    /**
     * The yellow locked door image.
     */
    YellowLockedDoor,
    /**
     * The free tile image.
     */
    FreeTile,
    /**
     * The exit tile image.
     */
    Exit,
    /**
     * The locked exit tile image.
     */
    LockedExit,
    /**
     * The infoField tile image.
     */
    InfoField,
    /**
     * The image displaying the information for level 1.
     */
    Level1Info,
    /**
     * The image of the player moving downwards.
     */
    PlayerDown,
    /**
     * The image of the player moving to the left.
     */
    PlayerLeft,
    /**
     * The image of the player moving to the right.
     */
    PlayerRight,
    /**
     * The image of the player moving upwards.
     */
    PlayerUp,
    /**
     * The image for a treasure.
     */
    Treasure,
    /**
     * The image for a wall tile.
     */
    Wall,
    /**
     * The blue key image without background for inventory display.
     */
    BlueKeyNB,
    /**
     * The green key image without background for inventory display.
     */
    GreenKeyNB,
    /**
     * The red key image without background for inventory display.
     */
    RedKeyNB,
    /**
     * The yellow key image without background for inventory display.
     */
    YellowKeyNB,
    /**
     * The image for a BouncyPad that launches a player downwards.
     */
    BouncyPadDown,
    /**
     * The image for a BouncyPad that launches a player to the left.
     */
    BouncyPadLeft,
    /**
     * The image for a BouncyPad that launches a player to the right.
     */
    BouncyPadRight,
    /**
     * The image for a BouncyPad that launches a player upwards.
     */
    BouncyPadUp,
    /**
     * The image displaying the information for level 2.
     */
    Level2Info,
    /**
     * The image for the MilkPuddle tile.
     */
    MilkPuddle;
    /**
     * The image to be loaded and displayed on screen.
     */
    public final BufferedImage image;

    /**
     * Img constructor to load an image.
     */
    Img() {
        image = loadImage(this.name());
    }

    /**
     * Loads a bufferedImage on screen.
     *
     * @param name Name of the image to be loaded.
     * @return A bufferedImage of the given name.
     */
    static private BufferedImage loadImage(String name) {
        URL imagePath = Img.class.getResource("/imgs/" + name + ".png");
        try {
            assert imagePath != null;
            return ImageIO.read(imagePath);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}