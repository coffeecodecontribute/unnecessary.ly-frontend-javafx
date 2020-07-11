package ly.unnecessary.frontend.components.powerups;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import ly.unnecessary.frontend.EntityType;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SuperBallComponent extends Component {

    String superBallTexture = "game/ballBlue.png";

    @Override
    public void onAdded() {
        byType(EntityType.BALL).forEach(this::addSuperBallTexture);
    }

    @Override
    public void onRemoved() {
        byType(EntityType.BALL).forEach(e -> e.getViewComponent().removeChild(texture(superBallTexture, geti("ballRadius"), geti("ballRadius"))));
    }

    public void addSuperBallTexture(Entity e) {
        e.getViewComponent().addChild(texture(superBallTexture, geti("ballRadius"), geti("ballRadius")));
    }
}
