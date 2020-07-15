package ly.unnecessary.frontend.components;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getInput;
import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.run;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;
import javafx.util.Duration;

public class BallComponent extends Component {
    private boolean allowedToChangeCollideDirection = true;

    @Override
    public void onAdded() {
        // entity.getTransformComponent().setAnchoredPosition(entity.getCenter()); TODO:
        // Required?
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D velocity = entity.getObject("velocity");
        entity.translate(velocity);

        // System.out.println(Math.abs(velocity.getX()) + Math.abs(velocity.getY())); //
        // TODO: Gets Ball Speed; Ball speed is to slow after first collide.

        if (entity.getY() < 0) {
            entity.getComponent(BallComponent.class).collide(new Point2D(velocity.getX(), -velocity.getY())); // TOP
            play("alpha/ball_collide_wall_1_arp.wav");
        } else if (entity.getY() + entity.getWidth() > getAppHeight()) {
            entity.getComponent(BallComponent.class).collide(new Point2D(velocity.getX(), -velocity.getY())); // BOTTOM
            play("alpha/ball_collide_wall_1_arp.wav");
        } else if (entity.getX() < 0) {
            entity.getComponent(BallComponent.class).collide(new Point2D(-velocity.getX(), velocity.getY())); // LEFT
            play("alpha/ball_collide_wall_1_arp.wav");
        } else if (entity.getX() + entity.getHeight() > getAppWidth()) {
            entity.getComponent(BallComponent.class).collide(new Point2D(-velocity.getX(), velocity.getY())); // RIGHT
            play("alpha/ball_collide_wall_1_arp.wav");
        }
    }

    public void collide(Point2D point2d) {
        if (allowedToChangeCollideDirection) {
            entity.setProperty("velocity", point2d);
            allowedToChangeCollideDirection = false;
        }

        run(() -> {
            allowedToChangeCollideDirection = true;
        }, Duration.millis(1));
    }

    public void release() {
        Vec2 vel = new Vec2(getInput().getMouseXWorld(), getInput().getMouseYWorld());
        vel.subLocal(new Vec2(entity.getPosition()));
        vel.setLength(16); // Fixed issue with ball by setting a fixed length
        entity.setProperty("velocity", vel.toPoint2D());
    }
}
