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

        int ballSpawnPointX = 0;
        int ballSpawnPointY = getAppHeight() - 300;

        spawn("background", 0, 0);
        player = spawn("player", ballSpawnPointX, ballSpawnPointY + 75);
        ball = spawn("ball", ballSpawnPointX + 100, ballSpawnPointY);

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
        Point2D velocity = ball.getObject("velocity");
        ball.translate(velocity);

        if (ball.getY() < 0 || ball.getY() + ball.getWidth() > getAppHeight()) {
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
            System.out.println("Wall collide");
        }

        if (ball.getX() < 0 || ball.getX() + ball.getHeight() > getAppWidth()) {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
            System.out.println("Wall collide");
        }
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.D, "Move Right", () -> player.getComponent(PlayerComponent.class).moveRight());
        onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).moveLeft());
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {
            brick.removeFromWorld();
            ball.getComponent(BallComponent.class).collideBlock();
            Point2D velocity = ball.getObject("velocity");
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
            //System.out.println("Brick X: " + brick.getX() + " | Brick Y: " + brick.getY());
            System.out.println(Arrays.toString(bricks));
        });

        onCollisionBegin(EntityType.BALL, EntityType.PLAYER, (ball, player) -> {
            /*double lengthOfPaddleCollision = ball.getWidth() + player.getWidth();
            double collidePoint = ball.getX() - player.getX() + ball.getWidth();

            collidePoint = collidePoint / (lengthOfPaddleCollision / 2);

            double angle = collidePoint * Math.PI/3;

            System.out.println("lengthOfPaddleCollision: " + lengthOfPaddleCollision + " | collidePoint: " +  collidePoint + " | angle: " + angle);
            */

            double collidePoint = ball.getX() - (player.getX() + player.getWidth()/2);

            collidePoint = collidePoint / (player.getWidth() / 2);

            double angle = collidePoint * Math.PI/3;

            //Developer
            spawn("point", ball.getPosition());
            spawn("point", player.getPosition());


            //Ball collide logic
            Point2D velocity = ball.getObject("velocity");
            ball.setProperty("velocity", new Point2D(15 * Math.sin(angle), -15 * Math.cos(angle)));
        });
        onCollisionEnd(EntityType.BALL, EntityType.PLAYER, (ball, player) -> {
            System.out.println("End Collision");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}