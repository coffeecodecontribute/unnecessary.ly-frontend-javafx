package ly.unnecessary.frontend.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.binding.StringBinding;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class MainMenu extends FXGLMenu {

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        var start = new MainButton("New Game", this::fireNewGame);
        start.setTranslateX(getAppWidth() / 2 - 250 / 2);
        start.setTranslateY(getAppHeight() / 2 - 60 / 2);

        getMenuContentRoot().getChildren().add(start);
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
        return new Rectangle(width, height, Color.BLACK);
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
            var bg = new Rectangle(250, 60, Color.WHITE);
            var text = FXGL.getUIFactoryService().newText(name, Color.BLACK, 22);

            setOnMouseClicked(e -> action.run());

            getChildren().addAll(bg, text);
        }
    }
}
