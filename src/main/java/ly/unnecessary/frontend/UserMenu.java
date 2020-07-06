package ly.unnecessary.frontend;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class UserMenu {
    private Runnable onSignOut;

    public void setOnSignOut(Runnable onSignOut) {
        this.onSignOut = onSignOut;
    }

    public Node render() {
        var wrapper = new VBox();

        var signOutButton = new Button("Sign out");
        signOutButton.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);
        signOutButton.setOnAction((e) -> onSignOut.run());

        wrapper.getChildren().addAll(signOutButton);
        wrapper.setPadding(Constants.DEFAULT_INSETS);
        wrapper.setAlignment(Pos.CENTER);

        return wrapper;
    }
}