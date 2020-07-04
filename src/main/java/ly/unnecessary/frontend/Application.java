package ly.unnecessary.frontend;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import ly.unnecessary.frontend.components.BallComponent;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class Application extends GameApplication {


    Entity ball, player;

    static int ballSpeed = 8;
    static int ballRadius = 24;

    static final int collisionLogicSecurityPadding = 10;

    static int playerSpeed = 20;

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1920);
        gameSettings.setHeight(1080);
        gameSettings.setTitle("Brick Breaker");
        gameSettings.setVersion("0.2");
        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void onPreInit() {
        getAssetLoader().loadSound("asset.wav");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("ballSpeed", ballSpeed);
        vars.put("playerSpeed", playerSpeed);
        vars.put("ballRadius", ballRadius);
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

        int ballSpawnPointX = getAppWidth() - 200;
        int ballSpawnPointY = getAppHeight() - 300;

        spawn("background", 0, 0);
        player = spawn("player", ballSpawnPointX, ballSpawnPointY + 75);
        ball = spawn("ball", new SpawnData(ballSpawnPointX + 100, ballSpawnPointY));

        int m = 0;
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 15; j++) {
                spawn("brick", 80 + i * 150, 60 + j * 36);
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

        //Checks if ball is under the player
        byType(EntityType.BALL).forEach(entity -> {
            if(entity.getY() > getAppHeight() - 50)
                entity.removeFromWorld();
        });

        //Game is lost
        if (byType(EntityType.BALL).isEmpty()) {
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
        onKey(KeyCode.L, () -> {
            if(byType(EntityType.BRICK).isEmpty())
                spawn("brick", getAppWidth()/2 - 250, 100);
                spawn("point", byType(EntityType.BRICK).get(0).getX() - ball.getWidth() + collisionLogicSecurityPadding, 50);
                spawn("point", byType(EntityType.BRICK).get(0).getX() + byType(EntityType.BRICK).get(0).getWidth() - collisionLogicSecurityPadding, 50);
        });
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {

            if(getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
                spawn("point", brick.getPosition());
                spawn("point", ball.getPosition());
            }

            //play("asset.wav");

            brick.removeFromWorld();

            //ball.getComponent(BallComponent.class).collideBlock();


            Point2D velocity = ball.getObject("velocity");

            if(ball.getX() > brick.getX() - ball.getWidth() + collisionLogicSecurityPadding && ball.getX() < brick.getX() + brick.getWidth() - collisionLogicSecurityPadding) {
                ball.getComponent(BallComponent.class).collide(new Point2D(velocity.getX(), -velocity.getY()));
                System.out.println("TOP");
            }
            else {
                ball.getComponent(BallComponent.class).collide(new Point2D(-velocity.getX(), velocity.getY()));
                System.out.println("RIGHT");
            }

            //System.out.println("Brick X: " + brick.getX() + " | Brick Y: " + brick.getY());
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
            ball.getComponent(BallComponent.class).collide(new Point2D(ballSpeed * 1.5 * Math.sin(angle), -ballSpeed * 1.5 * Math.cos(angle)));
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