package ly.unnecessary.frontend.components.powerups;

import com.almasb.fxgl.entity.component.Component;
import ly.unnecessary.frontend.EntityType;
import ly.unnecessary.frontend.components.BallComponent;

import static com.almasb.fxgl.dsl.FXGL.byType;

/**
 * Super Ball Power Up Component
 * Will spawn when the player is collecting a super ball power up.
 */
public class SuperBallComponent extends Component {

    /**
     * Adds the super ball texture to all balls
     */
    @Override
    public void onAdded() {
        byType(EntityType.BALL).forEach(e -> e.getComponent(BallComponent.class).addSuperBallTexture());
    }

    /**
     * Removes the super ball texture from all balls
     */
    @Override
    public void onRemoved() {
        byType(EntityType.BALL).forEach(e -> e.getComponent(BallComponent.class).removeSuperBallTexture());
    }
}
