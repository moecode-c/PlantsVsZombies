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

public class Main extends Application {
    private final PlayerStore store = new PlayerStore();
    private boolean debug = false;

    @Override
    public void start(Stage stage) {
        store.load();

        ImageMenuPane menu = new ImageMenuPane();
        menu.setHandler(new ImageMenuPane.Handler() {
            @Override public void onSignIn() { showSignIn(); }
            @Override public void onSignUp() { showSignUp(); }
            @Override public void onExit() { Platform.exit(); }
        });

        Scene scene = new Scene(menu, menu.getPrefWidth(), menu.getPrefHeight());
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case F2 -> { debug = !debug; menu.setDebug(debug); }
                default -> {}
            }
        });

        stage.setScene(scene);
        stage.setTitle("PvZ Menu");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private void show(Alert.AlertType t, String msg) {
        Alert a = new Alert(t, msg); a.setHeaderText(null); a.showAndWait();
    }

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
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(10); g.setStyle("-fx-padding: 16;");
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
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(10); g.setStyle("-fx-padding: 16;");
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
