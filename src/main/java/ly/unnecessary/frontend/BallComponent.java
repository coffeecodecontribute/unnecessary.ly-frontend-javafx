package ly.unnecessary.frontend;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BallComponent extends Component {

    @Override
    public void onAdded() {
        entity.getTransformComponent().setAnchoredPosition(entity.getCenter());
    }

    @Override
    public void onUpdate(double tpf) {

    }

    public void collide(int i) {

    }

    public void collideBlock() {

    }
}
