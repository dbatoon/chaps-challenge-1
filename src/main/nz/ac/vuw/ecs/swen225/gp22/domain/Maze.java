package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Action.Interaction.ActionType;

/**
 * This class stores the game state (player, tilemap, entities, and treasures).
 * As such, it is accessed by other packages to query the game state for specific tiles
 * or perform operations on the player.
 *
 * @author Abdul
 * @version 1.13
 */
public class Maze {
    /**
     * Stores the Player entity so that other tiles can access it easily.
     */
    public static Player player;

    /**
     * Contains all non-player entities. Suppressed the raw types warning as
     * the generic type is only used for observers and does not affect this use case.
     */
    @SuppressWarnings("rawtypes")
    public static ArrayList<Entity> entities = new ArrayList<>();

    /**
     * A 2D array that stores the level Tile instances in a
     * way where they can be accessed by position.
     */
    private static Tile tileMap[][];

    /**
     * Represents how many more Treasure tiles are still on the map.
     */
    private static int treasuresLeft;

    /**
     * Stores the number of the next level to load. If -1 the game complete flag returns true.
     */
    private static int nextLevel;

    /**
     * Stores Interaction records to be claimed by entities.
     */
    public static Queue<Entity.Action.Interaction> unclaimedInteractions = new ArrayDeque<>();

    /**
     * Flag used to check if the player was killed.
     */
    private static boolean gameLost;

    /**
     * Used to make entity IDs.
     */
    public static int globalID;

    /**
     * Generates a new map. This will be used by the persistency module for level loading.
     *
     * @param dimensions The size of the map.
     * @param treasures  The number of treasures on the map.
     * @param nextLevelP Stores the number of the next level to load.
     */
    public static void generateMap(Point dimensions, int treasures, int nextLevelP) {
        if (dimensions == null || dimensions.x() <= 0 || dimensions.y() <= 0)
            throw new IllegalArgumentException("Invalid map dimensions.");
        if (treasures < 0) throw new IllegalArgumentException("Number of treasures cannot be below 0.");

        nextLevel = nextLevelP;
        gameLost = false;
        globalID = 0;
        entities.clear();

        tileMap = new Tile[dimensions.x()][dimensions.y()];
        for (int x = 0; x < dimensions.x(); x++) {
            for (int y = 0; y < dimensions.y(); y++) {
                setTile(new Point(x, y), new Ground(new Point(x, y)));
            }
        }

        treasuresLeft = treasures;
        player = new Player(new Point(0, 0), Entity.Direction.Down);
    }

    /**
     * @return A Point representing the maps dimensions.
     */
    public static Point getDimensions() {
        return new Point(tileMap.length, tileMap[0].length);
    }

    /**
     * Finds a Tile using the tilemap given a point.
     *
     * @param point The position of the tile.
     * @return Tile object at the given position.
     */
    public static Tile getTile(Point point) {
        if (point == null || !point.isValid()) throw new IllegalArgumentException("Invalid point given.");
        return tileMap[point.x()][point.y()];
    }

    /**
     * Sets the value on the tilemap at a given point.
     *
     * @param point The position the tile will be at.
     * @param tile  The tile to add to the tilemap.
     */
    public static void setTile(Point point, Tile tile) {
        if (point == null || !point.isValid()) throw new IllegalArgumentException("Invalid point given.");
        if (tile == null) throw new IllegalArgumentException("Given tile does not exist.");
        if (!tile.getPos().equals(point))
            throw new IllegalArgumentException("Tile position does not match the point it is being set to.");
        Tile oldTile = getTile(point);
        if (oldTile != null) oldTile.deleteTile();
        tileMap[point.x()][point.y()] = tile;
        assert Maze.getTile(point) != oldTile : "Tile has not been removed from the map.";
    }

    /**
     * Sets the Tile object at a given point to ground.
     *
     * @param point Point to reset.
     */
    public static void resetTile(Point point) {
        if (point == null || !point.isValid()) throw new IllegalArgumentException("Invalid point given.");
        setTile(point, new Ground(point));
        assert getTile(point) instanceof Ground : "Tile not reset properly.";
    }

    /**
     * @return A list of changes that have occurred since this was last called.
     */
    public static List<Entity.Action> getChangeMap() {
        List<Entity.Action> changeMap = entities.stream().filter(Entity::hasAction).map(n -> n.pollAction()).collect(Collectors.toList());
        if (player.hasAction()) changeMap.add(player.pollAction());
        return changeMap;
    }

    /**
     * Applies a change map to the current game, updating the game state.
     *
     * @param changeMap A list of changes to apply.
     */
    public static void apply(List<Entity.Action> changeMap) {
        changeMap.forEach(a -> {
            if (a.interaction().type() == ActionType.Pinged) getEntity(a.id()).ping();
            else {
                getEntity(a.id()).setDir(a.newDir());
                getEntity(a.id()).move(a.moveVector());
            }
        });
    }

