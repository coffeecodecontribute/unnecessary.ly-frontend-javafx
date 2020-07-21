package ly.unnecessary.frontend.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.texture;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static ly.unnecessary.frontend.GameApplication.brickHeight;
import static ly.unnecessary.frontend.GameApplication.brickWidth;

/**
 * Main Menu Logic
 */
public class MainMenu extends FXGLMenu {
    AnimatedTexture background;
    public static Sound backgroundMusic;

    /**
     * Main Menu on added. Creates Buttons and applies them to the root node.
     */
    public MainMenu() {
        super(MenuType.MAIN_MENU);

        var buttonPlay = new MainButton("PLAY", this::fireNewGame);
        var buttonCredits = new MainButton("CREDITS", this::displayCredits);
        var buttonExit = new MainButton("EXIT", this::fireExit);

        var box = new VBox(5, buttonPlay, buttonCredits, buttonExit);
        box.setTranslateX(100);
        box.setTranslateY(getAppHeight() / 2d);

        backgroundMusic = getAssetLoader().loadSound("beta/game_loop_small.wav");
        getAudioPlayer().playSound(backgroundMusic);

        getMenuContentRoot().getChildren().addAll(box);
    }

    /**
     * Update for main menu scene
     *
     * @param tpf time per frame
     */
    @Override
    protected void onUpdate(double tpf) {
        background.onUpdate(0.05); //background requires manually updated since there is no component animated Texture in main menu
    }

    /**
     * Creates the animated menu background
     *
     * @param width  width of the background
     * @param height height of the background
     * @return node with texture
     */
    @Override
    protected Node createBackground(double width, double height) {
        return background = texture("ui/backgrounds/background_menu_animated.png").toAnimatedTexture(2, Duration.seconds(3.0)).loop();
    }

    /**
     * Display credits in dialog box.
     */
    public void displayCredits() {
        VBox content = new VBox(20,
                getUIFactoryService().newText("Credits", 56),
                getAssetLoader().loadTexture("game/bricks/white_brick.png", brickWidth, brickHeight),
                getAssetLoader().loadTexture("game/bricks/red_brick.png", brickWidth, brickHeight),
                getAssetLoader().loadTexture("game/bricks/blue_brick.png", brickWidth, brickHeight),
                getAssetLoader().loadTexture("game/bricks/green_brick.png", brickWidth, brickHeight)
        );

        content.setAlignment(Pos.TOP_CENTER);

        Button btnClose = getUIFactoryService().newButton("Close Credits");
        btnClose.setPrefWidth(300);

        getDialogService().showBox("", content, btnClose);
    }

    /**
     * Create view for profile name.
     *
     * @param s profile user name
     * @return UI object
     */
    @Override
    protected Node createProfileView(String s) {
        return new Text();
    }

    /**
     * Create view for the app title.
     *
     * @param s app title
     * @return UI object
     */
    @Override
    protected Node createTitleView(String s) {
        return new Text();
    }

    /**
     * Create view for version string.
     *
     * @param s version string
     * @return UI object
     */
    @Override
    protected Node createVersionView(String s) {
        return new Text();
    }

    @Override
    protected Button createActionButton(StringBinding stringBinding, Runnable runnable) {
        return new Button();
    }

    @Override
    protected Button createActionButton(String s, Runnable runnable) {
        return new Button();
    }

    /**
     * Create buttons with an action
     */
    public static class MainButton extends StackPane {
        public MainButton(String name, Runnable action) {
            var text = getUIFactoryService().newText(name, Color.WHITE, 72);

            setOnMouseClicked(e -> {
                action.run();
            });

            setAlignment(Pos.CENTER_LEFT);

            getChildren().addAll(text);
        }
    }
}
