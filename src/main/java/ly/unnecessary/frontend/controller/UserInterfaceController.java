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

public class UserInterfaceController implements UIController {

    @FXML
    private Label labelScore;

    @FXML
    private double livesX;

    @FXML
    private double livesY;

    private List<Texture> lives = new ArrayList<>();

    private GameScene gameScene;

    public UserInterfaceController(GameScene gameScene) {
        this.gameScene = gameScene;
    }
    @Override
    public void init() {
        labelScore.setFont(getUIFactoryService().newFont(18));

    }

    public void addLife() {
        int numLives = lives.size();

        Texture texture = getAssetLoader().loadTexture("ui/heart.png", 16, 16);
        texture.setTranslateX(livesX + 32 * numLives);
        texture.setTranslateY(livesY);

        lives.add(texture);
        gameScene.addUINode(texture);
    }

    public void removeLife() {
        Texture t = lives.get(lives.size() - 1);
        lives.remove(t);
        gameScene.removeUINode(t);
    }

    public Label getLabelScore() {
        return labelScore;
    }


}
