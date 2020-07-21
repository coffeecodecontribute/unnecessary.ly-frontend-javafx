package ly.unnecessary.frontend;

/**
 * Define all Entity Types. Used in game logic to identify entity.
 * <ul>
 * <li>PLAYER - the one and only player</li>
 * <li>BRICK - any brick in a level</li>
 * <li>BALL - any ball in a level</li>
 * <li>WALL - the gameboard wall</li>
 * <li>LEVELBRICK - any not destroyable brick in a level</li>
 * <li>ACTIONBRICK - infected brick (spawns onRemove from a brick)</li>
 * <li>POWERUPDROP - a power up drop with a type</li>
 * </ul>
 */
public enum EntityType {
    PLAYER, BRICK, LEVELBRICK, ACTIONBRICK, BALL, WALL, POWERUPDROP
}
