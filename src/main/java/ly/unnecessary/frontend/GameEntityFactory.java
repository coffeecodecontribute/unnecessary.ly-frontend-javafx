package ly.unnecessary.frontend;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ly.unnecessary.frontend.components.BallComponent;
import ly.unnecessary.frontend.components.BrickComponent;
import ly.unnecessary.frontend.components.PlayerComponent;
import ly.unnecessary.frontend.components.powerups.MultiBallComponent;
import ly.unnecessary.frontend.components.powerups.PlayerGunComponent;
import org.w3c.dom.css.Rect;

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
                .with("velocity", new Point2D(0,0))
                .with(new BallComponent())
                .collidable()
                .build();
    }


    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        Rectangle brick = new Rectangle(0, 0, 128, 36);
        brick.setFill(data.get("color"));

        return entityBuilder()
                .type(EntityType.BRICK)
                .from(data)
                .viewWithBBox(brick)
                .with(new BrickComponent())
                .collidable()
                .build();
    }

    @Spawns("actionBrick")
    public Entity newActionBrick(SpawnData data) {
        Rectangle brick = new Rectangle(0, 0, 128, 36);
        brick.setFill(Color.DARKGRAY);
        Vec2 dir = Vec2.fromAngle(90);
        return entityBuilder()
                .from(data)
                .viewWithBBox(brick)
                .with(new ProjectileComponent(dir.toPoint2D(), 500).allowRotation(false))
                .with(new OffscreenCleanComponent())
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

    @Spawns("powerupdrop")
    public Entity newPowerupdrop(SpawnData data) {
        Rectangle rectangle = new Rectangle(0, 0, 10, 10);
        Vec2 dir = Vec2.fromAngle(90);
        rectangle.setFill(data.get("color"));

        return entityBuilder()
                .type(EntityType.POWERUPDROP)
                .from(data)
                .viewWithBBox(rectangle)
                .with(new ProjectileComponent(dir.toPoint2D(), 500))
                .with(new OffscreenCleanComponent())
                .collidable()
                .with("type", data.get("type"))
                .build();
    }

    /*
    @Spawns("powerup")
    public Entity newPowerup(SpawnData data) {

        return entityBuilder()
                .from(data)
                .type(PowerupType.PLAYERGUN)
                //.with(new PowerupComponent())
                .build();
    }
    */
    @Spawns("powerupSpawnPlayerGun")
    public Entity newPowerupSpawnPlayerGun(SpawnData data) {
        var e = new Rectangle(0,0,100,100);
        e.setFill(Color.DARKBLUE);

        return entityBuilder()
                .from(data)
                .type(PowerupType.SPAWNPLAYERGUN)
                .with(new ExpireCleanComponent(Duration.seconds(5)))
                .with(new PlayerGunComponent())
                .viewWithBBox(e)
                .build();
    }

    @Spawns("playerGunBullet")
    public Entity newPlayergun(SpawnData data) {
        Vec2 dir = Vec2.fromAngle(-90);
        Rectangle rectangle = new Rectangle(0, 0, 20, 20);
        rectangle.setFill(Color.YELLOW);

        return entityBuilder()
                .from(data)
                .type(PowerupType.PLAYERGUN)
                .viewWithBBox(rectangle)
                .with(new ProjectileComponent(dir.toPoint2D(), 500))
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }

    /**
     * Spawn Power Up Multi Ball - Multi Ball adds a another ball to the game.
     * @param data
     * @return
     */
    @Spawns("powerupSpawnMultiBall")
    public Entity newPowerupSpawnMutliBall(SpawnData data) {
        var e = new Rectangle(0,0,100,100);
        e.setFill(Color.DARKGREEN);
        return entityBuilder()
                .from(data)
                .type(PowerupType.MULTIBALL)
                .view(e)
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .with(new MultiBallComponent())
                .build();
    }


}
