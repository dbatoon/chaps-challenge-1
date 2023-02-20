package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Template for entities in a level, including the player.
 * Any entities are observable.
 *
 * @author Abdul
 * @version 1.17
 */
public abstract class Entity<S extends Observable<S>> extends Observable<S> {
    /**
     * Enum with values for each direction, which each have a point
     * representing the change in position.
     */
    public enum Direction {
        /**
         * Represents an upwards vector.
         */
        Up(0, -1) {
            @Override
            public Direction opposite() {
                return Down;
            }
        },
        /**
         * Represents an downwards vector.
         */
        Down(0, 1) {
            @Override
            public Direction opposite() {
                return Up;
            }
        },
        /**
         * Represents a vector going left.
         */
        Left(-1, 0) {
            @Override
            public Direction opposite() {
                return Right;
            }
        },
        /**
         * Represents a vector going right.
         */
        Right(1, 0) {
            @Override
            public Direction opposite() {
                return Left;
            }
        };

        /**
         * Amount the position of the entity will change by if it takes a step.
         */
        Maze.Point posChange;

        /**
         * Default constructor to set posChange.
         */
        Direction(int x, int y) {
            posChange = new Maze.Point(x, y);
        }

        /**
         * @return The opposite direction.
         */
        public abstract Direction opposite();
    }

    /**
     * A record used to store an action taken. This will be
     * used to revert actions when replaying a game.
     *
     * @param id          ID of the entity this action belongs to.
     * @param moveVector  Amount the entity moved by in this action.
     * @param oldDir      Direction entity was facing before action.
     * @param newDir      Direction entity is facing after action.
     * @param interaction The interaction, if any that happened this action.
     */
    public record Action(int id, Maze.Point moveVector, Direction oldDir, Direction newDir, Interaction interaction) {
        /**
         * Represents an interaction with a tile that happened in the action.
         *
         * @param type  The type of interaction that occurred.
         * @param color The color, if any, that the interaction had.
         */
        public record Interaction(ActionType type, ColorableTile.Color color) {
            /**
             * Represents the entity interacting with a tile.
             */
            public enum ActionType {
                /**
                 * No interaction with tiles in this action.
                 */
                None,
                /**
                 * Key was picked up this action.
                 */
                PickupKey {
                    @Override
                    public void undo(Maze.Point pos, ColorableTile.Color undoColor) {
                        Maze.setTile(pos, new Key(pos, undoColor));
                        Maze.player.consumeKey(undoColor);
                    }
                },
                /**
                 * Treasure was picked up this action.
                 */
                PickupTreasure {
                    @Override
                    public void undo(Maze.Point pos, ColorableTile.Color undoColor) {
                        Maze.setTile(pos, new Treasure(pos));
                        Maze.addTreasure();
                    }
                },
                /**
                 * Door was unlocked with key this action.
                 */
                UnlockDoor {
                    @Override
                    public void undo(Maze.Point pos, ColorableTile.Color undoColor) {
                        Maze.setTile(pos, new LockedDoor(pos, undoColor));
                        Maze.player.addKey(undoColor);
                    }
                },
                /**
                 * Exit was unlocked this action.
                 */
                UnlockExit {
                    @Override
                    public void undo(Maze.Point pos, ColorableTile.Color undoColor) {
                        Maze.setTile(pos, new LockedExit(pos));
                    }
                },
                /**
                 * Entity was pinged this action.
                 */
                Pinged;

                /**
                 * Undoes an interaction.
                 *
                 * @param pos       Position of tile to undo.
                 * @param undoColor Color of interaction, if any.
                 */
                public void undo(Maze.Point pos, ColorableTile.Color undoColor) {
                }
            }
        }
    }

    /**
     * Position of the entity in regards to the tilemap.
     */
    private Maze.Point entityPos;

    /**
     * The direction the entity is facing.
     */
    private Direction facingDir;

    /**
     * The action which the entity has taken, used for recorder.
     */
    public Action action;

    /**
     * Used to identify an entity during playback.
     */
    private final int id;

    /**
     * Makes sure updateObservers() isn't called in setPos() when
     * a move method is being called.
     */
    private boolean inMove = false;