    /**
     * Undoes a change map to the current game, updating the game state.
     *
     * @param changeMap A list of changes to undo.
     */
    public static void undo(List<Entity.Action> changeMap) {
        changeMap.forEach(a -> {
            if (a.interaction().type() == ActionType.Pinged) getEntity(a.id()).unping();
            else {
                getEntity(a.id()).setDir(a.oldDir());
                getEntity(a.id()).move(a.moveVector().x() * -1, a.moveVector().y() * -1);
                a.interaction().type().undo(getEntity(a.id()).getPos().add(a.moveVector()), a.interaction().color());
            }
        });
    }

    /**
     * Finds an entity based on its ID, used for
     * replaying and rewinding moves. Suppresses raw types
     * warning because the generic type is only used for observers
     * and does not affect the implementation of this method.
     *
     * @param id ID of the entity.
     * @return The entity that matches the ID.
     */
    @SuppressWarnings("rawtypes")
    public static Entity getEntity(int id) {
        if (player.id() == id) return player;
        return entities.stream().filter(n -> n.id() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("No entity exists with given ID."));
    }

    /**
     * Reduce the number of treasures left by 1.
     */
    public static void collectTreasure() {
        if (treasuresLeft <= 0) throw new IllegalStateException("No treasure to collect.");
        treasuresLeft--;
    }

    /**
     * Increases the number of treasures left by 1.
     */
    public static void addTreasure() {
        treasuresLeft++;
    }

    /**
     * @return Whether or not all the Treasure tiles on the map have been collected.
     */
    public static boolean collectedAllTreasures() {
        return treasuresLeft == 0;
    }

    /**
     * @return The number of treasures left to collect.
     */
    public static int getTreasuresLeft() {
        return treasuresLeft;
    }

    /**
     * @return The number of the next level to load.
     */
    public static int getNextLevel() {
        return nextLevel;
    }

    /**
     * @return Whether or not there are more levels to load.
     */
    public static boolean gameComplete() {
        return nextLevel == -1;
    }

    /**
     * @return Whether or not the game has been won.
     */
    public static boolean gameWon() {
        return getTile(player.getPos()) instanceof Exit;
    }

    /**
     * @return Whether or not the player has lost the game.
     */
    public static boolean isGameLost() {
        return gameLost;
    }

    /**
     * Flags the game as over.
     */
    public static void loseGame() {
        gameLost = true;
    }

    /**
     * @return The current maze state in string form. Used for testing.
     */
    public static String getStringState() {
        String mazeState = "";
        for (int y = 0; y < tileMap[0].length; y++) {
            for (int x = 0; x < tileMap.length; x++) {
                mazeState += getTile(new Point(x, y)).toString();
            }
            mazeState += "\n";
        }

        return mazeState;
    }

    /**
     * Represents a point on the tilemap.
     */
    public record Point(int x, int y) {
        /**
         * Adds this point and another, then returns the result.
         *
         * @param point The point to add.
         * @return Point object representing the sum of the two points.
         */
        public Point add(Point point) {
            if (point == null) throw new IllegalArgumentException("Given point is null");
            return new Point(x + point.x(), y + point.y());
        }

        /**
         * Overloaded method for add() that accepts a Direction.
         *
         * @param dir Direction to get point from.
         * @return Point object representing the sum of the point and direction.
         */
        public Point add(Entity.Direction dir) {
            if (dir == null) throw new IllegalArgumentException("Given direction is null");
            return add(new Point(dir.posChange.x(), dir.posChange.y()));
        }

        /**
         * Overloaded method for add() that accepts two individual numbers
         * that represent X and Y, respectively.
         *
         * @param addX The amount to add to the X.
         * @param addY The amount to add to the Y.
         * @return Point object representing the sum of the point and the two numbers.
         */
        public Point add(int addX, int addY) {
            return add(new Point(addX, addY));
        }

        /**
         * Subtract this point from another, then returns the result.
         *
         * @param point Point to subtract by.
         * @return Point object representing the result of the two points.
         */
        public Point subtract(Point point) {
            if (point == null) throw new IllegalArgumentException("Given point is null");
            return new Point(x - point.x(), y - point.y());
        }

        /**
         * Overloaded method for subtract() that accepts a Direction.
         *
         * @param dir Direction to get point from.
         * @return Point object representing the result of the point minus the direction.
         */
        public Point subtract(Entity.Direction dir) {
            if (dir == null) throw new IllegalArgumentException("Given direction is null");
            return subtract(new Point(dir.posChange.x(), dir.posChange.y()));
        }

        /**
         * Overloaded method for subtract() that accepts two individual numbers
         * that represent X and Y, respectively.
         *
         * @param subtractX The amount to subtract from the X.
         * @param subtractY The amount to subtract from the Y.
         * @return Point object representing the result of the point minus the two numbers.
         */
        public Point subtract(int subtractX, int subtractY) {
            return subtract(new Point(subtractX, subtractY));
        }

        /**
         * @return Whether or not the point exists on the tilemap.
         */
        public boolean isValid() {
            return x >= 0 && x < tileMap.length && y >= 0 && y < tileMap[0].length;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Point p)) return false;
            return x == p.x() && y == p.y();
        }
    }
}
