package ly.unnecessary.frontend.controller;

/**
 * Handles the Player Physics to give the player control over the ball
 */
public class GamePlayerPhysicController {

    /**
     * Calculates the angle of the ball in collision with the player
     *
     * @param ballX       the current ball position of X
     * @param playerX     the current player position of X
     * @param playerWidth the current with of the player
     * @return angle in rad where the ball should go
     */
    public static double calculateAngle(double ballX, double playerX, double playerWidth) {
        double collidePoint = getCollidePoint(ballX, playerX, playerWidth);

        collidePoint = getCollidePointNormalize(collidePoint, playerWidth);

        return collidePoint * Math.PI / 3; // Multiplies the normalized collide point with 60° as maximum angle the ball can get.
    }

    /**
     * Calculates the collide Point of the ball with the player. Left is leading in -player.width/2, middle in 0, right in +player.width/2
     *
     * @param ballX       the current ball position of X
     * @param playerX     the current player position of X
     * @param playerWidth the current with of the player
     * @return collide point in relative relation of the player width
     */
    public static double getCollidePoint(double ballX, double playerX, double playerWidth) {
        return ballX - (playerX + playerWidth / 2);
    }

    /**
     * Normalize the player collide Point into normalized variables. Left is leading in -1, middle in 0, right in +1
     *
     * @param collidePoint collide point in relative relation of the player width
     * @param playerWidth  the current with of the player
     * @return collide point in normalized variable
     */
    public static double getCollidePointNormalize(double collidePoint, double playerWidth) {
        return collidePoint / (playerWidth / 2);
    }
}
