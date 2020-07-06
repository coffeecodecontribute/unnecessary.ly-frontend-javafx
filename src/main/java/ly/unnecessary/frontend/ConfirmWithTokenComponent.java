package ly.unnecessary.frontend;

import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConfirmWithTokenComponent {
    private Function<String, Integer> onSubmit;
    private String invalidHeader;
    private String invalidDescription;
    private String tokenDescription;
    private String tokenFieldDescription;
    private String tokenSubmitDescription;
    private Boolean isPassword = false;

    public Node render() {
        var wrapper = new VBox();

        var invalidAlert = new Alert(AlertType.ERROR);
        invalidAlert.setHeaderText(this.invalidHeader);
        invalidAlert.setContentText(this.invalidDescription);

        var tokenDescription = new Label(this.tokenDescription);
        tokenDescription.setMaxWidth(300);
        tokenDescription.setPadding(new Insets(0, 0, 16, 0));

        var tokenField = this.isPassword ? new PasswordField() : new TextField();
        tokenField.setPromptText(this.tokenFieldDescription);
        tokenField.setStyle("-fx-background-radius: 16");
        tokenField.setMaxWidth(300);

        var tokenSubmitWrapper = new HBox();

        var tokenSubmitButton = new Button(this.tokenSubmitDescription);
        tokenSubmitButton.setStyle("-fx-base: royalblue; -fx-background-radius: 16");

        tokenSubmitWrapper.getChildren().add(tokenSubmitButton);
        tokenSubmitWrapper.setAlignment(Pos.CENTER_RIGHT);
        tokenSubmitWrapper.setMaxWidth(300);
        tokenSubmitWrapper.setSpacing(8);
        tokenSubmitWrapper.setPadding(new Insets(16, 0, 0, 0));

        Runnable submitHandler = () -> {
            var rv = this.onSubmit.apply(tokenField.getText());

            switch (rv) {
                case 0:
                    tokenField.clear();

                    return;
                case 1:
                    return;
                case 2:
                    invalidAlert.show();

                    return;
            }
        };

        tokenField.setOnAction(e -> submitHandler.run());
        tokenSubmitButton.setOnAction(e -> submitHandler.run());

        wrapper.getChildren().addAll(tokenDescription, tokenField, tokenSubmitWrapper);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(8));
        wrapper.setSpacing(8);
        wrapper.setStyle("-fx-font-family: 'Arial';");

        return wrapper;
    }

    public void setOnSubmit(Function<String, Integer> onSubmit) {
        this.onSubmit = onSubmit;
    }

    public void setInvalidHeader(String invalidHeader) {
        this.invalidHeader = invalidHeader;
    }

    public void setInvalidDescription(String invalidDescription) {
        this.invalidDescription = invalidDescription;
    }

    public void setTokenDescription(String tokenDescription) {
        this.tokenDescription = tokenDescription;
    }

    public void setTokenFieldDescription(String tokenFieldDescription) {
        this.tokenFieldDescription = tokenFieldDescription;
    }

    public void setTokenSubmitDescription(String tokenSubmitDescription) {
        this.tokenSubmitDescription = tokenSubmitDescription;
    }

    public void setIsPassword(Boolean isPassword) {
        this.isPassword = isPassword;
    }
}