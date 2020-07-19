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
    private int lastWallCollide = 0;

    @Override
    public void onAdded() {
        // entity.getTransformComponent().setAnchoredPosition(entity.getCenter()); TODO:
        // Required?
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D velocity = entity.getObject("velocity");
        entity.translate(velocity);

        if (entity.getY() < 0) {
            entity.getComponent(BallComponent.class).collideWall(new Point2D(velocity.getX(), -velocity.getY()), 1); // TOP
        } else if (entity.getY() + entity.getWidth() > getAppHeight()) {
            entity.getComponent(BallComponent.class).collideWall(new Point2D(velocity.getX(), -velocity.getY()), 3); // BOTTOM
        } else if (entity.getX() < 0) {
            entity.getComponent(BallComponent.class).collideWall(new Point2D(-velocity.getX(), velocity.getY()), 4); // LEFT
        } else if (entity.getX() + entity.getHeight() > getAppWidth()) {
            entity.getComponent(BallComponent.class).collideWall(new Point2D(-velocity.getX(), velocity.getY()), 2); // RIGHT
        }
    }

    /**
     * With last wall collide we want to fix issues causing the ball to stuck in the wall.
     * We handle that by only allowing a collision once with a the same wall.
     * We do that by tracking the last collided wall and only accepting other walls, bricks and the player as next collision object.
     * <ul>
     *     <li>0 = collided with any brick or player</li>
     *     <li>1 = top wall</li>
     *     <li>2 = top right</li>
     *     <li>3 = top bottom</li>
     *     <li>4 = top left</li>
     * </ul>
     *
     * |-------1--------|
     * |                |
     * 4                2
     * |                |
     * |-------3--------|
     *
     * @param point2D the new direction after collide
     * @param currentWallCollide the current wall with which you collided in form of an integer
     */
    public void collideWall(Point2D point2D, int currentWallCollide) {
        if(currentWallCollide != lastWallCollide) {
            lastWallCollide = currentWallCollide;
            entity.setProperty("velocity", point2D);
        }

    }

    /**
     * Collide with brick or player
     * @param point2d
     */
    public void collide(Point2D point2d) {
        lastWallCollide = 0;

        if (allowedToChangeCollideDirection) {
            entity.setProperty("velocity", point2d);
            allowedToChangeCollideDirection = false;
        }

        run(() -> {
            allowedToChangeCollideDirection = true;
        }, Duration.millis(1));
    }

    /**
     * Ball release in direction of Mouse
     */
    public void release() {
        Vec2 vel = new Vec2(getInput().getMouseXWorld(), getInput().getMouseYWorld());
        vel.subLocal(new Vec2(entity.getPosition()));
        vel.setLength(16); // Fixed issue with ball by setting a fixed length
        entity.setProperty("velocity", vel.toPoint2D());
    }
}
