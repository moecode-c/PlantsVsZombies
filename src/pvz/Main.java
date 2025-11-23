
package pvz;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pvz.model.PlayerStore;
import pvz.ui.AuthFormPane;
import pvz.ui.GameMenuPane;
import pvz.ui.ImageMenuPane;

/**
 * Application entry point. Shows the image-based main menu and opens dialogs for Sign In / Sign Up.
 */
public class Main extends Application {
    private final PlayerStore store = new PlayerStore();

    @Override
    public void start(Stage stage) {
        store.load();
        StackPane root = new StackPane();
        ImageMenuPane menu = new ImageMenuPane();
        root.getChildren().add(menu);
        menu.setHandler(new ImageMenuPane.Handler() {
            @Override public void onSignIn() { showAuth(root, stage, AuthFormPane.Mode.SIGN_IN); }
            @Override public void onSignUp() { showAuth(root, stage, AuthFormPane.Mode.SIGN_UP); }
            @Override public void onExit() { Platform.exit(); }
        });
        Scene scene = new Scene(root, menu.getPrefWidth(), menu.getPrefHeight());
        stage.setScene(scene);
        stage.setTitle("PvZ Menu");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private void show(Alert.AlertType t, String msg) {
        Alert a = new Alert(t, msg); a.setHeaderText(null); a.showAndWait();
    }

    private void showAuth(StackPane root, Stage stage, AuthFormPane.Mode mode) {
        Pane dim = new Pane();
        dim.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        dim.setMinSize(Double.MAX_VALUE, Double.MAX_VALUE);
        dim.prefWidthProperty().bind(root.widthProperty());
        dim.prefHeightProperty().bind(root.heightProperty());
        dim.setOnMouseClicked(e -> {});
        AuthFormPane form = new AuthFormPane(mode);
        form.setHandler(new AuthFormPane.Handler() {
            @Override public void onSubmit(String username, String password) {
                boolean ok = (mode == AuthFormPane.Mode.SIGN_IN)
                    ? store.signIn(username, password)
                    : store.createAccount(username, password);
                if (ok) {
                    if (mode == AuthFormPane.Mode.SIGN_UP) store.signIn(username, password);
                    show(Alert.AlertType.INFORMATION, (mode==AuthFormPane.Mode.SIGN_IN?"Signed in":"Account created") + " successfully.");
                    root.getChildren().removeAll(dim, form);
                    showGameMenu(root, stage, username);
                } else {
                    show(Alert.AlertType.ERROR, mode==AuthFormPane.Mode.SIGN_IN ? "Invalid username or password." : "Username exists or invalid.");
                }
            }
            @Override public void onBack() { root.getChildren().removeAll(dim, form); }
        });
        root.getChildren().addAll(dim, form);
        StackPane.setAlignment(form, javafx.geometry.Pos.CENTER);
        form.requestFocus();
    }

    private void showGameMenu(StackPane root, Stage stage, String username) {
        root.getChildren().clear();
        GameMenuPane gameMenu = new GameMenuPane(username);
        root.getChildren().add(gameMenu);
        gameMenu.setDebug(false);
        gameMenu.setUsernamePosition(55, 100, 18, javafx.scene.paint.Color.WHITE);
    }

    

    public static void main(String[] args) { launch(args); }
}
