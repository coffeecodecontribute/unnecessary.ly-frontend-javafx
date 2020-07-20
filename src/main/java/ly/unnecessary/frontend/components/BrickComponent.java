package ly.unnecessary.frontend.components;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;

import com.almasb.fxgl.texture.Texture;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ly.unnecessary.frontend.EntityType;
import ly.unnecessary.frontend.PowerupType;

public class BrickComponent extends Component {
    private boolean blockIsInfected = false;
    private int type;

    public BrickComponent() {
        this(1);
    }

    public BrickComponent(int type) {
        this.type = type;
    }

    public void hitByBall() {
        var hp = entity.getComponent(HealthIntComponent.class);

        if(!byType(PowerupType.SUPERBALL).isEmpty())
            hp.damage(3);

        hp.damage(1);
        if(hp.getValue() == 2) {
            Texture rc = texture("game/bricks/cracked_1.png", 128, 36);
            //rc.setFill(Color.DARKGRAY);
            entity.getViewComponent().addChild(rc);
        }
        if(hp.getValue() == 1) {
            //Rectangle rc = new Rectangle(0,0,128,36);
            //rc.setFill(Color.WHITE);
            Texture rc = texture("game/bricks/cracked_2.png", 128, 36);

            entity.getViewComponent().addChild(rc);
        }
        if (hp.getValue() == 0) {
            if (FXGLMath.randomBoolean(0.1f)) {
                play("alpha/power_up.wav");
                spawn("actionBrick", entity.getPosition());
                blockIsInfected = true;
            } else {
                spawn("brickBroken", new SpawnData(entity.getX() + entity.getWidth() / 2 - 72, entity.getY()).put("type", this.type));
            }
            entity.removeFromWorld();
        } else
            System.out.println("Lives: " + hp.getValue());

        if (FXGLMath.randomBoolean(0.1f) && !blockIsInfected) {
            if (byType(EntityType.POWERUPDROP).isEmpty()) {
                PowerupType powerUp = PowerupType.pickPowerUp();
                if (powerUp != null) {
                    spawn("powerupdrop", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY())
                            .put("type", powerUp.getType()).put("texture", powerUp.getTextureString()));
                }
            }
        }
    }

    public int getLives() {
        return 0;
    }
}
