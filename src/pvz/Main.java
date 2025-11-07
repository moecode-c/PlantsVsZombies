package pvz;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pvz.model.PlayerStore;
import pvz.ui.ImageMenuPane;
import pvz.ui.AuthFormPane;

/**
 * Application entry point. Shows the image-based main menu and opens small
 * dialogs for Sign In / Sign Up when the corresponding slab is clicked.
 *
 * Keys:
 * - F2 toggles a visual overlay (drawn by ImageMenuPane) so you can see/adjust
 *   hotspot bounds while tuning.
 */
public class Main extends Application {
    private final PlayerStore store = new PlayerStore();
    private boolean debug = false;

    @Override
    public void start(Stage stage) {
        store.load();

        // Root stack so we can overlay the auth form as a popover above the menu
        StackPane root = new StackPane();
        // Menu draws the background and handles hover image swapping.
        ImageMenuPane menu = new ImageMenuPane();
        root.getChildren().add(menu);
        // Wire up click callbacks from hotspots (Sign In / Sign Up / Exit)
        menu.setHandler(new ImageMenuPane.Handler() {
            @Override public void onSignIn() { showAuth(root, AuthFormPane.Mode.SIGN_IN); }
            @Override public void onSignUp() { showAuth(root, AuthFormPane.Mode.SIGN_UP); }
            @Override public void onExit() { Platform.exit(); }
        });

        // Size the window to exactly match the image so the overlay is pixel-perfect
        Scene scene = new Scene(root, menu.getPrefWidth(), menu.getPrefHeight());
        

        stage.setScene(scene);
        stage.setTitle("PvZ Menu");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    /** Convenience to show an information/error popup. */
    private void show(Alert.AlertType t, String msg) {
        Alert a = new Alert(t, msg); a.setHeaderText(null); a.showAndWait();
    }

    private void showAuth(StackPane root, AuthFormPane.Mode mode) {
        // Dimmer to block clicks to the menu and give a popover feel
        Pane dim = new Pane();
        dim.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        dim.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        dim.prefWidthProperty().bind(root.widthProperty());
        dim.prefHeightProperty().bind(root.heightProperty());
        dim.setOnMouseClicked(e -> {}); // consume clicks

        AuthFormPane form = new AuthFormPane(mode);
        form.setHandler(new AuthFormPane.Handler() {
            @Override public void onSubmit(String username, String password) {
                boolean ok = switch (mode) {
                    case SIGN_IN -> store.signIn(username, password);
                    case SIGN_UP -> store.createAccount(username, password);
                };
                if (ok) {
                    if (mode == AuthFormPane.Mode.SIGN_UP) {
                        // Optionally sign in immediately after creating an account
                        store.signIn(username, password);
                    }
                    show(Alert.AlertType.INFORMATION, (mode==AuthFormPane.Mode.SIGN_IN?"Signed in":"Account created") + " successfully.");
                    root.getChildren().removeAll(dim, form);
                } else {
                    show(Alert.AlertType.ERROR, mode==AuthFormPane.Mode.SIGN_IN ? "Invalid username or password." : "Username exists or invalid.");
                }
            }
            @Override public void onBack() { root.getChildren().removeAll(dim, form); }
        });
        // Add on top of the menu as a popover
        root.getChildren().addAll(dim, form);
        StackPane.setAlignment(form, Pos.CENTER);
        form.requestFocus(); // so ENTER works immediately
    }

    

    public static void main(String[] args) { launch(args); }
}
