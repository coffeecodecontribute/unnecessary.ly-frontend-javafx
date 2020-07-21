package ly.unnecessary.frontend.controller;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.set;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static ly.unnecessary.frontend.GameApplication.*;
import static ly.unnecessary.frontend.controller.LevelController.setLevel;

/**
 * Game Controller handles the logic for different game status.
 * <p>
 * To make the game more easier we have a global gameStatus value which can get the following values:
 * <ul>
 *     <li>-1 : lost</li>
 *     <li>0 : pregame</li>
 *     <li>1 : ingame</li>
 *     <li>2 : game win</li>
 * </ul>
 */
public class GameController {

    /**
     * Set the ball to player when the game is in pre game (gameStatus = 0)
     */
    public static void inPreGame() {
        ball.setX(player.getX() + player.getWidth() / 2 - ball.getWidth() / 2);
    }

    /**
     * Displays the game over screen when the game status is lost (gameStatus = -1)
     */
    public static void gameLost() {

        //creates content for popup
        VBox content = new VBox(
                getUIFactoryService().newText("Your score: " + geti("score"), 56),
                getAssetLoader().loadTexture("ui/sad_smiley.png", 64, 64)
        );

        content.setAlignment(Pos.CENTER); //Center the content

        //adds life back
        inc("playerLives", +playerLivesCount);
        for (int i = 0; i < playerLivesCount; i++) {
            uiController.addLife();
        }

        Button btnRestartLevel = getUIFactoryService().newButton("Restart Level"); // Restart level button
        btnRestartLevel.setPrefWidth(300);
        btnRestartLevel.setOnAction(e -> setLevel(geti("level")));  // setLevel to current Level -> "Restart"

        getDialogService().showBox("Game Over", content, btnRestartLevel); //creates pop up
    }

    /**
     * Displays the win screen (with next level button)  when the game status is won (gameStatus = 2)
     */
    public static void gameWon() {

        //creates content for popup
        VBox content = new VBox(
                getAssetLoader().loadTexture("ui/cup_icon.png", 64, 64),
                getUIFactoryService().newText("Your score: " + geti("score"), 40)

        );

        content.setAlignment(Pos.CENTER); //Center the content

        Button btnNextLevel = getUIFactoryService().newButton("Next Level"); //Next Level Button
        btnNextLevel.setPrefWidth(300);
        btnNextLevel.setOnAction(e -> setLevel(geti("level") + 1)); //current level + 1 -> "Next Level"

        Button btnRestartLevel = getUIFactoryService().newButton("Restart Level"); // Restart level button
        btnRestartLevel.setPrefWidth(300);
        btnRestartLevel.setOnAction(e -> setLevel(geti("level"))); // setLevel to current Level -> "Restart"

        getDialogService().showBox("Congratulations!", content, btnRestartLevel, btnNextLevel); //creates pop up
    }

    /**
     * Respawns the ball to the player. Changes game status to pre game. Ensures the player can move.
     */
    public static void respawnBall() {
        set("gameStatus", 0);
        set("freeze", false);
        ball = spawn("ball", player.getX() + player.getWidth() / 2 - ball.getWidth() / 2, ballSpawnPoint.getY());
    }
}
