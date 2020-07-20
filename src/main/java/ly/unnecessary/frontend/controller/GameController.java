package ly.unnecessary.frontend.controller;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.set;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static ly.unnecessary.frontend.GameApplication.*;
import static ly.unnecessary.frontend.controller.LevelController.setLevel;

public class GameController {


    public static void inPreGame() {
        ball.setX(player.getX() + player.getWidth() / 2 - ball.getWidth() / 2);
    }


    public static void gameLost() {
        VBox content = new VBox(
                getUIFactoryService().newText("Your score: " + geti("score"), 56),
                getAssetLoader().loadTexture("game/ball_small.png", 150, 150)
        );

        inc("playerLives", +3);
        uiController.addLife();
        uiController.addLife();
        uiController.addLife();

        Button btnRestartLevel = getUIFactoryService().newButton("Restart Level");
        btnRestartLevel.setPrefWidth(300);
        btnRestartLevel.setOnAction(e -> setLevel(geti("level")));

        getDialogService().showBox("Game Over", content, btnRestartLevel);
    }

    public static void gameWon() {
        VBox content = new VBox(
                getUIFactoryService().newText("Your score: " + geti("score"), 56),
                getAssetLoader().loadTexture("game/ball_small.png", 150, 150)
        );

        Button btnNextLevel = getUIFactoryService().newButton("Next Level");
        btnNextLevel.setPrefWidth(300);
        btnNextLevel.setOnAction(e -> setLevel(geti("level") + 1));

        Button btnRestartLevel = getUIFactoryService().newButton("Restart Level");
        btnRestartLevel.setPrefWidth(300);
        btnRestartLevel.setOnAction(e -> setLevel(geti("level")));

        getDialogService().showBox("Congratulations!", content, btnRestartLevel, btnNextLevel);
    }

    public static void respawnBall() {
        set("gameStatus", 0);
        set("freeze", false);
        ball = spawn("ball", player.getX() + player.getWidth() / 2 - ball.getWidth() / 2, ballSpawnPoint.getY());
    }
}
