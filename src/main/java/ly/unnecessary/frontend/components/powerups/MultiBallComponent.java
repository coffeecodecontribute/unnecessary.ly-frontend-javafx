package ly.unnecessary.frontend.components.powerups;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import ly.unnecessary.frontend.EntityType;
import ly.unnecessary.frontend.components.PlayerComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MultiBallComponent extends Component {

    Entity multiBall;

    @Override
    public void onAdded() {
        multiBall = spawn("ball", byType(EntityType.BALL).get(0).getPosition());
        multiBall.setProperty("velocity", new Point2D(geti("ballSpeed"),geti("ballSpeed")));
    }

    @Override
    public void onRemoved() {

    }
}