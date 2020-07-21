package ly.unnecessary.frontend.components.powerups;

/**
 * There are two types of the player gun.
 * <p>
 * Double       Single
 * <p>
 * |        |
 * |             |
 * |        |
 * |             |
 * _____       _____
 * <p>
 * Double spawns two bullets left, right. Single spawns one bullet in the center of the player. When the powerup is picked up, it will randomly pick one type.
 */
enum PlayerGunType {
    SINGLE, DOUBLE
}
