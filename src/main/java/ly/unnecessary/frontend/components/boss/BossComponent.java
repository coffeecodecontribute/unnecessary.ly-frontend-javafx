package ly.unnecessary.frontend.components.boss;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;
import ly.unnecessary.frontend.EntityType;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BossComponent extends Component {
    int v = 5;
    int speed;
    long frameCount = 0;

    TimerAction t;

    @Override
    public void onAdded() {
        System.out.println("Boss is there");
        t = run(() -> {

        }, Duration.seconds(1));
    }

    @Override
    public void onUpdate(double tpf) {
        if(frameCount % 60 == 0) {
            spawn("bossShotBullet", entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight());
        }
        if(entity.getX() > byType(EntityType.PLAYER).get(0).getX())
            speed = -v;
        else if(entity.getX() < byType(EntityType.PLAYER).get(0).getX())
            speed = v;

        entity.translateX(speed);

        frameCount++;
    }

    @Override
    public void onRemoved() {
        t.expire();
        System.out.println("Boss is dead");
    }
}
