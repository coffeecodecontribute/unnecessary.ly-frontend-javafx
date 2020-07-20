package ly.unnecessary.frontend;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.ui.UI;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import ly.unnecessary.frontend.components.BallComponent;
import ly.unnecessary.frontend.components.BrickComponent;
import ly.unnecessary.frontend.controller.MouseMovementController;
import ly.unnecessary.frontend.controller.UserInterfaceController;
import ly.unnecessary.frontend.menu.MainMenu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static ly.unnecessary.frontend.controller.GameController.*;
import static ly.unnecessary.frontend.controller.GamePlayerPhysicController.calculateAngle;
import static ly.unnecessary.frontend.controller.LevelController.*;
import static ly.unnecessary.frontend.controller.MouseMovementController.mouseMovement;

public class GameApplication extends com.almasb.fxgl.app.GameApplication {

    // brick
    public static final int collisionLogicSecurityPadding = 10;

    // app
    public static int appWidth = 1920;
    public static int appHeight = 1080;

    // ball
    public static int ballSpeed = 8;
    public static int ballRadius = 32;
    public static Point2D ballSpawnPoint = new Point2D(appWidth / 2d - ballRadius / 2d, appHeight - 150);

    // player
    public static int playerWidth = 320;
    public static int playerHeight = 64;
    public static int playerSpeed = 20;
    public static double playerSpeedMultiplier = 1.5f;
    public static Point2D playerSpawnPoint = new Point2D(appWidth / 2d - playerWidth / 2d, appHeight - 100);
    public static int brickWidth = 128;
    public static int brickHeight = 36;
    public static int playerLivesCount = 3;

    // level
    public static int levelMargin = 36;
    public static int levelRows = 19;

    // powerups
    static double chanceForDrop = 1.0f; // TODO: moved into brick componenet
    public static List level; // List with level data
    public static Entity ball, player;


    Sound loop_2;
    // User Interface
    public static UserInterfaceController uiController;

    public static void main(String[] args) {
        launch(args);
    }

    public static int getPlayerSpeed() {
        return playerSpeed;
    }

    public static String getGameStatus(int gameStatus) {
        return gameStatus == 1 ? "ingame"
                : gameStatus == 0 ? "Game over" : gameStatus == 2 ? "Victory" : "Unknown Status";
    }

