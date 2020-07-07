package ly.unnecessary.frontend.components.powerups;

import com.almasb.fxgl.entity.component.Component;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MultiBallComponent extends Component {
    @Override
    public void onAdded() {
        System.out.println("MB");
    }
}