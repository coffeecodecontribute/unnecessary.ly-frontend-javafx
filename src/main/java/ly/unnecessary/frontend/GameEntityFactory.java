package ly.unnecessary.frontend;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.awt.geom.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameEntityFactory implements EntityFactory {


    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        int radius = 24;

        Rectangle rectangle = new Rectangle(0, 0, 42, 42);
        rectangle.setFill(Color.RED);

        return entityBuilder()
                .type(EntityType.BALL)
                .from(data)
                .viewWithBBox("ball.png")
                .with("velocity", new Point2D(100, 100))
                //.with(new BallComponent())
                .collidable()
                .build();
    }


    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        return entityBuilder()
                .type(EntityType.BRICK)
                .from(data)
                .viewWithBBox("brick.png")
                .collidable()
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder()
                .type(EntityType.PLAYER)
                .from(data)
                .viewWithBBox("player.png")
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .from(data)
                .view(new Rectangle(getAppWidth(), getAppHeight()))
                .build();
    }

}
