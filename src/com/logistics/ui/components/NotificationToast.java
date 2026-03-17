package com.logistics.ui.components;

import com.logistics.ui.theme.DesignTokens;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * A transient notification toast displayed in the bottom-right corner.
 *
 * Design spec:
 *   - Width: 340px fixed
 *   - Position: bottom-right, 24px from edges (managed by parent ToastHost)
 *   - Elevation: Level 4
 *   - Left border: 4px semantic color band
 *   - Auto-dismiss: configurable duration (default 4 seconds)
 *   - Slide-in from the right, slide-out to the right
 *
 * The static show() factory method is the primary entry point.
 * It requires a ToastHost StackPane to be set on the scene first.
 *
 * Usage:
 *   // Once, during app startup:
 *   ToastHost host = new ToastHost();
 *   rootLayout.getChildren().add(host);
 *   NotificationToast.setHost(host);
 *
 *   // Anywhere in the app:
 *   NotificationToast.show("Shipment delivered", "SHP-1823 arrived at JFK.", Type.SUCCESS);
 */
public class NotificationToast extends HBox {

    public enum Type { SUCCESS, WARNING, ERROR, INFO }

    private static ToastHost sharedHost;

    private final Type type;
    private Runnable onDismiss;

    // -------------------------------------------------------------------------
    // Static API
    // -------------------------------------------------------------------------

    /** Register the single application-wide toast host. */
    public static void setHost(ToastHost host) {
        sharedHost = host;
    }

    /**
     * Displays a toast notification. Auto-dismisses after 4 seconds.
     *
     * @param title   short headline (14px bold)
     * @param message descriptive body (13px, wraps)
     * @param type    determines accent color
     */
    public static void show(String title, String message, Type type) {
        show(title, message, type, 4_000);
    }

    /**
     * Displays a toast notification with a custom auto-dismiss delay.
     *
     * @param title          short headline
     * @param message        descriptive body
     * @param type           determines accent color
     * @param autoDismissMs  milliseconds before automatic dismissal (0 = never)
     */
    public static void show(String title, String message, Type type, long autoDismissMs) {
        if (sharedHost == null) {
            System.err.println("[NotificationToast] No ToastHost registered. Call setHost() first.");
            return;
        }
        NotificationToast toast = new NotificationToast(title, message, type);
        sharedHost.addToast(toast);
        toast.animateIn();
        if (autoDismissMs > 0) {
            Timeline autoClose = new Timeline(
                    new KeyFrame(Duration.millis(autoDismissMs), e -> toast.dismiss())
            );
            autoClose.play();
        }
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    private NotificationToast(String title, String message, Type type) {
        this.type = type;

        getStyleClass().addAll("toast", cssToastClass(type));
        setSpacing(DesignTokens.SPACE_3);
        setPadding(new Insets(DesignTokens.SPACE_4));
        setAlignment(Pos.TOP_LEFT);
        setMaxWidth(340);
        setMinWidth(280);

        // Text stack (title + message)
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("toast-title");
        titleLabel.setWrapText(true);

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("toast-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(260);

        VBox textStack = new VBox(4, titleLabel, messageLabel);
        HBox.setHgrow(textStack, Priority.ALWAYS);

        // Close button
        Button closeBtn = new Button("x");
        closeBtn.getStyleClass().add("btn-icon");
        closeBtn.setOnAction(e -> dismiss());

        getChildren().addAll(textStack, closeBtn);

        // Start off-screen (translateX = 380 to the right)
        setTranslateX(380);
        setOpacity(0);
    }

    // -------------------------------------------------------------------------
    // Animation
    // -------------------------------------------------------------------------

    void animateIn() {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(translateXProperty(), 380),
                        new KeyValue(opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(DesignTokens.ANIM_TOAST_SLIDE),
                        new KeyValue(translateXProperty(), 0,
                                javafx.animation.Interpolator.EASE_OUT),
                        new KeyValue(opacityProperty(), 1,
                                javafx.animation.Interpolator.EASE_OUT)
                )
        );
        tl.play();
    }

    void dismiss() {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(translateXProperty(), 0),
                        new KeyValue(opacityProperty(), 1)
                ),
                new KeyFrame(Duration.millis(DesignTokens.ANIM_TOAST_SLIDE),
                        new KeyValue(translateXProperty(), 380,
                                javafx.animation.Interpolator.EASE_IN),
                        new KeyValue(opacityProperty(), 0,
                                javafx.animation.Interpolator.EASE_IN)
                )
        );
        tl.setOnFinished(e -> {
            if (sharedHost != null) sharedHost.removeToast(this);
            if (onDismiss != null) onDismiss.run();
        });
        tl.play();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String cssToastClass(Type type) {
        return switch (type) {
            case SUCCESS -> "toast-success";
            case WARNING -> "toast-warning";
            case ERROR   -> "toast-error";
            case INFO    -> "toast-info";
        };
    }

    // -------------------------------------------------------------------------
    // ToastHost — the container that manages stacked toasts
    // -------------------------------------------------------------------------

    /**
     * A transparent overlay pane anchored to the bottom-right of the window.
     * Add this as the top-most child of your root StackPane once at startup.
     */
    public static class ToastHost extends StackPane {

        private final VBox stack = new VBox(8);

        public ToastHost() {
            setPickOnBounds(false); // Let mouse events through the transparent area
            setAlignment(Pos.BOTTOM_RIGHT);
            setPadding(new Insets(0, DesignTokens.SPACE_6, DesignTokens.SPACE_6, 0));

            stack.setAlignment(Pos.BOTTOM_RIGHT);
            stack.setPickOnBounds(false);
            getChildren().add(stack);
        }

        void addToast(NotificationToast toast) {
            stack.getChildren().add(0, toast);
        }

        void removeToast(NotificationToast toast) {
            stack.getChildren().remove(toast);
        }
    }
}
