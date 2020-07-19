package ly.unnecessary.frontend.components.powerups;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;

import javafx.util.Duration;
import ly.unnecessary.frontend.EntityType;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PlayerGunComponent extends Component {
    private TimerAction playergun;
    private double bulletSpawnPeriod = 0.5;
    private boolean switchSides = false;
    private final int marginOfPlayer = 30;

    /**
     * Creates a bullet spawner which will fire a player gun bullet each bullet
     * spawn period.
     */
    @Override
    public void onAdded() {
        PlayerGunType type = FXGLMath.randomBoolean(0.5) ? PlayerGunType.SINGLE : PlayerGunType.DOUBLE;

        if (type == PlayerGunType.DOUBLE)
            bulletSpawnPeriod *= 0.5;

        playergun = run(() -> {
            var player = byType(EntityType.PLAYER).get(0);
            if (type == PlayerGunType.SINGLE) {

                play("beta/shot_" + FXGLMath.random(1, 3) + ".wav"); //TODO: MUSIC

                spawn("playerGunBullet", player.getX() + player.getWidth() / 2, player.getY());
            } else if (type == PlayerGunType.DOUBLE) {

                play("beta/shot_" + FXGLMath.random(1, 3) + ".wav"); //TODO: MUSIC

                if (switchSides)
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