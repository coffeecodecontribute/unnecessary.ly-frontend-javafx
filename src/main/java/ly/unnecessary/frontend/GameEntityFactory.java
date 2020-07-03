package ly.unnecessary.frontend;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
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
import javafx.util.Duration;

import java.awt.geom.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameEntityFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        Rectangle rectangle = new Rectangle(0, 0, (int) data.get("ballRadius"), (int) data.get("ballRadius"));
        //Circle rectangle = new Circle((int) data.get("ballRadius"));
        rectangle.setFill(Color.RED);

        return entityBuilder()
                .type(EntityType.BALL)
                .from(data)
                .viewWithBBox(rectangle)
                .with("velocity", new Point2D((int) data.get("ballSpeed"), (int) data.get("ballSpeed")))
                .with(new BallComponent())
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
        var e = new Rectangle(0,0, 300,30);
        e.setFill(Color.RED);
        return entityBuilder()
                .type(EntityType.PLAYER)
                .from(data)
                .viewWithBBox(e)
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

    //Developer

    @Spawns("point")
    public Entity newPoint(SpawnData data) {
        var pixel = new Rectangle(10,10);
        pixel.setFill(Color.CYAN);
        return entityBuilder()
                .from(data)
                .view(pixel)
                .with(new ExpireCleanComponent(Duration.seconds(0.5)))
                .build();
    }
}
