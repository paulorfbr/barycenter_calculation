package com.logistics.ui.screens;

import com.logistics.ui.theme.DesignTokens;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.function.BiConsumer;

/**
 * SCR-013: Login Screen
 *
 * Full-window centered login card. No sidebar or top bar present.
 * The background uses a subtle gradient.
 *
 * Design spec:
 *   - Background: gradient from primary-900 (bottom-left) to primary-700 (top-right)
 *   - Card: neutral-000, 420px wide, radius-4, elevation Level 3
 *   - Logo area: centered above the form
 *   - On error: shake animation + red error message under password field
 *   - Fade-in on load
 *
 * Usage:
 *   LoginScreen login = new LoginScreen();
 *   login.setOnLogin((email, password) -> authService.authenticate(email, password));
 *   primaryStage.setScene(new Scene(login));
 */
public class LoginScreen extends StackPane {

    private final TextField emailField       = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final CheckBox rememberMe        = new CheckBox("Remember me");
    private final Button loginButton         = new Button("Sign In");
    private final Label errorLabel           = new Label();

    private BiConsumer<String, String> onLogin;

    public LoginScreen() {
        // Background
        setStyle(
                "-fx-background-color: linear-gradient("
                + "from 0%% 100%% to 100%% 0%%, "
                + DesignTokens.CSS_PRIMARY_900 + " 0%%, "
                + DesignTokens.CSS_PRIMARY_700 + " 100%%"
                + ");"
        );
        setAlignment(Pos.CENTER);

        // Card
        VBox card = buildCard();
        card.setMaxWidth(420);

        getChildren().add(card);

        // Fade in the card on load
        card.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // -------------------------------------------------------------------------
    // Card builder
    // -------------------------------------------------------------------------

    private VBox buildCard() {
        VBox card = new VBox(DesignTokens.SPACE_5);
        card.getStyleClass().add("modal-dialog");
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(DesignTokens.SPACE_8));
        card.setStyle(
                "-fx-background-color: " + DesignTokens.CSS_NEUTRAL_000 + ";"
                + "-fx-background-radius: 12px;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 24, 0, 0, 8);"
        );

        // Logo / branding
        Label appName = new Label("Define Logistics");
        appName.setStyle(
                "-fx-font-size: 22px; -fx-font-weight: bold;"
                + "-fx-text-fill: " + DesignTokens.CSS_PRIMARY_800 + ";"
        );

        Label subtitle = new Label("Logistics Management Platform");
        subtitle.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + ";"
        );

        VBox branding = new VBox(4, appName, subtitle);
        branding.setAlignment(Pos.CENTER_LEFT);
        branding.setPadding(new Insets(0, 0, DesignTokens.SPACE_3, 0));

        // Divider
        Region divider = new Region();
        divider.setStyle("-fx-background-color: " + DesignTokens.CSS_NEUTRAL_300 + ";");
        divider.setMinHeight(1);
        divider.setMaxHeight(1);

        // Sign In heading
        Label heading = new Label("Sign In");
        heading.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold;"
                + "-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_900 + ";"
        );

        // Email field
        Label emailLabel = new Label("Email address");
        emailLabel.getStyleClass().add("form-label");
        emailField.getStyleClass().add("form-input");
        emailField.setPromptText("you@company.com");
        emailField.setMaxWidth(Double.MAX_VALUE);

        // Password field
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("form-label");
        passwordField.getStyleClass().add("form-input");
        passwordField.setPromptText("••••••••");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setOnAction(e -> handleLogin());

        // Error message (hidden until login fails)
        errorLabel.setStyle(
                "-fx-text-fill: " + DesignTokens.CSS_DANGER_600 + ";"
                + "-fx-font-size: 12px;"
        );
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Remember me + forgot password row
        Hyperlink forgotLink = new Hyperlink("Forgot password?");
        forgotLink.setStyle("-fx-text-fill: " + DesignTokens.CSS_PRIMARY_600 + "; -fx-font-size: 13px;");
        Region rememberSpacer = new Region();
        HBox.setHgrow(rememberSpacer, Priority.ALWAYS);
        HBox rememberRow = new HBox(rememberSpacer, rememberMe, new Region(), forgotLink);
        rememberRow.setSpacing(DesignTokens.SPACE_3);
        rememberRow.setAlignment(Pos.CENTER_LEFT);

        // Login button
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setMinHeight(44);
        loginButton.setOnAction(e -> handleLogin());

        // Footer
        Label contactText = new Label("Don't have an account?");
        contactText.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_500 + "; -fx-font-size: 13px;");
        Hyperlink contactLink = new Hyperlink("Contact Admin");
        contactLink.setStyle("-fx-text-fill: " + DesignTokens.CSS_PRIMARY_600 + "; -fx-font-size: 13px;");
        HBox footer = new HBox(6, contactText, contactLink);
        footer.setAlignment(Pos.CENTER);

        // Version
        Label version = new Label("Version 1.0.0");
        version.setStyle("-fx-text-fill: " + DesignTokens.CSS_NEUTRAL_300 + "; -fx-font-size: 11px;");
        version.setAlignment(Pos.CENTER);
        version.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(
                branding, divider, heading,
                emailLabel, emailField,
                passwordLabel, passwordField,
                errorLabel,
                rememberRow,
                loginButton,
                footer,
                version
        );

        return card;
    }

    // -------------------------------------------------------------------------
    // Login handling
    // -------------------------------------------------------------------------

    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        clearError();

        if (email.isEmpty()) {
            showError("Email address is required.");
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        if (password.isEmpty()) {
            showError("Password is required.");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Signing in...");

        if (onLogin != null) {
            onLogin.accept(email, password);
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Registers the callback invoked when the user submits credentials.
     * The host must call either loginSuccess() or loginFailure() after
     * authentication completes.
     *
     * @param handler receives (email, password)
     */
    public void setOnLogin(BiConsumer<String, String> handler) {
        this.onLogin = handler;
    }

    /** Call from the auth service on successful login. */
    public void loginSuccess() {
        loginButton.setDisable(false);
        loginButton.setText("Sign In");
        clearError();
    }

    /**
     * Call from the auth service on failed authentication.
     *
     * @param message human-readable error message
     */
    public void loginFailure(String message) {
        loginButton.setDisable(false);
        loginButton.setText("Sign In");
        showError(message != null ? message : "Invalid email or password.");
        shakePasswordField();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /** Horizontal shake animation on the password field to signal an error. */
    private void shakePasswordField() {
        javafx.animation.TranslateTransition shake =
                new javafx.animation.TranslateTransition(Duration.millis(60), passwordField);
        shake.setFromX(0);
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> passwordField.setTranslateX(0));
        shake.play();
    }
}
