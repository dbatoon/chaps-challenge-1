package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.util.List;
import java.util.Stack;

import org.junit.jupiter.api.Test;
import nz.ac.vuw.ecs.swen225.gp22.domain.Entity.Direction;

/**
 * Tests to check the integrity of the game
 * state in the domain module.
 * <p>
 * These tests cover everything except for the DevMarkers class,
 * which is used for testing and marking work in progress code.
 *
 * @author Abdul
 * @version 1.2
 */
public class DomainTest {
    /**
     * Checks that a new maze is created properly.
     */
    @Test
    public void newMazeTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);

        assert Maze.player.getPos().equals(new Maze.Point(0, 0));
        assert Maze.getDimensions().equals(new Maze.Point(5, 3));
        assert Maze.getStringState().equals(
                "GGGGG\n" +
                        "GGGGG\n" +
                        "GGGGG\n"
        );
    }

    /**
     * Checks that all tiles can be added properly.
     */
    @Test
    public void createTilesTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        Maze.setTile(new Maze.Point(0, 0), new Ground(new Maze.Point(0, 0)));
        Maze.setTile(new Maze.Point(1, 0), new InfoField(new Maze.Point(1, 0), "Hello"));
        Maze.setTile(new Maze.Point(2, 0), new Key(new Maze.Point(2, 0), ColorableTile.Color.Blue));
        Maze.setTile(new Maze.Point(3, 0), new LockedDoor(new Maze.Point(3, 0), ColorableTile.Color.Blue));
        Maze.setTile(new Maze.Point(4, 0), new LockedExit(new Maze.Point(4, 0)));
        Maze.setTile(new Maze.Point(0, 1), new Treasure(new Maze.Point(0, 1)));
        Maze.setTile(new Maze.Point(1, 1), new Wall(new Maze.Point(1, 1)));
        Maze.setTile(new Maze.Point(2, 1), new Exit(new Maze.Point(2, 1)));
        Maze.setTile(new Maze.Point(3, 1), new MilkPuddle(new Maze.Point(3, 1)));
        Maze.setTile(new Maze.Point(4, 1), new BouncyPad(new Maze.Point(4, 1), Direction.Left));

        assert Maze.getStringState().equals(
                "GIKDL\n" +
                        "TWEMB\n" +
                        "GGGGG\n"
        );
    }

    /**
     * Checks that the player can move properly.
     */
    @Test
    public void movePlayerTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);

        assert Maze.player.id() == 0;
        assert Maze.getEntity(0).equals(Maze.player);

        Maze.player.move(Direction.Right);
        Maze.player.setDir(Direction.Down);
        Maze.player.move();
        Maze.player.moveAndTurn(Direction.Up);

        // Make sure ping does nothing for player.
        Maze.player.ping();

        assert Maze.player.getPos().equals(new Maze.Point(1, 0));
        assert Maze.player.getDir().equals(Direction.Up);

        // Make sure unping does nothing for player.
        Maze.player.unping();

        assert Maze.player.getPos().equals(new Maze.Point(1, 0));
        assert Maze.player.getDir().equals(Direction.Up);
    }

    /**
     * Checks that created tiles have the expected properties.
     */
    @Test
    public void correctTileInfoTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        InfoField infoTile = new InfoField(new Maze.Point(1, 0), "Hello");
        Key key = new Key(new Maze.Point(2, 0), ColorableTile.Color.Blue);
        LockedDoor door = new LockedDoor(new Maze.Point(3, 0), ColorableTile.Color.Blue);

        assert infoTile.getText().equals("Hello");
        assert key.getColor().equals(ColorableTile.Color.Blue);
        assert door.getColor().equals(ColorableTile.Color.Blue);
    }

    /**
     * Checks that the player can correctly pickup a key.
     */
    @Test
    public void keyPickupTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        Maze.setTile(new Maze.Point(2, 0), new Key(new Maze.Point(2, 0), ColorableTile.Color.Blue));

        Maze.player.move(Direction.Right);
        assert Maze.player.keyCount() == 0;
        Maze.player.move(Direction.Right);

        assert Maze.player.keyCount() == 1;
        assert Maze.player.getAllKeys().size() == 1;
        assert Maze.player.hasKey(ColorableTile.Color.Blue);
        Maze.player.resetItems();
        assert Maze.player.keyCount() == 0;
        assert Maze.player.getPos().equals(new Maze.Point(2, 0));
    }

    /**
     * Checks that the player can correctly unlock a door
     * only when the player has the correct key, and that the key is consumed.
     */
    @Test
    public void unlockDoorTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        LockedDoor door = new LockedDoor(new Maze.Point(1, 0), ColorableTile.Color.Blue);
        Maze.setTile(new Maze.Point(1, 0), door);
        Maze.setTile(new Maze.Point(0, 1), new Key(new Maze.Point(0, 1), ColorableTile.Color.Blue));

        try {
            Maze.player.move(Direction.Right);
        } catch (IllegalArgumentException exception) {
            assert Maze.player.getPos().equals(new Maze.Point(0, 0));
        }
        assert door.isObstructive();

        Maze.player.move(Direction.Down);
        assert !door.isObstructive();
        Maze.player.move(Direction.Up);
        Maze.player.move(Direction.Right);
        assert Maze.player.getPos().equals(new Maze.Point(1, 0));
        assert Maze.player.keyCount() == 0;
    }

    /**
     * Checks that the player can correctly pickup a treasure, and unlock
     * a locked exit when the player has all the treasures.
     */
    @Test
    public void unlockExitTest() {
        Maze.generateMap(new Maze.Point(5, 3), 1, -1);
        LockedExit exitGate = new LockedExit(new Maze.Point(1, 0));
        Maze.setTile(new Maze.Point(1, 0), exitGate);
        Maze.setTile(new Maze.Point(0, 1), new Treasure(new Maze.Point(0, 1)));

        try {
            Maze.player.move(Direction.Right);
        } catch (IllegalArgumentException exception) {
            assert Maze.player.getPos().equals(new Maze.Point(0, 0));
        }
        assert exitGate.isObstructive();
        assert Maze.getTreasuresLeft() == 1;

        Maze.player.move(Direction.Down);
        assert Maze.getTreasuresLeft() == 0;
        assert !exitGate.isObstructive();
        Maze.player.move(Direction.Up);
        Maze.player.move(Direction.Right);
        assert Maze.player.getPos().equals(new Maze.Point(1, 0));
    }

    /**
     * Checks that the player moves 2 tiles if
     * they step on a bounce pad. Also checks that
     * a deletedtile will not still
     * move the player.
     */
    @Test
    public void bouncePadTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        BouncyPad pad = new BouncyPad(new Maze.Point(2, 0), Direction.Right);
        Maze.setTile(new Maze.Point(1, 0), new BouncyPad(new Maze.Point(1, 0), Direction.Right));
        Maze.setTile(new Maze.Point(2, 0), pad);
        Maze.resetTile(new Maze.Point(1, 0));

        Maze.player.move(Direction.Right);
        assert Maze.player.getPos().equals(new Maze.Point(1, 0));
        assert pad.getDir() == Direction.Right;

        Maze.player.move(Direction.Right);
        assert Maze.player.getPos().equals(new Maze.Point(4, 0));
    }

    /**
     * Checks that the enemy can kill the player
     * and move properly.
     */
    @Test
    public void enemyTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        GummyGuard enemy = new GummyGuard(new Maze.Point(3, 0), Direction.Left);
        Maze.entities.add(enemy);
        Maze.player.setPos(new Maze.Point(1, 0));

        assert enemy.id() == 1;
        assert Maze.getEntity(1).equals(enemy);

        enemy.ping();
        assert !Maze.isGameLost();
        assert enemy.getSpeed() == 400;

        // Checks that unping works.
        enemy.unping();
        assert !Maze.isGameLost();
        assert Maze.entities.get(0).getPos().equals(new Maze.Point(3, 0));

        enemy.ping();
        enemy.ping();

        // Player should be dead now, make sure game is 
        // over even if enemy is removed. 
        enemy.deleteEntity();
        assert Maze.isGameLost();
    }

    /**
     * Checks that the player loses the game if
     * they step on a milk tile. Also checks that
     * a deleted milk tile will not still
     * kill the player.
     */
    @Test
    public void playerDeathTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        Maze.setTile(new Maze.Point(1, 0), new MilkPuddle(new Maze.Point(1, 0)));
        Maze.setTile(new Maze.Point(2, 0), new MilkPuddle(new Maze.Point(2, 0)));
        Maze.resetTile(new Maze.Point(1, 0));

        Maze.player.move(Direction.Right);
        assert !Maze.isGameLost();

        Maze.player.move(Direction.Right);
        assert Maze.isGameLost();
    }

    /**
     * Checks that the player can walk onto the exit.
     */
    @Test
    public void exitLevelTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        Maze.setTile(new Maze.Point(1, 0), new Exit(new Maze.Point(1, 0)));

        Maze.player.move(Direction.Right);

        assert Maze.getNextLevel() == -1;
        assert Maze.gameComplete();
        assert Maze.gameWon();
    }

    /**
     * Checks that all actions undo properly.
     */
    @Test
    public void undoTest() {
        Maze.generateMap(new Maze.Point(5, 5), 1, -1);
        Maze.player.setPos(new Maze.Point(1, 1));
        Maze.setTile(new Maze.Point(2, 1), new Key(new Maze.Point(2, 1), ColorableTile.Color.Blue));
        Maze.setTile(new Maze.Point(2, 2), new Treasure(new Maze.Point(2, 2)));
        Maze.setTile(new Maze.Point(1, 2), new LockedDoor(new Maze.Point(1, 2), ColorableTile.Color.Blue));
        Maze.setTile(new Maze.Point(1, 3), new LockedExit(new Maze.Point(1, 3)));
        Stack<Entity.Action> actions = new Stack<>();

        Maze.player.moveAndTurn(Direction.Right);
        assert Maze.player.hasAction();
        actions.push(Maze.getChangeMap().get(0));
        assert !Maze.player.hasAction();

        Maze.player.moveAndTurn(Direction.Down);
        assert Maze.player.hasAction();
        actions.push(Maze.getChangeMap().get(0));
        assert !Maze.player.hasAction();

        Maze.player.moveAndTurn(Direction.Left);
        assert Maze.player.hasAction();
        actions.push(Maze.getChangeMap().get(0));
        assert !Maze.player.hasAction();

        Maze.player.moveAndTurn(Direction.Down);
        assert Maze.player.hasAction();
        actions.push(Maze.getChangeMap().get(0));
        assert !Maze.player.hasAction();

        Maze.player.moveAndTurn(Direction.Up);
        assert Maze.player.hasAction();
        actions.push(Maze.getChangeMap().get(0));
        assert !Maze.player.hasAction();

        // Shouldn't do anything, as player doesn't have observers attached to other entities.
        Maze.player.deleteEntity();

        while (!actions.empty()) {
            Maze.undo(List.of(actions.pop()));
        }
        assert Maze.player.getPos().equals(new Maze.Point(1, 1));
        assert Maze.getStringState().equals(
                "GGGGG\n" +
                        "GGKGG\n" +
                        "GDTGG\n" +
                        "GLGGG\n" +
                        "GGGGG\n"
        ) : Maze.getStringState();
    }

    /**
     * Checks that an action can be redone properly.
     */
    @Test
    public void redoTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);

        Maze.player.moveAndTurn(Direction.Right);
        assert Maze.player.hasAction();
        Entity.Action action = Maze.getChangeMap().get(0);

        Maze.undo(List.of(action));
        Maze.apply(List.of(action));
        assert Maze.player.getPos().equals(new Maze.Point(1, 0));
        assert Maze.getStringState().equals(
                "GGGGG\n" +
                        "GGGGG\n" +
                        "GGGGG\n"
        ) : Maze.getStringState();
    }

    /**
     * Makes sure the Direction enum has
     * the correct opposite directions
     */
    @Test
    public void oppositeDirectionsTest() {
        assert Direction.Up.opposite() == Direction.Down;
        assert Direction.Down.opposite() == Direction.Up;
        assert Direction.Left.opposite() == Direction.Right;
        assert Direction.Right.opposite() == Direction.Left;
    }

    /**
     * Checks that the tile factory method can correctly
     * create tiles based on the ID.
     */
    @Test
    public void successfulFactoryTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        Tile wall = TileDatabase.create("wall", new Maze.Point(1, 0));
        InfoField infoTile = (InfoField) TileDatabase.create("info", new Maze.Point(1, 0), "Hello");

        assert wall instanceof Wall;
        assert infoTile.getText().equals("Hello");
    }

    /**
     * Checks that the tile factory method will throw
     * an IllegalArgumentException when the incorrect parameter types
     * are given.
     */
    @Test
    public void incorrectParametersTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        try {
            TileDatabase.create("wall", "Wrong type here");
        } catch (IllegalArgumentException e) {
            return;
        }
        assert false;
    }

    /**
     * Checks that the tile factory method will throw
     * an IllegalStateException if the constructor of a class throws
     * an error. In this case it is done by providing a point outside of the
     * tileMap bounds.
     */
    @Test
    public void unsuccessfulFactoryTest() {
        Maze.generateMap(new Maze.Point(1, 1), 0, -1);
        try {
            TileDatabase.create("wall", new Maze.Point(1, 0));
        } catch (IllegalStateException e) {
            return;
        }
        assert false;
    }

    /**
     * Checks that the correct ID is given
     * when using the getID() methods.
     */
    @Test
    public void getIDTest() {
        Maze.generateMap(new Maze.Point(5, 3), 0, -1);
        Tile wall = TileDatabase.create("wall", new Maze.Point(1, 0));

        assert TileDatabase.getID(Wall.class).equals("wall");
        assert TileDatabase.getID(wall).equals("wall");
    }

    /**
     * Making sure equals() and hashCode() in
     * Maze.Point is reflexive, transitive, and symmetric.
     */
    @Test
    public void pointTest() {
        Maze.Point pointOne = new Maze.Point(5, 5);
        Maze.Point pointTwo = new Maze.Point(1, 5).add(5, 0).subtract(1, 0);

        assert pointOne.equals(pointTwo) && pointTwo.equals(pointOne);
        assert pointOne.hashCode() == pointTwo.hashCode();
    }
}
