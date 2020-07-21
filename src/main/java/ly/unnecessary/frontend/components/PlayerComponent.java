package ly.unnecessary.frontend.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;
import static ly.unnecessary.frontend.GameApplication.player;
import static ly.unnecessary.frontend.GameApplication.playerSpeed;

/**
 * Handles player movement
 */
public class PlayerComponent extends Component {
    /**
     * Modern Mouse Movement
     * <p>
     * Control the player with your mouse. Benefits of this method are:
     * <ul>
     * <li>faster control</li>
     * <li>more direct impact</li>
     * <li>Can handle multiple balls easy</li>
     * </ul>
     */
    public void mouseMovement() {
        if (getb("freeze"))
            return;

        // gets the cursor and sets it to the middle of the player
        double mouseX = getInput().getMouseXWorld() - player.getWidth() / 2;

        // fixes issue where player is stucked before the max
        if (mouseX < 0)
            player.setX(0);
        else if (mouseX > getAppWidth() - player.getWidth())
            player.setX(getAppWidth() - player.getWidth());
        else
            player.setX(mouseX); // sets the player mouseX if it's in the allowed area
    }

    /**
     * Deprecated control moved to mouse controller Moves the player right. Can be
     * used in initInput
     */
    public void moveRight() {
        if (entity.getX() + entity.getWidth() < FXGL.getAppWidth()) // ensures that the player is not leaving the game
                                                                    // world
            entity.setX(entity.getX() + playerSpeed);
    }

    /**
     * Deprecated control moved to mouse controller Moves the player left. Can be
     * used in initInput
     */
    public void moveLeft() {
        if (entity.getX() > 0) // ensures that the player is not leaving the game world
            entity.setX(entity.getX() - playerSpeed);
    }
}
