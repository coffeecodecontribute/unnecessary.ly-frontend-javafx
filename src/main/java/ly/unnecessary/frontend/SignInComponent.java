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
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class SignInComponent {
    private Function<SignInInfo, Integer> onSignIn;

    public void setOnSignIn(Function<SignInInfo, Integer> onSignIn) {
        this.onSignIn = onSignIn;
    }

    public class SignInInfo {
        private String apiUrl;

        private String email;

        private String password;

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public Node render() {
        var wrapper = new VBox();

        var header = new Label("unnecessary.ly");
        header.setStyle("-fx-font-size: 42; -fx-font-weight: bold");
        header.setPadding(new Insets(0, 0, 24, 0));

        var apiUrlField = new TextField();
        apiUrlField.setPromptText("API URL");
        apiUrlField.setMaxWidth(300);

        var emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);

        var passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        var signInButtonWrapper = new HBox();

        var signInButton = new Button("Sign in");
        signInButton.setStyle("-fx-base: royalblue");

        signInButtonWrapper.getChildren().add(signInButton);
        signInButtonWrapper.setAlignment(Pos.CENTER_RIGHT);
        signInButtonWrapper.setMaxWidth(300);

        Runnable submitHandler = () -> {
            var invalidSignInInfoAlert = new Alert(AlertType.ERROR);
            invalidSignInInfoAlert.setHeaderText("Invalid sign in info");
            invalidSignInInfoAlert.setContentText("Invalid API URL, email or password.");

            var signInInfo = new SignInInfo();

            signInInfo.setApiUrl(apiUrlField.getText());
            signInInfo.setEmail(emailField.getText());
            signInInfo.setPassword(passwordField.getText());

            var rv = this.onSignIn.apply(signInInfo);

            switch (rv) {
                case 0:
                    apiUrlField.clear();
                    emailField.clear();
                    passwordField.clear();

                    return;
                case 1:
                    return;
                case 2:
                    invalidSignInInfoAlert.show();

                    return;
            }
        };

        apiUrlField.setOnAction((e) -> submitHandler.run());
        emailField.setOnAction((e) -> submitHandler.run());
        passwordField.setOnAction((e) -> submitHandler.run());
        signInButton.setOnAction((e) -> submitHandler.run());

        wrapper.getChildren().addAll(header, apiUrlField, emailField, passwordField, signInButtonWrapper);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(8));
        wrapper.setSpacing(8);
        wrapper.setStyle("-fx-font-family: 'Arial';");

        return wrapper;
    }
}