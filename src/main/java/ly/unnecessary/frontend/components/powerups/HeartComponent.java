package ly.unnecessary.frontend.components.powerups;

import static com.almasb.fxgl.dsl.FXGL.inc;
import static ly.unnecessary.frontend.GameApplication.uiController;

import com.almasb.fxgl.entity.component.Component;

/**
 * Heart Power Up Component
 * Will spawn when the player is collecting a heart power up.
 */
public class HeartComponent extends Component {

    /**
     * Increases Health Points by one when the player is collecting it.
     */
    @Override
    public void onAdded() {
        inc("playerLives", +1);
        uiController.addLife(); // Update UI
    }
}
