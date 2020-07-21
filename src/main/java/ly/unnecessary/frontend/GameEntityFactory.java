package ly.unnecessary.frontend;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
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
import static ly.unnecessary.frontend.GameApplication.playerHeight;
import static ly.unnecessary.frontend.GameApplication.playerWidth;

/**
 * Game Entity Factory
 */
public class GameEntityFactory implements EntityFactory {

    /**
     * Spawns game ball with default texture.
     *
     * @param data Spawndata with x, y.
     * @return Entity of ball
     */
    @Spawns("ball")
    public Entity newBall(SpawnData data) {

        return entityBuilder().from(data).type(EntityType.BALL)
                .viewWithBBox(texture("game/balls/ball_default.png", geti("ballRadius"), geti("ballRadius")))
                .with("velocity", new Point2D(0, 0)).with(new BallComponent()).collidable().build();
    }

    /**
     * Spawns a brick with a given type
     *
     * @param data Spawndata with x, y and type.
     * @return Entity of brick
     */
    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        int type = data.get("type");
        Texture brick;

        switch (type) {
            case 1:
                brick = texture("game/bricks/white_brick.png", 128, 36);
                break;
            case 2:
                brick = texture("game/bricks/blue_brick.png", 128, 36);
                break;
            case 3:
                brick = texture("game/bricks/red_brick.png", 128, 36);
                break;
            default:
                brick = texture("game/bricks/white_brick.png", 128, 36);
        }

