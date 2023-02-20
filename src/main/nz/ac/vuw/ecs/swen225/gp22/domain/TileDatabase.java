package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Used by persistency module to create tiles based on an ID (for loading),
 * as well as get the ID of a current object (for saving).
 *
 * @author Abdul
 * @version 1.2
 */
public class TileDatabase {
    /**
     * Map of tile classes with a string as the key/ID.
     */
    private static Map<String, Class<? extends Tile>> tileDB = new HashMap<>() {{
        put("ground", Ground.class);
        put("wall", Wall.class);
        put("info", InfoField.class);
        put("key", Key.class);
        put("treasure", Treasure.class);
        put("door", LockedDoor.class);
        put("exit-gate", LockedExit.class);
        put("exit", Exit.class);
        put("death", MilkPuddle.class);
        put("bounce-pad", BouncyPad.class);
    }};

    /**
     * Factory method that creates a Tile object based on the tile ID.
     * Makes sure the given parameters are the correct amount and with
     * the correct types. Suppresses unchecked warnings because I know that any constructor
     * from a class in tileDB will be of the correct constructor type.
     *
     * @param tileID     ID of the requested tile.
     * @param tileParams Array of the given tile parameters.
     * @return The newly created tile Tile.
     */
    @SuppressWarnings("unchecked")
    public static Tile create(String tileID, Object... tileParams) {
        if (!tileDB.containsKey(tileID)) throw new IllegalArgumentException("Tile ID doesn't exist.");
        Constructor<? extends Tile> c = (Constructor<? extends Tile>) tileDB.get(tileID).getConstructors()[0];
        if (c.getParameterCount() != tileParams.length) throw new IllegalArgumentException("Invalid parameter count.");
        for (int i = 0; i < c.getParameterCount(); i++) {
            if (tileParams[i].getClass() != c.getParameterTypes()[i] && tileParams[i].getClass().getSuperclass() != c.getParameterTypes()[i]) {
                throw new IllegalArgumentException("Invalid parameter types.");
            }
        }

        try {
            return c.newInstance(tileParams);
        } catch (Exception e) {
            // Should not be reachable because of preconditions. 
            throw new IllegalStateException("Failure creating tile object.");
        }
    }

    /**
     * Gets the ID of a tile class.
     *
     * @param clazz Class to get ID of.
     * @return String ID of the class
     */
    public static String getID(Class<? extends Tile> clazz) {
        return tileDB.keySet().stream().filter(t -> tileDB.get(t) == clazz).findFirst()
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("No such class exists in database.");
                });
    }

    /**
     * Overloaded method for getID() with Tile
     * object as the parameter.
     *
     * @param tile Tile object to get ID of.
     * @return String ID of the object's class.
     */
    public static String getID(Tile tile) {
        return getID(tile.getClass());
    }
}
