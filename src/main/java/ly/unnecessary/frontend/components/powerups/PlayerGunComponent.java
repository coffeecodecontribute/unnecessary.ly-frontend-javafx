package ly.unnecessary.frontend.components.powerups;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;

import javafx.scene.paint.Stop;
import javafx.util.Duration;
import ly.unnecessary.frontend.EntityType;
import ly.unnecessary.frontend.PowerupType;

enum Type {
    SINGLE, DOUBLE
}

public class PlayerGunComponent extends Component {
    
    private TimerAction playergun;
    private double bulletSpawnPeriod = 0.5;
    private boolean switchSides = false;
    private final int marginOfPlayer = 30;

    /**
     * Creates a bullet spawner which will fire a player gun bullet each bullet spawn period.
     */
    @Override
    public void onAdded() {
        Type type = FXGLMath.randomBoolean(0.1) ? Type.SINGLE : Type.DOUBLE;

        if(type == Type.DOUBLE)
            bulletSpawnPeriod *= 0.5;

        playergun = run(() -> {
            var player = byType(EntityType.PLAYER).get(0);
            if(type == Type.SINGLE) {
                spawn("playerGunBullet", player.getX() + player.getWidth() / 2, player.getY());
            } else if(type == Type.DOUBLE) {
                if(switchSides)
                    spawn("playerGunBullet", player.getX() + marginOfPlayer, player.getY());
                 else
                    spawn("playerGunBullet", player.getX() + player.getWidth() - marginOfPlayer, player.getY());
                switchSides = !switchSides;
            }
        }, Duration.seconds(bulletSpawnPeriod));
    }

    /**
     * Removes the bullet spawner when the Entity is removed
     */
    @Override
    public void onRemoved() {
        playergun.expire();
    }

}