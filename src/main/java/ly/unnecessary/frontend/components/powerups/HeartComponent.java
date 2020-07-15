package ly.unnecessary.frontend.components.powerups;

import static com.almasb.fxgl.dsl.FXGL.inc;

import com.almasb.fxgl.entity.component.Component;

public class HeartComponent extends Component {
    @Override
    public void onAdded() {
        inc("playerLives", +1);
    }
}
