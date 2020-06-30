package ly.unnecessary.frontend;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.Arrays;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class Application extends GameApplication {

    Entity ball, player;

    Entity[] bricks = new Entity[42];

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1920);
        gameSettings.setHeight(1080);
        gameSettings.setTitle("Brick Breaker");
        gameSettings.setVersion("0.1");
    }


    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());

        int ballSpawnPointX = getAppWidth() / 2;
        int ballSpawnPointY = getAppHeight() - 150;

        spawn("background", 0, 0);
        player = spawn("player", ballSpawnPointX, ballSpawnPointY + 75);
        ball = spawn("ball", ballSpawnPointX, ballSpawnPointY);

        int m = 0;
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 7; j++) {
                bricks[m++] = spawn("brick", 40 + i * 150, 30 + j * 50);
            }
        }

        entityBuilder()
                .type(EntityType.WALL)
                .collidable()
                .with(new IrremovableComponent())
                .buildScreenBoundsAndAttach(40);
    }


    @Override
    protected void onUpdate(double tpf) {

    }

    @Override
    protected void initInput() {
        onKey(KeyCode.D, () -> player.getComponent(PlayerComponent.class).moveRight());
        onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).moveLeft());
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {
            brick.removeFromWorld();
            ball.getComponent(BallComponent.class).collideBlock();
            //System.out.println("Brick X: " + brick.getX() + " | Brick Y: " + brick.getY());
            System.out.println(Arrays.toString(bricks));
        });

        onCollisionBegin(EntityType.BALL, EntityType.PLAYER, (ball, player) -> {
            ball.getComponent(BallComponent.class).collide(1);
        });
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {

            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("BOT")) {
                    ball.getComponent(BallComponent.class).collide(1);
                }
                if (boxB.getName().equals("TOP")) {
                    ball.getComponent(BallComponent.class).collide(1);
                }
                if (boxB.getName().equals("RIGHT")) {
                    ball.getComponent(BallComponent.class).collide(0);
                }
                if (boxB.getName().equals("LEFT")) {
                    ball.getComponent(BallComponent.class).collide(0);
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}