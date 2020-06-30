package ly.unnecessary.frontend;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class PlayerComponent extends Component {

    int playerSpeed = 10;

    public void moveRight() {
        System.out.println(entity.getX());
        if(entity.getX() + entity.getWidth() < FXGL.getAppWidth())
            entity.setX(entity.getX() + playerSpeed);
    }

    public void moveLeft() {
        System.out.println(entity.getX());
        if(entity.getX() > 0)
            entity.setX(entity.getX() - playerSpeed);
    }
}
