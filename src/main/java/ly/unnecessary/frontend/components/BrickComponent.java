package ly.unnecessary.frontend.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import ly.unnecessary.frontend.Application;
import ly.unnecessary.frontend.EntityType;
import ly.unnecessary.frontend.PowerupType;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BrickComponent extends Component {

    private int lives = 1;
    private boolean blockIsInfected = false;

    public void hitByBall() {
        lives--;

        if(lives == 0) {
            if(FXGLMath.randomBoolean(0.1f)) {
                play("alpha/power_up.wav");
                spawn("actionBrick", entity.getPosition());
                blockIsInfected = true;
            }
            entity.removeFromWorld();
        } else
            System.out.println("Lives: " + lives);

        if (FXGLMath.randomBoolean(1f) && !blockIsInfected) {
            if (byType(EntityType.POWERUPDROP).isEmpty()) {
                PowerupType powerUp = PowerupType.pickPowerUp();
                if (powerUp != null) {
                    spawn("powerupdrop", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY()).put("type", powerUp.getType()).put("texture", powerUp.getTextureString()));
                }
            }
        }
    }
}
