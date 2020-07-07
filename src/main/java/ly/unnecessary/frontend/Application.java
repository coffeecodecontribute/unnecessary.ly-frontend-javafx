package ly.unnecessary.frontend;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.services.AssetLoaderService;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.UserAction;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import ly.unnecessary.frontend.components.BallComponent;
import ly.unnecessary.frontend.components.BrickComponent;
import ly.unnecessary.frontend.menu.MainMenu;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class Application extends GameApplication {

    //brick
    static final int collisionLogicSecurityPadding = 10;
    //app
    static int appWidth = 1920;
    static int appHeight = 1080;
    //ball
    static int ballSpeed = 8;
    static int ballRadius = 24;
    static Point2D ballSpawnPoint = new Point2D(appWidth / 2d - ballRadius / 2d, appHeight - 150);
    //player
    static int playerWidth = 300;
    static int playerHeight = 30;
    static int playerSpeed = 20;
    static double playerSpeedMultiplier = 1.5f;
    static Point2D playerSpawnPoint = new Point2D(appWidth / 2d - playerWidth / 2d, appHeight - 100);
    static int brickWidth = 128;
    static int brickHeight = 36;

    //level

    static int levelMargin = 36;
    static int levelRows = 19;

    /*
    static String level =
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000" +
                    "000000000000";
     */

    Entity ball, player;

    public static void main(String[] args) {
        launch(args);
    }

    public static int getPlayerSpeed() {
        return playerSpeed;
    }

    public static String getGameStatus(int gameStatus) {
        return gameStatus == 1 ? "ingame" : gameStatus == 0 ? "Game over" : gameStatus == 2 ? "Victory" : "Unknown Status";
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(appWidth);
        gameSettings.setHeight(appHeight);
        gameSettings.setTitle("Brick Breaker");
        gameSettings.setVersion("0.2");
        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }
        });
        gameSettings.setFullScreenAllowed(true);
        gameSettings.setFullScreenFromStart(false);
    }

    @Override
    protected void onPreInit() {
        getAssetLoader().loadSound("asset.wav");
        Sound loop_1 = getAssetLoader().loadSound("alpha/loop_1_arp.wav");
        Sound loop_2 = getAssetLoader().loadSound("alpha/loop_1_plomg.wav");
        getAssetLoader().loadSound("alpha/ball_collide_brick.wav");
        getAssetLoader().loadSound("alpha/ball_collide_player_1.wav");
        getAssetLoader().loadSound("alpha/ball_collide_wall_1_arp.wav");
        getAssetLoader().loadSound("alpha/power_up.wav");
        getAssetLoader().loadSound("alpha/ball_collide_player_hard.wav");
        getAssetLoader().loadSound("alpha/ball_collide_wall_2_arp.wav");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("ballSpeed", ballSpeed);
        vars.put("playerSpeed", playerSpeed);
        vars.put("ballRadius", ballRadius);
        vars.put("gameStatus", 0);
        vars.put("freeze", false);
        vars.put("level", 0);
        /*
            -1 : lost
            0 : pregame
            1 : ingame
            2 : game win
         */
    }

    @Override
    protected void initUI() {

        if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
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


        setLevel(0);
        //play("alpha/loop_1_arp.wav");
        loopBGM("music.mp3");

        entityBuilder()
                .type(EntityType.WALL)
                .collidable()
                .with(new IrremovableComponent())
                .buildScreenBoundsAndAttach(40);
    }

    @Override
    protected void onUpdate(double tpf) {
        mouseMovement();

        if (geti("gameStatus") == 0)
            inPreGame();

        if (getSettings().getApplicationMode() == ApplicationMode.RELEASE) {
            //Checks if ball is under the player
            byType(EntityType.BALL).forEach(entity -> {
                if (entity.getY() > getAppHeight() - 50)
                    entity.removeFromWorld();
            });
        }


        //Game is lost
        if (byType(EntityType.BALL).isEmpty()) {
            set("gameStatus", -1);
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
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onActionBegin() {
                if (geti("gameStatus") == 0) {
                    set("freeze", true);
                }
            }

            @Override
            protected void onActionEnd() {
                if (geti("gameStatus") == 0) {
                    ball.getComponent(BallComponent.class).release();
                    set("gameStatus", 1);
                    set("freeze", false);
                }
            }
        }, MouseButton.PRIMARY);


        //onKey(KeyCode.D, "Move Right", () -> { player.getComponent(PlayerComponent.class).moveRight(); });
        //onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).moveLeft());
        onKey(KeyCode.K, () -> {
            setLevel(1);
            //getDialogService().showMessageBox("You have completed demo!", getGameController()::exit);
        });

        onKey(KeyCode.L, () -> {
            if (byType(EntityType.BRICK).isEmpty())
                spawn("brick", getAppWidth() / 2 - 250, 100);
            spawn("point", byType(EntityType.BRICK).get(0).getX() - ball.getWidth() + collisionLogicSecurityPadding, 50);
            spawn("point", byType(EntityType.BRICK).get(0).getX() + byType(EntityType.BRICK).get(0).getWidth() - collisionLogicSecurityPadding, 50);
        });


    }

    private void setLevel(int levelId) {
        getGameWorld().getEntitiesCopy().forEach(e -> e.removeFromWorld());

        String level = "";
        set("gameStatus", 0);
        set("freeze", false);
        set("level", levelId);

        if(geti("level") == 0)
            level =
                        "000000000000000" +
                        "000000000000000" +
                        "000000000000000" +
                        "000010000010000" +
                        "000101000101000" +
                        "001000111000100" +
                        "001000000000100" +
                        "010022000220010" +
                        "010000000000010" +
                        "010000030000010" +
                        "001000030000100" +
                        "001000030000100" +
                        "000100000001000" +
                        "000010000010000" +
                        "000001000100000" +
                        "000000101000000" +
                        "000000010000000" +
                        "000000000000000" +
                        "000000000000000";
        else if(geti("level") == 1) {
            level =
                            "000000000000000" +
                            "000000000000000" +
                            "002121212121200" +
                            "002020202020200" +
                            "002020202020200" +
                            "002020202020200" +
                            "002020202020200" +
                            "001111111111100" +
                            "000111111111000" +
                            "000011111110000" +
                            "000001111100000" +
                            "000000111000000" +
                            "000000010000000" +
                            "000000020000000" +
                            "000000020000000" +
                            "001111121111100" +
                            "000000000000000" +
                            "000000000000000" +
                            "000000000000000";
        }

        spawn("background", 0, 0);
        player = spawn("player", playerSpawnPoint);
        ball = spawn("ball", ballSpawnPoint);


        int i = 0, x = 0, y = levelMargin;
        for (int row = 0; row < levelRows; row++) {
            for (int col = 0; col < 1920 / brickWidth; col++) {
                if (level.charAt(i) == '1')
                    spawn("brick", new SpawnData(x, y).put("color", Color.DARKGRAY));
                else if (level.charAt(i) == '2')
                    spawn("brick", new SpawnData(x, y).put("color", Color.RED));
                i++;
                x += brickWidth;
            }
            x = 0;
            y += brickHeight;
        }
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {

            if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
                spawn("point", brick.getPosition());
                spawn("point", ball.getPosition());
            }

            //play("asset.wav");

            play("alpha/ball_collide_brick.wav");
            brick.getComponent(BrickComponent.class).hitByBall();

            //ball.getComponent(BallComponent.class).collideBlock();


            Point2D velocity = ball.getObject("velocity");

            if (ball.getX() > brick.getX() - ball.getWidth() + collisionLogicSecurityPadding && ball.getX() < brick.getX() + brick.getWidth() - collisionLogicSecurityPadding) {
                ball.getComponent(BallComponent.class).collide(new Point2D(velocity.getX(), -velocity.getY()));
                System.out.println("TOP");
            } else {
                ball.getComponent(BallComponent.class).collide(new Point2D(-velocity.getX(), velocity.getY()));
                System.out.println("RIGHT");
            }

            if (FXGLMath.randomBoolean(0.5f)) {
                spawn("powerupdrop", brick.getPosition());
            }

            //System.out.println("Brick X: " + brick.getX() + " | Brick Y: " + brick.getY());
        });

        onCollisionCollectible(EntityType.PLAYER, EntityType.POWERUPDROP, powerupdrop -> {
            String powerUpType = powerupdrop.getString("type");
            PowerupType type;

            if (powerUpType == "MULTIBALL") {
                type = PowerupType.SPAWNMULTIBALL;
                if (byType(type).isEmpty()) {
                    System.out.println("MULTIBALL");
                    spawn("powerupSpawnMultiBall", 100, 0);
                }
            }
            else if (powerUpType == "PLAYERGUN") {
                type = PowerupType.SPAWNPLAYERGUN;
                if (byType(type).isEmpty()) {
                    System.out.println("PLAYERGUN");
                    spawn("powerupSpawnPlayerGun", 0, 0);
                }
            }
            /*
             * else if (randomNuber == 1) { type = PowerupType.SCOREBOMB; }
             */
        });

        onCollisionBegin(EntityType.BRICK, PowerupType.PLAYERGUN, (brick, bullet) -> {
            brick.removeFromWorld();
            bullet.removeFromWorld();
        });

        onCollisionBegin(EntityType.BALL, EntityType.PLAYER, (ball, player) -> {
            /*double lengthOfPaddleCollision = ball.getWidth() + player.getWidth();
            double collidePoint = ball.getX() - player.getX() + ball.getWidth();

            collidePoint = collidePoint / (lengthOfPaddleCollision / 2);

            double angle = collidePoint * Math.PI/3;

            System.out.println("lengthOfPaddleCollision: " + lengthOfPaddleCollision + " | collidePoint: " +  collidePoint + " | angle: " + angle);
            */
            play("alpha/ball_collide_wall_2_arp.wav");
            double collidePoint = ball.getX() - (player.getX() + player.getWidth() / 2);

            collidePoint = collidePoint / (player.getWidth() / 2);

            double angle = collidePoint * Math.PI / 3;

            //Developer
            if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
                spawn("point", ball.getPosition());
                spawn("point", player.getPosition());
            }


            //Ball collide logic
            Point2D velocity = ball.getObject("velocity");
            ball.getComponent(BallComponent.class).collide(new Point2D(ballSpeed * playerSpeedMultiplier * Math.sin(angle), -ballSpeed * playerSpeedMultiplier * Math.cos(angle)));
        });
        onCollisionEnd(EntityType.BALL, EntityType.PLAYER, (ball, player) -> {
            System.out.println("End Collision");
        });
    }

    /**
     * sets player'x position to the mouse'x
     */
    public void mouseMovement() {
        if (getb("freeze"))
            return;

        //gets the cursor and sets it to the middle of the player
        double mouseX = getInput().getMouseXWorld() - player.getWidth() / 2;

        //fixes issue where player is stucked before the max
        if (mouseX < 0)
            player.setX(0);
        else if (mouseX > getAppWidth() - player.getWidth())
            player.setX(getAppWidth() - player.getWidth());
        else
            player.setX(mouseX); // sets the player mouseX if it's in the allowed area
    }

    public void inPreGame() {
        ball.setX(player.getX() + player.getWidth() / 2 - ball.getWidth() / 2);
    }
}