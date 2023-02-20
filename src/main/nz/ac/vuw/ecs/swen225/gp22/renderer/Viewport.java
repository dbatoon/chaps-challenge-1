package nz.ac.vuw.ecs.swen225.gp22.renderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.List;
import java.util.HashMap;

import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import nz.ac.vuw.ecs.swen225.gp22.renderer.SFX.Sounds;
import nz.ac.vuw.ecs.swen225.gp22.util.GameConstants;

/**
 * This class displays the maze, and all the entities active in the current level
 * such as the player, free tiles, walls, keys, locked doors, treasures, locked exit, and exit.
 *
 * @author Diana
 * @version 1.6
 */
public class Viewport extends JPanel implements ActionListener {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * A 9x9 maze.
     */
    private final Tile[][] currentMaze = new Tile[GameConstants.NUM_GAME_TILE][GameConstants.NUM_GAME_TILE]; // 9x9 maze displayed on screen
    /**
     * The timer that triggers the actionPerformed method.
     */
    private Timer timer;
    /**
     * Accounts for the width of the maze for each level.
     */
    private final int boundariesX;
    /**
     * Accounts for the height of the maze for each level.
     */
    private final int boundariesY;
    /**
     * An SFXPlayer that plays a sound depending on the actions performed in the game.
     */
    private final SFXPlayer sfxPlayer = new SFXPlayer();
    /**
     * Stores the names of the sound and the SFX object itself.
     */
    private final HashMap<String, SFX> soundList = new HashMap<>();

