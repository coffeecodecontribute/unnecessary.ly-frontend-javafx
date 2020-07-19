package ly.unnecessary.frontend;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ly.unnecessary.frontend.components.BallComponent;
import ly.unnecessary.frontend.components.BrickComponent;
import ly.unnecessary.frontend.components.PlayerComponent;
import ly.unnecessary.frontend.components.boss.BossComponent;
import ly.unnecessary.frontend.components.powerups.HeartComponent;
import ly.unnecessary.frontend.components.powerups.MultiBallComponent;
import ly.unnecessary.frontend.components.powerups.PlayerGunComponent;
import ly.unnecessary.frontend.components.powerups.SuperBallComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameEntityFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data) {

        Rectangle rectangle = new Rectangle(0, 0, geti("ballRadius"), geti("ballRadius"));
        // Circle rectangle = new Circle((int) data.get("ballRadius"), (int)
        // data.get("ballRadius"), (int) data.get("ballRadius"));
        rectangle.setFill(Color.RED);

        return entityBuilder().type(EntityType.BALL).from(data)
                .viewWithBBox(texture("game/ball_small.png", geti("ballRadius"), geti("ballRadius")))
                .with("velocity", new Point2D(0, 0)).with(new BallComponent()).collidable().build();
    }

    @Spawns("levelBrick")
    public Entity newLevelBrick(SpawnData data) {
        Rectangle brick = new Rectangle(0, 0, 128, 36);
        brick.setFill(Color.BLACK);

        return entityBuilder()
                .type(EntityType.LEVELBRICK)
                .from(data)
                .viewWithBBox(brick)
                .collidable()
                .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        Rectangle brick = new Rectangle(0, 0, 128, 36);
        int life = data.get("type");
        switch(life) {
            case 1:
                brick.setFill(Color.WHITE);
                break;
            case 2:
                brick.setFill(Color.DARKGRAY);
                break;
            case 3:
                brick.setFill(Color.GRAY);
                break;
            default:
                brick.setFill(Color.WHITE);
        }

        /*
        Texture brick;
        if (data.get("color") == Color.RED)
            brick = texture("game/brickRed.png", 128, 36);
        else
            brick = texture("game/brickGray.png", 128, 36);
         */

        return entityBuilder()
                .type(EntityType.BRICK)
                .from(data).viewWithBBox(brick)
                .with(new HealthIntComponent(life))
                .with(new BrickComponent())
                .collidable()
                .build();
    }

    @Spawns("actionBrick")
    public Entity newActionBrick(SpawnData data) {
        Rectangle brick = new Rectangle(0, 0, 128, 36);
        brick.setFill(Color.DARKGRAY);
        Vec2 dir = Vec2.fromAngle(90);
        return entityBuilder().from(data).viewWithBBox(texture("game/brickGreen.png", 128, 36))
                .with(new ProjectileComponent(dir.toPoint2D(), 500).allowRotation(false))
                .with(new OffscreenCleanComponent()).collidable().build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        var e = new Rectangle(0, 0, 300, 30);
        e.setFill(Color.RED);
        return entityBuilder().type(EntityType.PLAYER).from(data).viewWithBBox(texture("game/playerPX.png", 320, 64))
                .with(new PlayerComponent()).collidable().build();
    }

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        Rectangle background = new Rectangle(getAppWidth(), getAppHeight());
        background.setFill(Color.web("#000000"));
        return entityBuilder().from(data).view(texture("game/bg_example.jpg")).build();
    }

    @Spawns("boss")
    public Entity newBoss(SpawnData data) {
        Texture brick;
        brick = texture("game/boss/boss.png", 1728, 300).toAnimatedTexture(8, Duration.seconds(0.5)).loop();
        HealthIntComponent hp = new HealthIntComponent(10);

        var hpView = new ProgressBar(true);
        hpView.setFill(Color.RED);
        hpView.setMaxValue(10);
        hpView.setWidth(200);
        hpView.setHeight(10);
        hpView.setTranslateY(-30);
        hpView.setTranslateX(0);
        hpView.setBackgroundFill(Color.BLACK);
        hpView.currentValueProperty().bind(hp.valueProperty());

        Entity e = entityBuilder().type(EntityType.BRICK).from(data).viewWithBBox(brick).view(hpView).with(hp)
                .with(new BrickComponent()).with(new BossComponent()).collidable().build();

        return e;
    }

    @Spawns("bossShotBullet")
    public Entity newBossShotBullet(SpawnData data) {
        Rectangle brick = new Rectangle(0, 0, 10, 40);
        Texture bossBullet = texture("game/boss/boss_bullet.png", 40, 60).toAnimatedTexture(5, Duration.seconds(0.5)).loop();
        bossBullet.setRotate(-90);

        brick.setFill(Color.YELLOW);
        Vec2 dir = data.get("dir");
        return entityBuilder().from(data).viewWithBBox(bossBullet)
                .with(new ProjectileComponent(dir.toPoint2D(), 800))
                .with(new OffscreenCleanComponent()).collidable().build();
    }

    // User Interface
    @Spawns("uiSpawnLevelInfo")
    public Entity newUiSpawnLevelInfo(SpawnData data) {
        System.out.println("uiSpawnLevelInfo");
        Text levelText = getUIFactoryService().newText("Level " + geti("level"), 24);

        Entity levelInfo = entityBuilder().view(levelText).with(new ExpireCleanComponent(Duration.seconds(3))).build();

        animationBuilder().interpolator(Interpolators.BOUNCE.EASE_OUT()).duration(Duration.seconds(2))
                .translate(levelInfo)
                .from(new Point2D(getAppWidth() / 2 - levelText.getLayoutBounds().getWidth() / 2, 0))
                .to(new Point2D(getAppWidth() / 2 - levelText.getLayoutBounds().getWidth() / 2, getAppHeight() / 4))
                .buildAndPlay();

        return levelInfo;
    }

    // Developer

    @Spawns("point")
    public Entity newPoint(SpawnData data) {
        var pixel = new Rectangle(2, 2);
        pixel.setFill(Color.CYAN);
        return entityBuilder().from(data).view(pixel).with(new ExpireCleanComponent(Duration.seconds(0.5))).build();
    }

    @Spawns("powerupdrop")
    public Entity newPowerupdrop(SpawnData data) {
        String dropIcon = data.get("texture");
        System.out.println(dropIcon);
        Vec2 dir = Vec2.fromAngle(90);
        return entityBuilder().type(EntityType.POWERUPDROP).from(data).viewWithBBox(texture(dropIcon, 33, 33))
                .with(new ProjectileComponent(dir.toPoint2D(), 500).allowRotation(false))
                .with(new OffscreenCleanComponent()).collidable().with("type", data.get("type")).build();
    }

    /*
     * @Spawns("powerup") public Entity newPowerup(SpawnData data) {
     *
     * return entityBuilder() .from(data) .type(PowerupType.PLAYERGUN) //.with(new
     * PowerupComponent()) .build(); }
     */

    /**
     * Spawn Player Gun Power Up - Player get's a shooting gun with which he can
     * shoot blocks
     *
     * @param data
     * @return
     */
    @Spawns("powerupSpawnPlayerGun")
    public Entity newPowerupSpawnPlayerGun(SpawnData data) {
        return entityBuilder().from(data).type(PowerupType.PLAYERGUN)
                .with(new ExpireCleanComponent(Duration.seconds(5))).with(new PlayerGunComponent())
                .view("game/powerups/playergun.png").build();
    }

    @Spawns("playerGunBullet")
    public Entity newPlayergun(SpawnData data) {
        Vec2 dir = Vec2.fromAngle(-90);

        // TODO: Bullet graphic
        Rectangle rectangle = new Rectangle(0, 0, 20, 20);
        rectangle.setFill(Color.YELLOW);

        return entityBuilder().from(data).type(PowerupType.PLAYERGUN_BULLET).viewWithBBox(rectangle)
                .with(new ProjectileComponent(dir.toPoint2D(), 500)).with(new OffscreenCleanComponent()).collidable()
                .build();
    }

    /**
     * Spawn Power Up Multi Ball - Multi Ball adds a another ball to the game.
     *
     * @param data
     * @return
     */
    @Spawns("powerupSpawnMultiBall")
    public Entity newPowerupSpawnMutliBall(SpawnData data) {

        return entityBuilder().from(data).type(PowerupType.MULTIBALL).view("game/powerups/extraball.png")
                .with(new ExpireCleanComponent(Duration.seconds(10))).with(new MultiBallComponent()).build();
    }

    /**
     * Spawn Heart Power Up - Adds one live to player lives.
     *
     * @param data
     * @return
     */
    @Spawns("powerupSpawnHeart")
    public Entity newPowerupSpawnHeart(SpawnData data) {

        return entityBuilder().from(data).type(PowerupType.HEART).view("game/powerups/heart.png")
                .with(new ExpireCleanComponent(Duration.seconds(10))).with(new HeartComponent()).build();
    }

    /**
     * Super Ball
     *
     * @param data
     * @return
     */
    @Spawns("powerupSpawnSuperBall")
    public Entity newPowerupSpawnSuperBall(SpawnData data) {

        return entityBuilder().from(data).type(PowerupType.SUPERBALL).view("game/powerups/superball.png")
                .with(new ExpireCleanComponent(Duration.seconds(10))).with(new SuperBallComponent()).build();
    }

}
