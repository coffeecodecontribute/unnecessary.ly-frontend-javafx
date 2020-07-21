package ly.unnecessary.frontend.controller;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.UIController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

/**
 * Handles the game user interface (Hearts and score)
 */
public class UserInterfaceController implements UIController {
    private final GameScene gameScene;
    @FXML
    private Label labelScore;
    @FXML
    private double livesX;
    @FXML
    private double livesY;
    private final List<Texture> lives = new ArrayList<>(); // hols all textures for hearts

    public UserInterfaceController(GameScene gameScene) {
        this.gameScene = gameScene;
    }

    /**
     * Init User Interface
     */
    @Override
    public void init() {
        labelScore.setFont(getUIFactoryService().newFont(30)); // Set Font and size for labelScore
    }

    /**
     * Adds life to Ui Node
     */
    public void addLife() {
        int numLives = lives.size(); // gets amount of lives

        Texture texture = getAssetLoader().loadTexture("ui/heart.png", 32, 32); // adds life texture
        texture.setTranslateX(livesX + 64 * numLives); // calculates the correct X position
        texture.setTranslateY(livesY);

        lives.add(texture); // adds to arraylist
        gameScene.addUINode(texture); // adds to Ui node
    }

    /**
     * Removes Life from UI
     */
    public void removeLife() {
        Texture t = lives.get(lives.size() - 1);
        lives.remove(t);
        gameScene.removeUINode(t);
    }

    /**
     * Get labelScore Variable (is required to bind it to the UI node from
     * GameApplication)
     *
     * @return score label
     */
    public Label getLabelScore() {
        return labelScore;
    }
}
