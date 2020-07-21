package ly.unnecessary.frontend.components.boss;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import ly.unnecessary.frontend.EntityType;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

/**
 * Boss Component
 */
public class BossComponent extends Component {
    int velocity = 5; //velocity of boss
    int speed; //speed of boss
    long frameCount = 0;

    boolean isFreezed = false;

    /**
     * Update method for boss. Handles all the "ai" logic.
     * Boss is split in three phases:
     * <ol>
     *     <li>Start Phase: A few seconds before the fight the boss is not firing.</li>
     *     <li>Middle Phase: The boss is firing normal bullets and each 300 frames a shotgun bullet.</li>
     *     <li>End Phase: When the boss has only a few lives he will get faster and start only firing shotgun bullets. This can be very hard.</li>
     * </ol>
     *
     * @param tpf time per frame
     */
    @Override
    public void onUpdate(double tpf) {
        var hp = entity.getComponent(HealthIntComponent.class);

        if (frameCount > 50) { // start phase
            if (hp.getValue() < 4) { //  end phase
                isFreezed = false;
                velocity = 8;
                if (frameCount % 50 == 0) {
                    fireShootGun();
                }
            } else { // middle phase
                if (frameCount % 280 == 0) {
                    isFreezed = true;
                }
                if (frameCount % 300 == 0) {
                    fireShootGun();
                } else if (frameCount % 60 == 0) { // fire 1 bullet straight down
                    fireBullet(90);
                }

                if (frameCount % 320 == 0) {
                    isFreezed = false;
                }
            }
        }

        //handles the movement of boss
        if (isFreezed == false) {
            if (Math.abs(entity.getX() - byType(EntityType.PLAYER).get(0).getX()) < 100)
                speed = 0;
            else if (entity.getX() > byType(EntityType.PLAYER).get(0).getX()) {
                speed = -velocity;
                entity.getViewComponent().getChildren().get(0).setScaleX(1);
            } else if (entity.getX() < byType(EntityType.PLAYER).get(0).getX()) {
                speed = velocity;
                entity.getViewComponent().getChildren().get(0).setScaleX(-1);
            }
            entity.translateX(speed); //applies speed to boss
        }

        frameCount++;
    }

    /**
     * Fire boss bullet with direction
     *
     * @param angle direction where the bullet goes. 0 -> Right | 90° -> Down ...
     */
    public void fireBullet(double angle) {

        if (geti("gameStatus") == 0) //do not fire when the player is in pre Game
            return;

        Vec2 dir = Vec2.fromAngle(angle); //creates a dir from the angle
        spawn("bossShotBullet",
                new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight())
                        .put("dir", dir)); //spawns bullet at bottom center pos of boss

    }

    /**
     * Shotgun Bullet
     * fires 3 bullets in 45°, 90° and 135°
     */
    public void fireShootGun() {

        //fires 3 times with +45 ° each time
        for (int i = 45; i < 136; i += 45) {
            fireBullet(i);
        }
    }
}
