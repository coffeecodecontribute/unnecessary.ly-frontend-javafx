package ly.unnecessary.frontend.components.powerups;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.Node;
import ly.unnecessary.frontend.EntityType;

import static com.almasb.fxgl.dsl.FXGL.*;

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
