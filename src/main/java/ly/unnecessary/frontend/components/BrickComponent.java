package ly.unnecessary.frontend.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import static com.almasb.fxgl.dsl.FXGL.*;

public class BrickComponent extends Component {

    private int lives = 1;

    public void hitByBall() {
        lives--;

        if(lives == 0) {
            if(FXGLMath.randomBoolean(0.3f))
                spawn("actionBrick", entity.getPosition());
            entity.removeFromWorld();
        } else
            System.out.println("Lives: " + lives);
    }
}
