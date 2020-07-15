package ly.unnecessary.frontend.components.powerups;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxgl.dsl.FXGL.texture;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

import ly.unnecessary.frontend.EntityType;

public class SuperBallComponent extends Component {
    Texture t;

    @Override
    public void onAdded() {
        t = texture("game/ballBlue.png", geti("ballRadius"), geti("ballRadius"));
        byType(EntityType.BALL).forEach(e -> e.getViewComponent().addChild(t));
    }

    @Override
    public void onRemoved() {
        byType(EntityType.BALL).forEach(e -> e.getViewComponent().removeChild(t));
    }
}
