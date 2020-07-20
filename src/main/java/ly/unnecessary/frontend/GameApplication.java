package ly.unnecessary.frontend;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.ui.UI;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import ly.unnecessary.frontend.components.BallComponent;
import ly.unnecessary.frontend.components.BrickComponent;
import ly.unnecessary.frontend.controller.UserInterfaceController;
import ly.unnecessary.frontend.menu.MainMenu;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static ly.unnecessary.frontend.GamePlayerPhysic.calculateAngle;

public class GameApplication extends com.almasb.fxgl.app.GameApplication {

    // brick
    static final int collisionLogicSecurityPadding = 10;

    // app
    static int appWidth = 1920;
    static int appHeight = 1080;

    // ball
    static int ballSpeed = 8;
    static int ballRadius = 32;
    static Point2D ballSpawnPoint = new Point2D(appWidth / 2d - ballRadius / 2d, appHeight - 150);

    // player
    static int playerWidth = 320;
    static int playerHeight = 64;
    static int playerSpeed = 20;
    static double playerSpeedMultiplier = 1.5f;
    static Point2D playerSpawnPoint = new Point2D(appWidth / 2d - playerWidth / 2d, appHeight - 100);
    static int brickWidth = 128;
    static int brickHeight = 36;

    // level
    static int levelMargin = 36;
    static int levelRows = 19;

    // powerups
    static double chanceForDrop = 1.0f; // TODO: moved into brick componenet
    List level; // List with level data
    Entity ball, player;
    Sound loop_2;
    // User Interface
    private UserInterfaceController uiController;

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
        gameSettings.setFullScreenFromStart(false);
        // gameSettings.setFontUI("VT323-Regular.ttf");
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.5);
        getSettings().setGlobalSoundVolume(1);

        // Loading Levels
        level = getAssetLoader().loadText("level.txt");
        L("onPreInit: Level Data loaded!");

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
        L("onPreInit: Audio Files loaded!");

        // Loading Graphics
        L("onPreInit: Graphic Assets loaded!");
        getAssetLoader().loadTexture("ui/heart.png", 16, 16);
        getAssetLoader().loadTexture("game/ballBlue.png", ballRadius, ballRadius);
        getAssetLoader().loadTexture("game/ballGrey.png", ballRadius, ballRadius);
        getAssetLoader().loadTexture("game/paddleBlu.png", ballRadius, ballRadius);

        getAssetLoader().loadText("game/powerups/extraball.png");
        getAssetLoader().loadText("game/powerups/heart.png");
        getAssetLoader().loadText("game/powerups/playergun.png");
        getAssetLoader().loadText("game/powerups/superball.png");

        L("onPreInit: Done! Enjoy and have fun!");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("ballSpeed", ballSpeed);
        vars.put("playerSpeed", playerSpeed);
        vars.put("ballRadius", ballRadius);
        vars.put("level", 0);

        /*
         * -1 : lost 0 : pregame 1 : ingame 2 : game win
         */
        // level vars
        vars.put("gameStatus", 0);
        vars.put("freeze", false);
        vars.put("playerLives", 3);
        vars.put("playerStars", 0);
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());


        loopBGM("beta/game_loop_all_01.mp3"); //TODO: MUSIC

        setLevel(0);
        // loop_2.getAudio$fxgl_media().setVolume(0.1);
        // loop_2.getAudio$fxgl_media().play();

        // loopBGM("music.mp3");

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
            }

            if (geti("playerLives") < 1) {
                set("gameStatus", -1);

                play("beta/game_over.wav"); //TODO: MUSIC

                set("gameStatusReadable", getGameStatus(geti("gameStatus")));
            } else {
                respawnBall();
            }
        }

        // Game is won
        if (byType(EntityType.BRICK).isEmpty() && geti("gameStatus") != 2) {

            play("beta/game_win.wav"); //TODO: MUSIC

            set("gameStatus", 2);
            set("gameStatusReadable", getGameStatus(geti("gameStatus")));
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
            getDialogService().showInputBoxWithCancel("Jump to Level: ", levelId -> Integer.parseInt(levelId) > 0,
                    levelId -> {
                        setLevel(Integer.parseInt(levelId));
                    });
        });

        onKey(KeyCode.K, () -> {
            if (byType(EntityType.BRICK).isEmpty())
                spawn("brick", getAppWidth() / 2 - 250, 100);
            spawn("point", byType(EntityType.BRICK).get(0).getX() - ball.getWidth() + collisionLogicSecurityPadding,
                    50);
            spawn("point", byType(EntityType.BRICK).get(0).getX() + byType(EntityType.BRICK).get(0).getWidth()
                    - collisionLogicSecurityPadding, 50);
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

    private void setLevel(int levelId) {
        if (levelId != 10) {
            if (levelId >= level.size() || levelId < 0)
                return;
        }

        getGameWorld().getEntitiesCopy().forEach(e -> e.removeFromWorld());

        String currentLevel = "";
        set("gameStatus", 0);
        set("freeze", false);
        set("level", levelId);

        if (levelId == 10) {
            currentLevel = generateRandomLevel();
        } else
            currentLevel = level.get(levelId).toString();

        spawn("background", 0, 0);
        player = spawn("player", playerSpawnPoint);
        ball = spawn("ball", ballSpawnPoint);

        // Ui
        spawn("uiSpawnLevelInfo");

        if (currentLevel.equals("boss")) {
            spawn("boss", 100, 100);
            return;
        }

        int i = 0, x = 0, y = levelMargin;
        for (int row = 0; row < levelRows; row++) {
            for (int col = 0; col < 1920 / brickWidth; col++) {
                if (currentLevel.charAt(i) == '1') // 1 life
                    spawn("brick", new SpawnData(x, y).put("type", 1));
                else if (currentLevel.charAt(i) == '2') // 2 life
                    spawn("brick", new SpawnData(x, y).put("type", 2));
                else if (currentLevel.charAt(i) == '3') // 3 life
                    spawn("brick", new SpawnData(x, y).put("type", 3));
                else if (currentLevel.charAt(i) == 'X') // not destroyable
                    spawn("levelBrick", x, y);
                i++;
                x += brickWidth;
            }
            x = 0;
            y += brickHeight;
        }
    }

    public String generateRandomLevel() {
        String result = "";
        for (int i = 0; i < 285; i++) {
            result += FXGLMath.randomBoolean() ? "1" : "0";
        }
        return result;
    }

    public void respawnBall() {
        set("gameStatus", 0);
        set("freeze", false);
        ball = spawn("ball", player.getX() + player.getWidth() / 2 - ball.getWidth() / 2, ballSpawnPoint.getY());
    }

    /**
     * sets player'x position to the mouse'x
     */
    public void mouseMovement() {
        if (getb("freeze"))
            return;

        // gets the cursor and sets it to the middle of the player
        double mouseX = getInput().getMouseXWorld() - player.getWidth() / 2;

        // fixes issue where player is stucked before the max
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