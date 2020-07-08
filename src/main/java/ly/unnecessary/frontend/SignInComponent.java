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

/**
 * Sign in/up/password reset component
 */
public class SignInComponent {
    private Function<SignInInfo, Integer> onSignIn;
    private Function<SignInInfo, Integer> onSignUp;
    private Function<SignInInfo, Integer> onPasswordReset;

    private Runnable handleSignInActivate;
    private Runnable handleSignUpActivate;
    private Runnable handlePasswordResetActivate;

    private static int FORM_WIDTH = 300;

    /**
     * Set sign in handler
     * 
     * @param onSignIn
     */
    public void setOnSignIn(Function<SignInInfo, Integer> onSignIn) {
        this.onSignIn = onSignIn;
    }

    /**
     * Set sign up handler
     * 
     * @param onSignUp
     */
    public void setOnSignUp(Function<SignInInfo, Integer> onSignUp) {
        this.onSignUp = onSignUp;
    }

    /**
     * Set password reset handler
     * 
     * @param onPasswordReset
     */
    public void setOnPasswordReset(Function<SignInInfo, Integer> onPasswordReset) {
        this.onPasswordReset = onPasswordReset;
    }

    /**
     * Utility class with sign in/up/password reset info
     */
    public class SignInInfo {
        private String apiUrl;

        private String displayName;

        private String email;

        private String password;

        private Boolean isSignUp = false;

        private Boolean isPasswordReset = false;

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

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean getIsSignUp() {
            return isSignUp;
        }

        public void setIsSignUp(Boolean isSignUp) {
            this.isSignUp = isSignUp;
        }

        public Boolean getIsPasswordReset() {
            return isPasswordReset;
        }

        public void setIsPasswordReset(Boolean isPasswordReset) {
            this.isPasswordReset = isPasswordReset;
        }
    }

    /**
     * Render component
     * 
     * @return Node
     */
    public Node render() {
        var wrapper = new VBox();

        var header = new Label(Constants.APP_NAME);
        header.setStyle("-fx-font-size: 42; -fx-font-weight: bold");
        header.setPadding(new Insets(0, 0, 24, 0));

        var apiUrlField = new TextField(Constants.DEFAULT_API_URL);
        apiUrlField.setPromptText("API URL");
        apiUrlField.setMaxWidth(FORM_WIDTH);
        apiUrlField.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);

        var displayNameField = new TextField();
        displayNameField.setPromptText("Display name");
        displayNameField.setMaxWidth(FORM_WIDTH);
        displayNameField.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);
        displayNameField.setVisible(false);
        displayNameField.setManaged(false);

        var emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(FORM_WIDTH);
        emailField.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);

        var passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(FORM_WIDTH);
        passwordField.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);

        var actionWrapper = new HBox();

        var signInButton = new Button("Sign in");
        signInButton.setStyle("-fx-base: royalblue; -fx-background-radius: 16");

        var signUpButton = new Button("Sign up");
        signUpButton.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);

        var passwordResetButton = new Button("Reset password");
        passwordResetButton.setStyle(Constants.DEFAULT_BACKGROUND_RADIUS);

        actionWrapper.getChildren().addAll(passwordResetButton, signUpButton, signInButton);
        actionWrapper.setAlignment(Pos.CENTER_RIGHT);
        actionWrapper.setMaxWidth(FORM_WIDTH);
        actionWrapper.setSpacing(Constants.DEFAULT_SPACING);
        actionWrapper.setPadding(new Insets(16, 0, 0, 0));

        var signInInfo = new SignInInfo();

        Runnable submitHandler = () -> {
            var invalidSignInInfoAlert = new Alert(AlertType.ERROR);
            invalidSignInInfoAlert.setHeaderText("Invalid credentials");
            invalidSignInInfoAlert.setContentText("Invalid API URL, email or password.");

            signInInfo.setApiUrl(apiUrlField.getText());
            signInInfo.setDisplayName(displayNameField.getText());
            signInInfo.setEmail(emailField.getText());
            signInInfo.setPassword(passwordField.getText());

            final int rv;
            if (signInInfo.getIsSignUp()) {
                rv = this.onSignUp.apply(signInInfo);
            } else if (!signInInfo.getIsPasswordReset()) {
                rv = this.onSignIn.apply(signInInfo);
            } else {
                rv = this.onPasswordReset.apply(signInInfo);
            }

            switch (rv) {
                case 0:
                    apiUrlField.clear();
                    displayNameField.clear();
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

        this.handleSignInActivate = () -> {
            signInInfo.setIsSignUp(false);
            signInInfo.setIsPasswordReset(false);

            displayNameField.setVisible(false);
            displayNameField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);

            signInButton.setOnAction((e) -> submitHandler.run());
            signUpButton.setOnAction((e) -> this.handleSignUpActivate.run());
            passwordResetButton.setOnAction((e) -> this.handlePasswordResetActivate.run());
        };

        this.handleSignUpActivate = () -> {
            signInInfo.setIsSignUp(true);
            signInInfo.setIsPasswordReset(false);

            displayNameField.setVisible(true);
            displayNameField.setManaged(true);

            passwordField.setVisible(true);
            passwordField.setManaged(true);

            signInButton.setOnAction((e) -> this.handleSignInActivate.run());
            signUpButton.setOnAction((e) -> submitHandler.run());
            passwordResetButton.setOnAction((e) -> this.handlePasswordResetActivate.run());
        };

        this.handlePasswordResetActivate = () -> {
            signInInfo.setIsSignUp(false);
            signInInfo.setIsPasswordReset(true);

            displayNameField.setVisible(false);
            displayNameField.setManaged(false);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            signInButton.setOnAction((e) -> this.handleSignInActivate.run());
            signUpButton.setOnAction((e) -> this.handleSignUpActivate.run());
            passwordResetButton.setOnAction((e) -> submitHandler.run());
        };

        apiUrlField.setOnAction((e) -> submitHandler.run());
        emailField.setOnAction((e) -> submitHandler.run());
        displayNameField.setOnAction((e) -> submitHandler.run());
        passwordField.setOnAction((e) -> submitHandler.run());
        signUpButton.setOnAction((e) -> this.handleSignUpActivate.run());
        signInButton.setOnAction((e) -> submitHandler.run());
        passwordResetButton.setOnAction((e) -> this.handlePasswordResetActivate.run());

        wrapper.getChildren().addAll(header, apiUrlField, displayNameField, emailField, passwordField, actionWrapper);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(Constants.DEFAULT_INSETS);
        wrapper.setSpacing(Constants.DEFAULT_SPACING);
        wrapper.setStyle("-fx-font-family: 'Arial';");

        return wrapper;
    }
}