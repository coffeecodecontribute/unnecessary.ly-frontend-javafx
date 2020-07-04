package ly.unnecessary.frontend.components;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BallComponent extends Component {

    boolean allowedToChangeCollideDirection = true;

    @Override
    public void onAdded() {
        entity.getTransformComponent().setAnchoredPosition(entity.getCenter());
    }

    @Override
    public void onUpdate(double tpf) {

    }

    public void collide(Point2D point2d) {
        if(allowedToChangeCollideDirection) {
            entity.setProperty("velocity", point2d);
            allowedToChangeCollideDirection = false;
        }
        run(()->{
            allowedToChangeCollideDirection = true;
        }, Duration.seconds(0.1));
    }

    public void collideBlock() {

    }
}
