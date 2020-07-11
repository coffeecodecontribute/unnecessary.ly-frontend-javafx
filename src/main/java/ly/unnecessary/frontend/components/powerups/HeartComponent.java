package ly.unnecessary.frontend.components.powerups;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.entity.component.Component;
import ly.unnecessary.frontend.Application;

public class HeartComponent extends Component {
    @Override
    public void onAdded() {
        inc("playerLives", +1);
    }
}
