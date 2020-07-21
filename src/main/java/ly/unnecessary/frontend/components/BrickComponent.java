package ly.unnecessary.frontend.components;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static ly.unnecessary.frontend.GameApplication.*;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;

import com.almasb.fxgl.texture.Texture;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ly.unnecessary.frontend.EntityType;
import ly.unnecessary.frontend.PowerupType;

/**
 * Brick Component
 */
public class BrickComponent extends Component {
    private boolean blockIsInfected = false;
    private final int type;

    /**
     * Default constructor. Sets brick type to default (1).
     */
    public BrickComponent() {
        this(1);
    }

    /**
     * Brick constructor.
     * @param type set type from 1 - 3 (1 : white, 2 : blue, 3 : red). The type also sets the lives a brick has.
     */
    public BrickComponent(int type) {
        this.type = type;
    }

    /**
     * When the ball is hitting a brick this method is called. It updates the texture, makes the brick an infected brick or spawns a power up.
     */
    public void hitByBall() {
        var hp = entity.getComponent(HealthIntComponent.class); //get the current hp from the brick

        if(!byType(PowerupType.SUPERBALL).isEmpty()) //destroy the brick if superball is active
            hp.damage(3);

        hp.damage(1); // remove one life

        //add cracked textures
        if(getHealthPoints() == 2) {
            Texture rc = texture("game/bricks/cracked_1.png", brickWidth, brickHeight);
            entity.getViewComponent().addChild(rc);
        }
        if(getHealthPoints() == 1) {
            Texture rc = texture("game/bricks/cracked_2.png", brickWidth, brickHeight);
            entity.getViewComponent().addChild(rc);
        }

        //Spawns infected brick
        if (getHealthPoints() == 0) {
            if (FXGLMath.randomBoolean(chanceForInfected)) {
                spawn("actionBrick", entity.getPosition());
                blockIsInfected = true;
            } else {
                spawn("brickBroken", new SpawnData(entity.getX() + entity.getWidth() / 2 - 72, entity.getY()).put("type", this.type));
            }
            entity.removeFromWorld();
        }

        //Spawns power up
        if (FXGLMath.randomBoolean(chanceForDrop) && !blockIsInfected) {
            if (byType(EntityType.POWERUPDROP).isEmpty()) {
                PowerupType powerUp = PowerupType.pickPowerUp();
                if (powerUp != null) {
                    spawn("powerupdrop", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY())
                            .put("type", powerUp.getType()).put("texture", powerUp.getTextureString()));
                }
            }
        }
    }

    /**
     * Get the current hp
     * @return current hp of an block
     */
    public int getHealthPoints() {
        return entity.getComponent(HealthIntComponent.class).getValue();
    }
}
