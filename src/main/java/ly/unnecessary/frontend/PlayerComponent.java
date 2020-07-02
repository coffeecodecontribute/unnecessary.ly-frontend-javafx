package ly.unnecessary.frontend;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class PlayerComponent extends Component {

    int playerSpeed = 15;

    public void moveRight() {
        if(entity.getX() + entity.getWidth() < FXGL.getAppWidth())
            entity.setX(entity.getX() + playerSpeed);
    }

    public void moveLeft() {
        if(entity.getX() > 0)
            entity.setX(entity.getX() - playerSpeed);
    }
}
