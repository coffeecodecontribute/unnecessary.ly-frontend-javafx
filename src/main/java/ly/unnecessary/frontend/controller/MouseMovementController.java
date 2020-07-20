package ly.unnecessary.frontend.controller;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static ly.unnecessary.frontend.GameApplication.*;

public class MouseMovementController {

    /**
     * sets player'x position to the mouse'x
     */
    public static void mouseMovement() {
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
}
