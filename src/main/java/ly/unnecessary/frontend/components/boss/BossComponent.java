package ly.unnecessary.frontend.components.boss;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;
import ly.unnecessary.frontend.EntityType;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BossComponent extends Component {
    int velocity = 5;
    int speed;
    long frameCount = 0;

    boolean isFreezed = false;

    TimerAction t;

    @Override
    public void onAdded() {
        System.out.println("Boss is there");
        t = run(() -> {

        }, Duration.seconds(1));

    }

    @Override
    public void onUpdate(double tpf) {
        var hp = entity.getComponent(HealthIntComponent.class);
        if(hp.getValue() < 4) {
            isFreezed = false;
            velocity = 8;
            if(frameCount % 30 == 0) {
                Vec2 dir = Vec2.fromAngle(45);
                spawn("bossShotBullet", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight()).put("dir", dir));
                dir = Vec2.fromAngle(90);
                spawn("bossShotBullet", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight()).put("dir", dir));
                dir = Vec2.fromAngle(135);
                spawn("bossShotBullet", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight()).put("dir", dir));
            }
        }
        else {
            if(frameCount % 280 == 0) {
                isFreezed = true;
            }
            if(frameCount % 300 == 0) {
                Vec2 dir = Vec2.fromAngle(45);
                spawn("bossShotBullet", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight()).put("dir", dir));
                dir = Vec2.fromAngle(90);
                spawn("bossShotBullet", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight()).put("dir", dir));
                dir = Vec2.fromAngle(135);
                spawn("bossShotBullet", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight()).put("dir", dir));
            } else if(frameCount % 60 == 0) {
                Vec2 dir = Vec2.fromAngle(90);
                spawn("bossShotBullet", new SpawnData(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight()).put("dir", dir));
            }

            if(frameCount % 320 == 0) {
                isFreezed = false;
            }
        }
        if(isFreezed == false) {
            if(entity.getX() > byType(EntityType.PLAYER).get(0).getX())
                speed = -velocity;
            else if(entity.getX() < byType(EntityType.PLAYER).get(0).getX())
                speed = velocity;
            entity.translateX(speed);
        }
        frameCount++;
    }

    @Override
    public void onRemoved() {
        t.expire();
        System.out.println("Boss is dead");
    }
}
