package ly.unnecessary.frontend.components.powerups;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxgl.dsl.FXGL.spawn;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;
import ly.unnecessary.frontend.EntityType;

public class MultiBallComponent extends Component {
    Entity multiBall;

    @Override
    public void onAdded() {
        multiBall = spawn("ball", byType(EntityType.BALL).get(0).getPosition());
        multiBall.setProperty("velocity", new Point2D(geti("ballSpeed"), geti("ballSpeed")));
    }

    @Override
    public void onRemoved() {

    }
}