        return entityBuilder().from(data)
                .type(EntityType.BRICK)
                .viewWithBBox(brick)
                .with(new HealthIntComponent(type)) // type == to lives a brick has
                .with(new BrickComponent(type))
                .collidable()
                .build();
    }

    /**
     * Spawns a level brick (undestroyable brick).
     *
     * @param data Spawndata with x, y.
     * @return Entity of level Brick
     */
    @Spawns("levelBrick")
    public Entity newLevelBrick(SpawnData data) {

        return entityBuilder().from(data)
                .type(EntityType.LEVELBRICK)
                .viewWithBBox(texture("game/bricks/undestroyable_brick.png", 128, 36))
                .collidable()
                .build();
    }

    /**
     * Spawns an infected brick falling down (to player)
     *
     * @param data Spawn data with x, y.
     * @return Entity of infected brick
     */
    @Spawns("actionBrick")
    public Entity newActionBrick(SpawnData data) {
        play("beta/power_up.wav"); // Suprise sound

        Vec2 dir = Vec2.fromAngle(90);

        return entityBuilder().from(data).viewWithBBox(texture("game/bricks/green_brick.png", 128, 36))
                .type(EntityType.ACTIONBRICK)
                .with(new ProjectileComponent(dir.toPoint2D(), 500).allowRotation(false))
                .with(new OffscreenCleanComponent()).collidable().build();
    }

    /**
     * Spawns an broken brick fx when a brick is destroyed
     *
     * @param data Spawn data with x, y.
     * @return Entity of fx
     */
    @Spawns("brickBroken")
    public Entity newBrokenBrick(SpawnData data) {

        return entityBuilder().from(data)
                .view(texture("game/fx/brick_" + data.get("type") + "_fx.png", 864, 72).toAnimatedTexture(6, Duration.seconds(0.3)).play())
                .with(new ExpireCleanComponent(Duration.seconds(0.3)))
                .build();
    }

    /**
     * Spawns the player
     *
     * @param data Spawndata with x,y
     * @return the entity of the player
     */
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder().from(data).type(EntityType.PLAYER).viewWithBBox(texture("game/player.png", playerWidth, playerHeight))
                .with(new PlayerComponent()).collidable().build();
    }

    /**
     * Spawns animated background
     *
     * @param data Spawn data x, y
     * @return Entity of background
     */
    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        Rectangle background = new Rectangle(getAppWidth(), getAppHeight());
        background.setFill(Color.web("#222222"));
        return entityBuilder().from(data).view(texture("ui/backgrounds/background_basic_animated.png").toAnimatedTexture(7, Duration.seconds(7)).loop()).build();
    }

    /**
     * Spawns the boss with healthBar and logic component
     *
     * @param data Spawndata x, y
     * @return Entity of boss
     */
    @Spawns("boss")
    public Entity newBoss(SpawnData data) {
        Texture brick;
        brick = texture("game/boss/boss.png", 1728, 300).toAnimatedTexture(8, Duration.seconds(0.5)).loop();
        HealthIntComponent hp = new HealthIntComponent(10);

        var hpView = new ProgressBar(true); //create health bar
        hpView.setFill(Color.RED);
        hpView.setMaxValue(10);
        hpView.setWidth(200);
        hpView.setHeight(10);
        hpView.setTranslateY(-30);
        hpView.setTranslateX(0);
        hpView.setBackgroundFill(Color.BLACK);
        hpView.currentValueProperty().bind(hp.valueProperty()); //bind hp value to health bar

        return entityBuilder().from(data).type(EntityType.BRICK).viewWithBBox(brick).view(hpView).with(hp)
                .with(new BrickComponent()).with(new BossComponent()).collidable().build();
    }

    /**
     * Spawns boss bullet with dir
     *
     * @param data Spawndata x, y and dir (for bullet)
     * @return Entity of boss bullet
     */
    @Spawns("bossShotBullet")
    public Entity newBossShotBullet(SpawnData data) {
        Texture bossBullet = texture("game/boss/boss_bullet.png", 40, 60).toAnimatedTexture(5, Duration.seconds(0.5)).loop();
        bossBullet.setRotate(-90);

        play("beta/shot_" + FXGLMath.random(1, 3) + ".wav"); //plays cool shot sound
        Vec2 dir = data.get("dir");

        return entityBuilder().from(data).viewWithBBox(bossBullet)
                .type(EntityType.ACTIONBRICK)
                .with(new ProjectileComponent(dir.toPoint2D(), 800))
                .with(new OffscreenCleanComponent()).collidable().build();
    }

    /**
     * Spawn Level UI Notification (not included or used in RELEASE) wth animation
     *
     * @param data Spawn data x, y
     * @return Entity of ui
     */
    @Spawns("uiSpawnLevelInfo")
    public Entity newUiSpawnLevelInfo(SpawnData data) {
        System.out.println("uiSpawnLevelInfo");
        Text levelText = getUIFactoryService().newText("Level " + geti("level"), 24);

        Entity levelInfo = entityBuilder().from(data).view(levelText).with(new ExpireCleanComponent(Duration.seconds(3))).build();

        animationBuilder().interpolator(Interpolators.BOUNCE.EASE_OUT()).duration(Duration.seconds(2))
                .translate(levelInfo)
                .from(new Point2D(getAppWidth() / 2d - levelText.getLayoutBounds().getWidth() / 2, 0))
                .to(new Point2D(getAppWidth() / 2d - levelText.getLayoutBounds().getWidth() / 2, getAppHeight() / 4d))
                .buildAndPlay();

        return levelInfo;
    }

    /**
     * Spawn powerup drop with type
     *
     * @param data Spawndata y, x and type
     * @return Entity of power up drop
     */
    @Spawns("powerupdrop")
    public Entity newPowerupdrop(SpawnData data) {
        String dropIcon = data.get("texture");
        Vec2 dir = Vec2.fromAngle(90);

        play("beta/power_up.wav"); //surprise sound

        return entityBuilder().from(data).type(EntityType.POWERUPDROP).viewWithBBox(texture(dropIcon, 33, 33))
                .with(new ProjectileComponent(dir.toPoint2D(), 500).allowRotation(false))
                .with(new OffscreenCleanComponent()).collidable().with("type", data.get("type")).build();
    }

    /**
     * Spawn Player Gun Power Up - Player get's a shooting gun with which he can
     * shoot blocks
     *
     * @param data Spawndata y, x
     * @return Entity of PlayerGun Power Up
     */
    @Spawns("powerupSpawnPlayerGun")
    public Entity newPowerupSpawnPlayerGun(SpawnData data) {
        return entityBuilder().from(data).type(PowerupType.PLAYERGUN)
                .with(new ExpireCleanComponent(Duration.seconds(5))).with(new PlayerGunComponent())
                .view("game/powerups/playergun.png").build();
    }

    /**
     * Spawns Player Gun Bullet
     *
     * @param data Spawndata y, x
     * @return Entity of Player Gun Bullet
     */
    @Spawns("playerGunBullet")
    public Entity newPlayergun(SpawnData data) {
        Vec2 dir = Vec2.fromAngle(-90);

        return entityBuilder().from(data).type(PowerupType.PLAYERGUN_BULLET).viewWithBBox(texture("game/fx/bullet_border.png"))
                .with(new ProjectileComponent(dir.toPoint2D(), 500).allowRotation(false)).with(new OffscreenCleanComponent()).collidable()
                .build();
    }

    /**
     * Spawn Power Up Multi Ball - Multi Ball adds a another ball to the game.
     *
     * @param data Spawndata y, x
     * @return Entity of MultiBall Power Up
     */
    @Spawns("powerupSpawnMultiBall")
    public Entity newPowerupSpawnMutliBall(SpawnData data) {

        return entityBuilder().from(data).type(PowerupType.MULTIBALL).view("game/powerups/extraball.png")
                .with(new ExpireCleanComponent(Duration.seconds(10))).with(new MultiBallComponent()).build();
    }

    /**
     * Spawn Heart Power Up - Adds one live to player lives.
     *
     * @param data Spawndata y, x
     * @return Entity of hear Power up
     */
    @Spawns("powerupSpawnHeart")
    public Entity newPowerupSpawnHeart(SpawnData data) {

        return entityBuilder().from(data).type(PowerupType.HEART).view("game/powerups/heart.png")
                .with(new ExpireCleanComponent(Duration.seconds(10))).with(new HeartComponent()).build();
    }

    /**
     * Spawns Super Ball power up
     *
     * @param data Spawndata y, x
     * @return Entity of super ball power up
     */
    @Spawns("powerupSpawnSuperBall")
    public Entity newPowerupSpawnSuperBall(SpawnData data) {

        return entityBuilder().from(data).type(PowerupType.SUPERBALL).view("game/powerups/superball.png")
                .with(new ExpireCleanComponent(Duration.seconds(10))).with(new SuperBallComponent()).build();
    }


    // Developer

    /**
     * Spawns a small point for developer purpose (only 0.5 seconds visable)
     *
     * @param data Spawndata x,y
     * @return Entity of this point
     */
    @Spawns("point")
    public Entity newPoint(SpawnData data) {
        var pixel = new Rectangle(2, 2);
        pixel.setFill(Color.CYAN);
        return entityBuilder().from(data).view(pixel).with(new ExpireCleanComponent(Duration.seconds(0.5))).build();
    }

}
