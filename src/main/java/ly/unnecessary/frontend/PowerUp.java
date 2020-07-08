package ly.unnecessary.frontend;

import com.almasb.fxgl.core.math.FXGLMath;
import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.scene.paint.Color;

public class PowerUp {

    private final String type;
    private final Color color;

    /**
     * Creates power up with type and color
     * @param type type of power up - ensure the type is used in collision logic
     * @param color color of the power up drop - player can identify the power up
     */
    public PowerUp(String type, Color color) {
        this.type = type;
        this.color = color;
    }

    /**
     * Picks a random power up which is not active. If all power ups are active it will return null.
     * @return A power up reference with necessary information
     */
    public static PowerUp pickPowerUp() {
        PowerUp[] powerUps = {new PowerUp("MULTIBALL", Color.RED), new PowerUp("PLAYERGUN", Color.YELLOW)}; // Pool of Power ups
        PowerupType[] powerUpTypes = {PowerupType.MULTIBALL, PowerupType.PLAYERGUN}; //Pool of Types! Warning: Needs to be the same order as list above.

        int selectedPowerUp;
        boolean notFound = true;
        int checkCount = 0;

        selectedPowerUp = FXGLMath.random(0, powerUps.length - 1);
        while(notFound) {
            //Ends search after all checked
            if(checkCount > powerUps.length) {
                selectedPowerUp = -1;
                return null;
            }

            //Check if power up is already spawned
            if(byType(powerUpTypes[selectedPowerUp]).isEmpty()) {
                notFound = false;
            }
            else {
                selectedPowerUp = (selectedPowerUp + 1) % powerUps.length; //Moves counter if the first picked already is active
                checkCount++;
            }
        }

        return powerUps[selectedPowerUp];
    }

    /**
     * Getter of Type
     * @return type of current power up
     */
    public String getType() {
        return type;
    }

    /**
     * Getter of Color
     * @return color of current power up
     */
    public Color getColor() {
        return color;
    }
}
