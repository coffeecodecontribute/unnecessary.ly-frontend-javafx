package ly.unnecessary.frontend;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class PlayerComponent extends Component {

    public void moveRight() {
        if(entity.getX() + entity.getWidth() < FXGL.getAppWidth())
            entity.setX(entity.getX() + Application.getPlayerSpeed());
    }

    public void moveLeft() {
        if(entity.getX() > 0)
            entity.setX(entity.getX() - Application.getPlayerSpeed());
    }
}