    /**
     * Default constructor, sets the position and direction of the entity.
     *
     * @param entityPos Point to set the position field to.
     * @param facingDir Direction to set the direction field to.
     */
    public Entity(Maze.Point entityPos, Direction facingDir) {
        setPos(entityPos);
        setDir(facingDir);
        id = Maze.globalID;
        Maze.globalID++;
    }

    /**
     * Non-player entities will act based on how often this is called.
     */
    abstract public void ping();

    /**
     * Undoes the effects of ping().
     */
    abstract public void unping();

    /**
     * @return Whether or not this entity has done an action since the last call of getChangeMap().
     */
    public boolean hasAction() {
        return action != null;
    }

    /**
     * @return The current action and set the field to null.
     */
    public Action pollAction() {
        Action storedAction = action;
        action = null;
        return storedAction;
    }

    /**
     * Moves the entity in a given direction.
     *
     * @param direction Direction to move the entity in.
     */
    public void move(Direction direction) {
        if (direction == null) throw new IllegalArgumentException("Given direction is null");
        Maze.Point newPos = entityPos.add(direction);
        if (Maze.getTile(newPos).isObstructive())
            throw new IllegalArgumentException("Entity cannot move onto this tile.");
        if (!newPos.isValid()) throw new IllegalArgumentException("Entity is trying to move onto a nonexistent tile.");
        inMove = true;
        setPos(newPos);
        inMove = false;
        assert entityPos.isValid() && newPos.equals(entityPos) : "Moving the player resulted in the incorrect position.";
        updateObservers();
    }

    /**
     * Overloaded method for move(), takes in a point.
     *
     * @param moveVector Amount to move by.
     */
    public void move(Maze.Point moveVector) {
        if (moveVector == null) throw new IllegalArgumentException("Given point is null");
        Maze.Point newPos = entityPos.add(moveVector);
        if (Maze.getTile(newPos).isObstructive())
            throw new IllegalArgumentException("Entity cannot move onto this tile.");
        if (!newPos.isValid()) throw new IllegalArgumentException("Entity is trying to move onto a nonexistent tile.");
        inMove = true;
        setPos(newPos);
        inMove = false;
        assert entityPos.isValid() && newPos.equals(entityPos) : "Moving the player resulted in the incorrect position.";
        updateObservers();
    }

    /**
     * Overloaded method for move(), that accepts two individual numbers
     * that represent X and Y, respectively.
     *
     * @param moveX The amount to change X by.
     * @param moveY The amount to change Y by.
     */
    public void move(int moveX, int moveY) {
        move(new Maze.Point(moveX, moveY));
    }

    /**
     * Overloaded method for move(), assumes
     * the chosen direction is the direction the entity is facing.
     */
    public void move() {
        move(facingDir);
    }

    /**
     * Combines methods setDir() and move().
     *
     * @param dir The new direction of the entity.
     */
    public void moveAndTurn(Direction dir) {
        setDir(dir);
        move();
    }

    /**
     * @return The position of the entity.
     */
    public Maze.Point getPos() {
        return entityPos;
    }

    /**
     * @return The direction the entity is facing.
     */
    public Direction getDir() {
        return facingDir;
    }

    /**
     * @return The ID of the entity.
     */
    public int id() {
        return id;
    }

    /**
     * Sets the position of the entity.
     *
     * @param pos Point that represents the entity's new position.
     */
    public void setPos(Maze.Point pos) {
        if (pos == null || !pos.isValid()) throw new IllegalArgumentException("Invalid point given.");
        if (Maze.getTile(pos).isObstructive()) throw new IllegalArgumentException("Entity cannot move onto this tile.");
        entityPos = pos;
        if (!inMove) updateObservers();
    }

    /**
     * Sets the direction the entity is facing.
     *
     * @param dir The new direction of the entity.
     */
    public void setDir(Direction dir) {
        if (dir == null) throw new IllegalArgumentException("Given direction is null");
        facingDir = dir;
        updateObservers();
    }

    /**
     * Called when an entity is removed.
     * This method is not abstract because not all entities
     * will need special code to run on deletion.
     */
    public void deleteEntity() {
    }
}
