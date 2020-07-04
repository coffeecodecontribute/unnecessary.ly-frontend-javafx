package ly.unnecessary.frontend;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ly.unnecessary.frontend.components.BallComponent;
import ly.unnecessary.frontend.components.PlayerComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameEntityFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data) {

        Rectangle rectangle = new Rectangle(0, 0, (int) geti("ballRadius"), (int) geti("ballRadius"));
        //Circle rectangle = new Circle((int) data.get("ballRadius"), (int) data.get("ballRadius"), (int) data.get("ballRadius"));
        rectangle.setFill(Color.RED);

        return entityBuilder()
                .type(EntityType.BALL)
                .from(data)
                .viewWithBBox(rectangle)
                .with("velocity", new Point2D((int) geti("ballSpeed"), (int) geti("ballSpeed")))
                .with(new BallComponent())
                .collidable()
                .build();
    }


    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        Rectangle brick = new Rectangle(0, 0, 500, 500);
        brick.setFill(Color.RED);
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
        var pixel = new Rectangle(2,2);
        pixel.setFill(Color.CYAN);
        return entityBuilder()
                .from(data)
                .view(pixel)
                .with(new ExpireCleanComponent(Duration.seconds(0.5)))
                .build();
    }
}
