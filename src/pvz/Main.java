package pvz;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pvz.model.PlayerStore;
import pvz.ui.ImageMenuPane;

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

        // Menu draws the background and handles hover image swapping.
        ImageMenuPane menu = new ImageMenuPane();
        // Wire up click callbacks from hotspots (Sign In / Sign Up / Exit)
        menu.setHandler(new ImageMenuPane.Handler() {
            @Override public void onSignIn() { showSignIn(); }
            @Override public void onSignUp() { showSignUp(); }
            @Override public void onExit() { Platform.exit(); }
        });

        // Size the window to exactly match the image so the overlay is pixel-perfect
        Scene scene = new Scene(menu, menu.getPrefWidth(), menu.getPrefHeight());
        

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

    /** Build and show the Sign Up dialog (minimal form). */
    private void showSignUp() {
        Stage dialog = new Stage();
        TextField user = new TextField();
        PasswordField pw = new PasswordField();
        var create = new javafx.scene.control.Button("Create");
        create.setOnAction(e -> {
            boolean ok = store.createAccount(user.getText().trim(), pw.getText());
            show(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    ok ? "Account created" : "Could not create (exists or blank)");
        });
        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(10); g.setStyle("-fx-padding: 16;");
        int r = 0;
        g.add(new Label("Sign Up"), 0, r++);
        g.add(new Label("Username:"), 0, r); g.add(user, 1, r++);
        g.add(new Label("Password:"), 0, r); g.add(pw, 1, r++);
        g.add(create, 1, r);
        dialog.setScene(new Scene(g));
        dialog.setTitle("Sign Up");
        dialog.setResizable(false);
        dialog.show();
    }

    /** Build and show the Sign In dialog (minimal form). */
    private void showSignIn() {
        Stage dialog = new Stage();
        TextField user = new TextField();
        PasswordField pw = new PasswordField();
        var login = new javafx.scene.control.Button("Login");
        login.setOnAction(e -> {
            boolean ok = store.signIn(user.getText().trim(), pw.getText());
            show(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    ok ? ("Welcome, " + user.getText().trim()) : "Invalid credentials");
        });
        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(10); g.setStyle("-fx-padding: 16;");
        int r = 0;
        g.add(new Label("Sign In"), 0, r++);
        g.add(new Label("Username:"), 0, r); g.add(user, 1, r++);
        g.add(new Label("Password:"), 0, r); g.add(pw, 1, r++);
        g.add(login, 1, r);
        dialog.setScene(new Scene(g));
        dialog.setTitle("Sign In");
        dialog.setResizable(false);
        dialog.show();
    }

    public static void main(String[] args) { launch(args); }
}