    /**
     * Initialises a new maze upon the loading of a level.
     */
    public Viewport() {
        timer = new Timer(50, this);
        timer.start();

        boundariesX = Maze.getDimensions().x() - GameConstants.NUM_GAME_TILE;
        boundariesY = Maze.getDimensions().y() - GameConstants.NUM_GAME_TILE;

        //load wav files
        try {
            for (SFX.Sounds sfx : Sounds.values()) {
                soundList.put(sfx.toString(), new SFX(sfx.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        playSFX("Background"); // play the background music
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g.create();

        for (int x = 0; x < GameConstants.NUM_GAME_TILE; x++) {
            for (int y = 0; y < GameConstants.NUM_GAME_TILE; y++) {
                // draw the tiles
                g2D.drawImage(getTileImg(currentMaze[x][y]),
                        x * GameConstants.TILE_SIZE,
                        y * GameConstants.TILE_SIZE, this);
            }
        }

        int focusX = GameConstants.FOCUS_AREA;
        int focusY = GameConstants.FOCUS_AREA;

        int playerX = Maze.player.getPos().x();
        int playerY = Maze.player.getPos().y();

        Tile playerTile = Maze.getTile(new Maze.Point(playerX, playerY));

        // checking for the far left
        if (playerX < focusX) {
            focusX = playerX;
        }
        // checking for the far right
        else if (playerX > Maze.getDimensions().x() - focusX - 1) {
            focusX = playerX - boundariesX;
        }
        // checking for the top
        if (playerY < focusY) {
            focusY = playerY;
        }
        // checking for bottom
        else if (playerY > Maze.getDimensions().y() - focusY - 1) {
            focusY = playerY - boundariesY;
        }

        focusX *= GameConstants.TILE_SIZE;
        focusY *= GameConstants.TILE_SIZE;

        // draw the player
        g2D.drawImage(getEntityImg(Maze.player.getDir()), focusX, focusY, this);

        // display the info field if the player steps on it
        if (playerTile instanceof InfoField inField) {
            int infoPos = GameConstants.TILE_SIZE;
            g2D.drawImage(Img.valueOf(inField.getText()).image, infoPos, 5 * infoPos, this);
        }

        Maze.Point focusPoint = getFocusArea(playerX, playerY); // the point that the maze should be centered on

        // drawing the enemies
        for (Entity e : Maze.entities) {
            EnemyEntity enemy = (EnemyEntity) e;
            int enemyX = enemy.getPos().x();
            int enemyY = enemy.getPos().y();

            int rows = focusPoint.x() + GameConstants.NUM_GAME_TILE - 1;
            int cols = focusPoint.y() + GameConstants.NUM_GAME_TILE - 1;

            // check that the enemy is within the current the focus area
            if (focusPoint.x() <= enemyX && rows >= enemyX
                    && focusPoint.y() <= enemyX && cols >= enemyY) {
                g2D.drawImage(EnemyEntity.imageMap.get(enemy.getDir()),
                        (enemyX - focusPoint.x()) * GameConstants.TILE_SIZE,
                        (enemyY - focusPoint.y()) * GameConstants.TILE_SIZE, this);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!this.isValid()) {
            timer.stop(); // method doesn't get called anymore
            timer = null; // removes reference to the Viewport class
            return;
        }

        int playerX = Maze.player.getPos().x();
        int playerY = Maze.player.getPos().y();

        Maze.Point mazePoint = getFocusArea(playerX, playerY);
        setCurrentMaze(mazePoint); // get the new 9x9 tiles to be displayed

        repaint();
    }

    /**
     * Converts a coordinate to a point in the player's focus area.
     *
     * @param x x coordinate of the player.
     * @param y y coordinate of the player.
     * @return A Maze.Point in the current focus area.
     */
    private Maze.Point getFocusArea(int x, int y) {
        int focusX = x - GameConstants.FOCUS_AREA;
        int focusY = y - GameConstants.FOCUS_AREA;

        // checking for boundary cases
        focusX = Math.max(focusX, 0);
        focusY = Math.max(focusY, 0);

        focusX = Math.min(focusX, boundariesX);
        focusY = Math.min(focusY, boundariesY);

        return new Maze.Point(focusX, focusY);
    }

    /**
     * Displays a 9x9 maze that shows the tiles surrounding the Player.
     *
     * @param point A point to be displayed in the maze.
     */
    private void setCurrentMaze(Maze.Point point) {
        for (int i = 0; i < GameConstants.NUM_GAME_TILE; i++) {
            for (int j = 0; j < GameConstants.NUM_GAME_TILE; j++) {
                currentMaze[i][j] = Maze.getTile(new Maze.Point(i + point.x(), j + point.y()));
            }
        }
    }

    /**
     * Returns a BufferedImage corresponding to the type of tile in the maze.
     *
     * @param tile The tile being drawn on the canvas.
     * @return A BufferedImage of the tile.
     */
    private BufferedImage getTileImg(Tile tile) {
        if (tile instanceof Key k) { // check the colours against the four different ones available
            return switch (k.getColor()) {
                case Blue -> Img.BlueKey.image;
                case Green -> Img.GreenKey.image;
                case Red -> Img.RedKey.image;
                case Yellow -> Img.YellowKey.image;
                default -> throw new IllegalArgumentException("Invalid colour./n");
            };
        }
        if (tile instanceof LockedDoor ld) {
            return switch (ld.getColor()) {
                case Blue -> Img.BlueLockedDoor.image;
                case Green -> Img.GreenLockedDoor.image;
                case Red -> Img.RedLockedDoor.image;
                case Yellow -> Img.YellowLockedDoor.image;
                default -> throw new IllegalArgumentException("Invalid colour./n");
            };
        }
        if (tile instanceof BouncyPad bp) {
            return switch (bp.getDir()) {
                case Up -> Img.BouncyPadUp.image;
                case Down -> Img.BouncyPadDown.image;
                case Left -> Img.BouncyPadLeft.image;
                case Right -> Img.BouncyPadRight.image;
                default -> throw new IllegalArgumentException("Invalid direction./n");
            };

        }
        if (tile instanceof InfoField) {
            return Img.InfoField.image;
        }
        if (tile instanceof Exit) {
            return Img.Exit.image;
        }
        if (tile instanceof LockedExit) {
            return Img.LockedExit.image;
        }
        if (tile instanceof MilkPuddle) {
            return Img.MilkPuddle.image;
        }
        if (tile instanceof Treasure) {
            return Img.Treasure.image;
        }
        if (tile instanceof Wall) {
            return Img.Wall.image;
        }

        return Img.FreeTile.image;
    }

    /**
     * Returns a BufferedImage corresponding to the direction
     * an entity is facing.
     *
     * @param dir The direction the entity is facing.
     * @return The image of an entity depending on the direction it is facing.
     */
    private BufferedImage getEntityImg(Entity.Direction dir) {
        return switch (dir) {
            case Up -> Img.PlayerUp.image;
            case Down -> Img.PlayerDown.image;
            case Left -> Img.PlayerLeft.image;
            case Right -> Img.PlayerRight.image;
            default -> throw new IllegalArgumentException("Invalid direction./n");
        };
    }

    /**
     * Stores the action that has happened in the game.
     *
     * @param actions A list of actions performed.
     */
    public void setAction(List<Entity.Action.Interaction.ActionType> actions) {
        // play sounds based on the player's actions and game status
        for (Entity.Action.Interaction.ActionType a : actions) {
            checkAction(a);
        }
    }

    /**
     * Plays a sound based on the action that takes place in the game.
     *
     * @param a Action performed.
     */
    private void checkAction(Entity.Action.Interaction.ActionType a) {
        switch (a) {
            case PickupKey, PickupTreasure -> playSFX("CollectItem");
            case UnlockDoor, UnlockExit -> playSFX("Unlock");
        }
    }

    /**
     * Plays a sound.
     *
     * @param soundName The name of the sound.
     */
    private void playSFX(String soundName) {
        sfxPlayer.playSound(soundList.get(soundName), 1);
    }

    /**
     * Stops the current sounds from playing.
     */
    public void stopSound() {
        sfxPlayer.stopSFX();
    }

}
