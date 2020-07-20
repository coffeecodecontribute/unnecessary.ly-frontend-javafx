package ly.unnecessary.frontend;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.texture;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.texture.Texture;

public enum PowerupType {
    // MULTIBALL, SCOREBOMB, PLAYERGUN, SPAWNMULTIBALL, SPAWNSCOREBOMB,
    // SPAWNPLAYERGUN

    // PowerUps it self
    MULTIBALL("MULTIBALL", "game/powerups/extraball.png"), PLAYERGUN("PLAYERGUN", "game/powerups/playergun.png"),
    HEART("HEART", "game/powerups/heart.png"), SUPERBALL("SUPERBALL", "game/powerups/superball.png"),

    // PowerUps Helper
    PLAYERGUN_BULLET("PLAYERGUN_BULLET", "game/powerups/playergun.png");

    private final String type;
    private final Texture texture;
    private final String textureString;

    /**
     * Creates power up with type and color
     *
     * @param type          type of power up - ensure the type is used in collision
     *                      logic
     * @param textureString color of the power up drop - player can identify the
     *                      power up
     */
    PowerupType(String type, String textureString) {
        this.type = type;
        this.texture = texture(textureString);
        this.textureString = textureString;
    }

    /**
     * Picks a random power up which is not active. If all power ups are active it
     * will return null.
     * 
     * @return A power up reference with necessary information
     */
    public static PowerupType pickPowerUp() {
        PowerupType[] powerUpTypes = { PowerupType.MULTIBALL, PowerupType.PLAYERGUN, PowerupType.HEART, PowerupType.SUPERBALL }; // Pool of PowerUps

        int selectedPowerUp;
        boolean notFound = true;
        int checkCount = 0;

        selectedPowerUp = FXGLMath.random(0, powerUpTypes.length - 1);
        while (notFound) {
            // Ends search after all checked
            if (checkCount > powerUpTypes.length) {
                selectedPowerUp = -1;
                return null;
            }

            // Check if power up is already spawned
            if (byType(powerUpTypes[selectedPowerUp]).isEmpty()) {
                notFound = false;
            } else {
                selectedPowerUp = (selectedPowerUp + 1) % powerUpTypes.length; // Moves counter if the first picked already is active
                checkCount++;
            }
        }

        return powerUpTypes[selectedPowerUp];
    }

    /**
     * Getter of Type
     * 
     * @return type of current power up
     */
    public String getType() {
        return type;
    }

    /**
     * Getter of Texture Name
     *
     * @return texture name
     */
    public String getTextureString() {
        return textureString;
    }
}