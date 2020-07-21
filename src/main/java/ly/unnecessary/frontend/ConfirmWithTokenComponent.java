package ly.unnecessary.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Function;

/**
 * Confirmation component
 */
public class ConfirmWithTokenComponent {
    private Function<String, Integer> onSubmit;
    private String invalidHeader;
    private String invalidDescription;
    private String tokenDescription;
    private String tokenFieldDescription;
    private String tokenSubmitDescription;
    private Boolean isPassword = false;

    /**
     * Set submit handler
     *
     * @param onSubmit
     */
    public void setOnSubmit(Function<String, Integer> onSubmit) {
        this.onSubmit = onSubmit;
    }

    /**
     * Set header for invalid state
     *
     * @param invalidHeader
     */
    public void setInvalidHeader(String invalidHeader) {
        this.invalidHeader = invalidHeader;
    }

    /**
     * Set description for invalid state
     *
     * @param invalidDescription
     */
    public void setInvalidDescription(String invalidDescription) {
        this.invalidDescription = invalidDescription;
    }

    /**
     * Set token description
     *
     * @param tokenDescription
     */
    public void setTokenDescription(String tokenDescription) {
        this.tokenDescription = tokenDescription;
    }

    /**
     * Set token prompt text description
     *
     * @param tokenFieldDescription
     */
    public void setTokenFieldDescription(String tokenFieldDescription) {
        this.tokenFieldDescription = tokenFieldDescription;
    }

    /**
     * Set token submit description
     *
     * @param tokenSubmitDescription
     */
    public void setTokenSubmitDescription(String tokenSubmitDescription) {
        this.tokenSubmitDescription = tokenSubmitDescription;
    }

    /**
     * Enable/disable password field
     *
     * @param isPassword
     */
    public void setIsPassword(Boolean isPassword) {
        this.isPassword = isPassword;
    }

    /**
     * Render component
     *
     * @return Node
     */
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
        tokenField.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);
        tokenField.setMaxWidth(300);

        var tokenSubmitWrapper = new HBox();

        var tokenSubmitButton = new Button(this.tokenSubmitDescription);
        tokenSubmitButton.setStyle("-fx-base: royalblue; -fx-background-radius: 16");

        tokenSubmitWrapper.getChildren().add(tokenSubmitButton);
        tokenSubmitWrapper.setAlignment(Pos.CENTER_RIGHT);
        tokenSubmitWrapper.setMaxWidth(300);
        tokenSubmitWrapper.setSpacing(Constants.DEFAULT_SPACING);
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
        wrapper.setPadding(Constants.DEFAULT_INSETS);
        wrapper.setSpacing(Constants.DEFAULT_SPACING);
        wrapper.setStyle("-fx-font-family: 'Arial';");

        return wrapper;
    }
}