package ly.unnecessary.frontend.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import ly.unnecessary.frontend.Application;

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
