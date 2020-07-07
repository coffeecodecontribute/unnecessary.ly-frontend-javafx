package ly.unnecessary.frontend.components.powerups;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.entity.action.Action;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;

import javafx.scene.paint.Stop;
import javafx.util.Duration;
import ly.unnecessary.frontend.EntityType;
import ly.unnecessary.frontend.PowerupType;

public class PlayerGunComponent extends Component {
    
    long tick = 0;
    TimerAction playergun;

    @Override
    public void onAdded() {
        playergun = run(() -> {
            var player = byType(EntityType.PLAYER).get(0);
            spawn("playergun", player.getX() + player.getWidth() / 2, player.getY());
        }, Duration.seconds(0.01));
    }

    @Override
    public void onRemoved() {
        playergun.expire();
    }

    @Override
    public void onUpdate(double tpf) {

    }
}