package nz.ac.vuw.ecs.swen225.gp22.domain;

import nz.ac.vuw.ecs.swen225.gp22.domain.Maze.Point;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Base class for enemies, has a speed property so app
 * knows how often to ping the enemy.
 *
 * @author Abdul
 * @version 1.2
 */
public abstract class EnemyEntity<S extends Observable<S>> extends Entity<S> {
    /**
     * How often the monster gets pinged.
     */
    private final int speed;

    /**
     * Map used by renderer to get image based on direction.
     */
    public static HashMap<Direction, BufferedImage> imageMap = new HashMap<>();

    /**
     * Default constructor, sets the position, direction, and speed of the enemy.
     *
     * @param entityPos Point to set the position field to.
     * @param facingDir Direction to set the direction field to.
     * @param speed     How often the monster gets pinged.
     */
    public EnemyEntity(Point entityPos, Direction facingDir, int speed) {
        super(entityPos, facingDir);
        this.speed = speed;
    }

    /**
     * @return The speed of the enemy.
     */
    public final int getSpeed() {
        return speed;
    }
}
