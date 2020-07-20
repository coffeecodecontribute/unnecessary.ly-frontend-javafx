package ly.unnecessary.frontend.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MainMenu extends FXGLMenu {
    public MainMenu() {
        super(MenuType.MAIN_MENU);

        var buttonPlay = new MainButton("PLAY", this::fireNewGame);
        var buttonCredits = new MainButton("CREDITS", this::createContentCredits);
        var buttonExit = new MainButton("EXIT", this::fireExit);

        var box = new VBox(5, buttonPlay, buttonCredits, buttonExit);
        box.setTranslateX(100);
        box.setTranslateY(getAppHeight() / 2);

        getMenuContentRoot().getChildren().addAll(box);
    }

    @Override
    protected Button createActionButton(StringBinding stringBinding, Runnable runnable) {
        return new Button();
    }

    @Override
    protected Button createActionButton(String s, Runnable runnable) {
        return new Button();
    }

    @Override
    protected Node createBackground(double width, double height) {
        return texture("ui/backgrounds/background_menu_animated.png").toAnimatedTexture(2, Duration.seconds(0.3)).loop();
    }

    @Override
    protected Node createProfileView(String s) {
        return new Text();
    }

    @Override
    protected Node createTitleView(String s) {
        return new Text();
    }

    @Override
    protected Node createVersionView(String s) {
        return new Text();
    }

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