    /**
     * Used to print out updates to the console while the game is running.
     *
     * @param msg Message developer want to print
     */
    public static void L(String msg) {
        System.out.println(msg);
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(appWidth);
        gameSettings.setHeight(appHeight);
        gameSettings.setTitle("Brick Breaker");
        gameSettings.setVersion("0.3");
        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }
        });
        gameSettings.setFullScreenAllowed(true);
        gameSettings.setIntroEnabled(false);
        gameSettings.setFullScreenFromStart(false);
        gameSettings.getCredits().addAll(Arrays.asList(
                "Music by Marcel Cabanis",
                "Copyright 2020"
        ));
        gameSettings.setFontUI("basic.ttf");
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.5);
        getSettings().setGlobalSoundVolume(1);

        // Loading Levels
        level = getAssetLoader().loadText("level.txt");

        // Loading Audio
        getAssetLoader().loadSound("asset.wav");
        Sound loop_1 = getAssetLoader().loadSound("alpha/loop_1_arp.wav");
        loop_2 = getAssetLoader().loadSound("alpha/loop_1_plomg.wav");

        getAssetLoader().loadSound("alpha/ball_collide_brick.wav");
        getAssetLoader().loadSound("alpha/ball_collide_player_1.wav");
        getAssetLoader().loadSound("alpha/ball_collide_wall_1_arp.wav");
        getAssetLoader().loadSound("alpha/power_up.wav");
        getAssetLoader().loadSound("alpha/ball_collide_player_hard.wav");
        getAssetLoader().loadSound("alpha/ball_collide_wall_2_arp.wav");

        // Loading Graphics
        getAssetLoader().loadTexture("ui/heart.png", 16, 16);
        getAssetLoader().loadTexture("game/ballBlue.png", ballRadius, ballRadius);
        getAssetLoader().loadTexture("game/ballGrey.png", ballRadius, ballRadius);
        getAssetLoader().loadTexture("game/paddleBlu.png", ballRadius, ballRadius);

        getAssetLoader().loadText("game/powerups/extraball.png");
        getAssetLoader().loadText("game/powerups/heart.png");
        getAssetLoader().loadText("game/powerups/playergun.png");
        getAssetLoader().loadText("game/powerups/superball.png");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("ballSpeed", ballSpeed); //ballSpeed
        vars.put("playerSpeed", playerSpeed); //playerSpeed (if player uses only keyboard

        vars.put("ballRadius", ballRadius); //ball radius

        vars.put("level", 0); //current Level

        vars.put("gameStatus", 0); //gameStatus -1 : lost 0 : pregame 1 : ingame 2 : game win
        vars.put("freeze", false); //game Freeze?
        vars.put("playerLives", playerLivesCount); //player lives
        vars.put("playerStars", 0); //player stars
        vars.put("score", 0); //current score (resets with level change)
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());


        loopBGM("beta/game_loop_all_01.mp3"); //TODO: MUSIC

        setLevel(0);

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

        // Checks if ball is under the player
        byType(EntityType.BALL).forEach(entity -> {
            if (entity.getY() > getAppHeight() - 50) {
                entity.removeFromWorld();
            }
        });

        // Game is lost
        if (byType(EntityType.BALL).isEmpty()) {
            if (geti("gameStatus") != -1) {
                inc("playerLives", -1);

                play("beta/player_live_loss.wav"); //TODO: MUSIC

                getGameScene().getViewport().shakeTranslational(1.5);
                uiController.removeLife();

                removeAllPowerUps();

                inc("score", -500);
            }

            if (geti("playerLives") < 1 && geti("gameStatus") != -1) {
                set("gameStatus", -1);

                play("beta/game_over.wav"); //TODO: MUSIC

                set("gameStatusReadable", getGameStatus(geti("gameStatus")));

                gameLost();
            } else if (geti("gameStatus") != -1) {
                respawnBall();
            }
        }

        // Game is won
        if (byType(EntityType.BRICK).isEmpty() && geti("gameStatus") != 2) {

            play("beta/game_win.wav"); //TODO: MUSIC

            set("gameStatus", 2);
            set("gameStatusReadable", getGameStatus(geti("gameStatus")));

            gameWon();
        }
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {

            play("beta/brick_collide_" + FXGLMath.random(1, 4) + ".wav"); //TODO: MUSIC

            if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
                spawn("point", brick.getPosition());
                spawn("point", ball.getPosition());
            }
            brick.getComponent(BrickComponent.class).hitByBall();

            inc("score", 100);


            if (byType(PowerupType.SUPERBALL).isEmpty()) { // Only collide if SuperBall is not active
                Point2D velocity = ball.getObject("velocity");

                if (ball.getX() > brick.getX() - ball.getWidth() + collisionLogicSecurityPadding
                        && ball.getX() < brick.getX() + brick.getWidth() - collisionLogicSecurityPadding) {
                    ball.getComponent(BallComponent.class).collide(new Point2D(velocity.getX(), -velocity.getY()));
                } else {
                    ball.getComponent(BallComponent.class).collide(new Point2D(-velocity.getX(), velocity.getY()));
                }
            }
        });

        onCollisionBegin(EntityType.BALL, EntityType.LEVELBRICK, (ball, levelBrick) -> {

            play("beta/wall_collide_" + FXGLMath.random(1, 6) + ".wav"); //TODO: MUSIC

            Point2D velocity = ball.getObject("velocity");

            if (ball.getX() > levelBrick.getX() - ball.getWidth() + collisionLogicSecurityPadding
                    && ball.getX() < levelBrick.getX() + levelBrick.getWidth() - collisionLogicSecurityPadding) {
                ball.getComponent(BallComponent.class).collide(new Point2D(velocity.getX(), -velocity.getY()));
            } else {
                ball.getComponent(BallComponent.class).collide(new Point2D(-velocity.getX(), velocity.getY()));
            }
        });

        onCollisionCollectible(EntityType.PLAYER, EntityType.POWERUPDROP, powerupdrop -> {

            play("beta/power_up_collect.wav"); //TODO: MUSIC

            String powerUpType = powerupdrop.getString("type");
            PowerupType type;

            if (powerUpType.equals("MULTIBALL")) {
                type = PowerupType.MULTIBALL;
                if (byType(type).isEmpty()) {
                    System.out.println(type);
                    spawn("powerupSpawnMultiBall", 30, 30);
                }
            } else if (powerUpType.equals("PLAYERGUN")) {
                type = PowerupType.PLAYERGUN;
                if (byType(type).isEmpty()) {
                    System.out.println(type);
                    spawn("powerupSpawnPlayerGun", 33 * 2, 30);
                }
            } else if (powerUpType.equals("HEART")) {
                type = PowerupType.HEART;
                if (byType(type).isEmpty()) {
                    System.out.println(type);
                    spawn("powerupSpawnHeart", 33 * 3, 30);
                    uiController.addLife(); // Require to update the UI from Application
                }
            } else if (powerUpType.equals("SUPERBALL")) {
                type = PowerupType.SUPERBALL;
                if (byType(type).isEmpty()) {
                    System.out.println(type);
                    spawn("powerupSpawnSuperBall", 33 * 4, 30);
                }
            }
        });

        onCollisionBegin(EntityType.ACTIONBRICK, EntityType.PLAYER, (actionBrick, player) -> {
            actionBrick.removeFromWorld();
            byType(EntityType.BALL).get(FXGLMath.random(0, byType(EntityType.BALL).size() - 1)).removeFromWorld(); //Removes one random ball as negative impact

            play("beta/player_live_loss.wav"); //TODO: MUSIC

            getGameScene().getViewport().shakeTranslational(1.5);
        });


        onCollisionBegin(EntityType.ACTIONBRICK, PowerupType.PLAYERGUN_BULLET, (actionBrick, bullet) -> {
            play("beta/brick_collide_" + FXGLMath.random(1, 4) + ".wav"); //TODO: MUSIC

            actionBrick.removeFromWorld();
            bullet.removeFromWorld();
        });

        onCollisionBegin(EntityType.BRICK, PowerupType.PLAYERGUN_BULLET, (brick, bullet) -> {
            play("beta/brick_collide_" + FXGLMath.random(1, 4) + ".wav"); //TODO: MUSIC

            brick.getComponent(BrickComponent.class).hitByBall();
            bullet.removeFromWorld();
        });

        onCollisionBegin(EntityType.BALL, EntityType.PLAYER, (ball, player) -> {

            play("beta/player_collide_" + FXGLMath.random(1, 6) + ".wav"); //TODO: MUSIC

            // Display the collide points for testing
            if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
                spawn("point", ball.getPosition());
                spawn("point", player.getPosition());
            }

            double ballCorrectX = ball.getX() + ball.getWidth() / 2; // Takes the middle of the ball as ball x
            double angle = calculateAngle(ballCorrectX, player.getX(), player.getWidth());

            ball.getComponent(BallComponent.class).collide(new Point2D(ballSpeed * playerSpeedMultiplier * Math.sin(angle), -ballSpeed * playerSpeedMultiplier * Math.cos(angle)));
        });
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

        // onKey(KeyCode.D, "Move Right", () -> {
        // player.getComponent(PlayerComponent.class).moveRight(); });
        // onKey(KeyCode.A, () ->
        // player.getComponent(PlayerComponent.class).moveLeft());
        onKey(KeyCode.L, () -> {
            getDialogService().showInputBox("Jump to Level: ",
                    levelId -> {
                        setLevel(Integer.parseInt(levelId));
                        getNotificationService().pushNotification("Level set to " + levelId);
                    });
        });

        onKey(KeyCode.S, () -> {
            getDialogService().showInputBox("Spawn Something\n0 - Remove all Power Ups (inc. drops)\n1 - Heart\n2 - Multi Ball\n3 - Player Gun\n4 - Super Ball\n5 - Infected Brick\n6 - Spawn Balls", type -> {
                int selection = Integer.parseInt(type);
                PowerupType powerUp = null;
                String notification = "";
                switch (selection) {
                    case 0:
                        removeAllPowerUps();
                        notification = "All Power-Ups removed";
                        break;
                    case 1:
                        powerUp = PowerupType.HEART;
                        notification = "Heart spawned";
                        break;
                    case 2:
                        powerUp = PowerupType.MULTIBALL;
                        notification = "Multi Ball spawned";
                        break;
                    case 3:
                        powerUp = PowerupType.PLAYERGUN;
                        notification = "Player Gun spawned";
                        break;
                    case 4:
                        powerUp = PowerupType.SUPERBALL;
                        notification = "Super Ball spawned";
                        break;
                    case 5:
                        spawn("actionBrick", getAppWidth() / 2, 50);
                        notification = "Infected Brick spawned.";
                        break;
                    case 6:
                        powerUp = PowerupType.MULTIBALL;
                        getDialogService().showInputBox("Number of balls: ", number -> {
                            int numberInt = Integer.parseInt(number);
                            for (int i = 0; i < numberInt; i++) {
                                Entity multiBall = FXGL.spawn("ball", byType(EntityType.BALL).get(0).getPosition());
                                multiBall.setProperty("velocity", new Point2D(geti("ballSpeed") * i, geti("ballSpeed")));
                            }
                        });
                        notification = "Balls spawned.";
                        break;
                    default:
                        notification = "Upps. This command is unknown.";
                }
                if (powerUp != null)
                    spawn("powerupdrop", new SpawnData(getAppWidth() / 2, getAppHeight() / 2)
                            .put("type", powerUp.getType()).put("texture", powerUp.getTextureString()));

                getNotificationService().pushNotification(notification);
            });
        });

    }

    @Override
    protected void initUI() {
        uiController = new UserInterfaceController(getGameScene());

        UI ui = getAssetLoader().loadUI("game.fxml", uiController);

        uiController.getLabelScore().textProperty().bind(getip("score").asString("Score: %d"));

        IntStream.range(0, geti("playerLives")).forEach(i -> uiController.addLife());

        if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
            addText("ballSpeed:", getAppWidth() - 250, 50);
            addText("playerSpeed:", getAppWidth() - 250, 70);
            addText("gameStatus:", getAppWidth() - 250, 90);
            addText("playerLives:", getAppWidth() - 250, 110);

            addVarText("ballSpeed", getAppWidth() - 100, 50);
            addVarText("playerSpeed", getAppWidth() - 100, 70);
            addVarText("gameStatus", getAppWidth() - 100, 90);
            addVarText("playerLives", getAppWidth() - 100, 110);
        }

        getGameScene().addUI(ui);
    }

    public void removeAllPowerUps() {
        //Removes all powerup drops
        if (!byType(EntityType.POWERUPDROP).isEmpty()) {
            byType(EntityType.POWERUPDROP).forEach(Entity::removeFromWorld);
        }

        //Removes all active powerups
        PowerupType[] powerUps = PowerupType.values();
        for (int i = 0; i < powerUps.length; i++) {
            if (!byType(powerUps[i]).isEmpty()) {
                byType(powerUps[i]).forEach(Entity::removeFromWorld);
            }
        }
    }
}