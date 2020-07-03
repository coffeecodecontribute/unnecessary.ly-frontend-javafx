package ly.unnecessary.frontend;

import com.almasb.fxgl.app.ApplicationMode;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class Application extends GameApplication {



    Entity ball, player;

    static int ballSpeed = 18;
    static int ballRadius = 24;

    static int playerSpeed = 20;




    Entity[] bricks = new Entity[42];

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1920);
        gameSettings.setHeight(1080);
        gameSettings.setTitle("Brick Breaker");
        gameSettings.setVersion("0.2");
        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("ballSpeed", ballSpeed);
        vars.put("playerSpeed", playerSpeed);
        vars.put("gameStatus", 1);
    }

    @Override
    protected void initUI() {

        if(getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
            addText("ballSpeed:", getAppWidth() - 250, 50);
            addText("playerSpeed:", getAppWidth() - 250, 70);
            addText("gameStatus:", getAppWidth() - 250, 90);

            addVarText("ballSpeed", getAppWidth() - 100, 50);
            addVarText("playerSpeed", getAppWidth() - 100, 70);
            addVarText("gameStatus", getAppWidth() - 100, 90);
        }
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());

        int ballSpawnPointX = 0;
        int ballSpawnPointY = getAppHeight() - 300;

        spawn("background", 0, 0);
        player = spawn("player", ballSpawnPointX, ballSpawnPointY + 75);
        ball = spawn("ball", new SpawnData(ballSpawnPointX + 100, ballSpawnPointY)
                .put("ballSpeed", ballSpeed)
                .put("ballRadius", ballRadius)
        );

        int m = 0;
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 7; j++) {
                bricks[m++] = spawn("brick", 80 + i * 150, 60 + j * 50);
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
        mouseMovement();
        Point2D velocity = ball.getObject("velocity");
        ball.translate(velocity);

        if (ball.getY() < 0 || ball.getY() + ball.getWidth() > getAppHeight()) {
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }

        if (ball.getX() < 0 || ball.getX() + ball.getHeight() > getAppWidth()) {
            ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));
        }

        //Game is lost
        if (ball.getY() > getAppHeight() - 50) {
            set("gameStatus", 0);
            set("gameStatusReadable", getGameStatus(geti("gameStatus")));
        }

        //Game is won
        if (byType(EntityType.BRICK).isEmpty()) {
            set("gameStatus", 2);
            set("gameStatusReadable", getGameStatus(geti("gameStatus")));
        }
    }
    @Override
    protected void initInput() {
        //onKey(KeyCode.D, "Move Right", () -> { player.getComponent(PlayerComponent.class).moveRight(); });
        //onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).moveLeft());
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {

            if(getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
                spawn("point", brick.getPosition());
                spawn("point", ball.getPosition());
            }

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
            if(getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
                spawn("point", ball.getPosition());
                spawn("point", player.getPosition());
            }


            //Ball collide logic
            Point2D velocity = ball.getObject("velocity");
            ball.setProperty("velocity", new Point2D(ballSpeed * Math.sin(angle), -ballSpeed * Math.cos(angle)));
        });
        onCollisionEnd(EntityType.BALL, EntityType.PLAYER, (ball, player) -> {
            System.out.println("End Collision");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * sets player'x position to the mouse'x
     */
    public void mouseMovement() {
        //gets the cursor and sets it to the middle of the player
        double mouseX = getInput().getMouseXWorld() - player.getWidth() / 2;

        //fixes issue where player is stucked before the max
        if(mouseX < 0)
            player.setX(0);
        else if(mouseX > getAppWidth() - player.getWidth())
            player.setX(getAppWidth() - player.getWidth());
        else
            player.setX(mouseX); // sets the player mouseX if it's in the allowed area
    }

    public static int getPlayerSpeed() {
        return playerSpeed;
    }

    public static String getGameStatus(int gameStatus) {
        return gameStatus == 1 ? "ingame" : gameStatus == 0 ? "Game over" : gameStatus == 2 ? "Victory" : "Unknown Status";
    }
}