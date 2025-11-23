package pvz;

import javax.swing.plaf.synth.Region;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
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
            @Override public void onSignIn() { showAuth(root, stage, AuthFormPane.Mode.SIGN_IN); }
            @Override public void onSignUp() { showAuth(root, stage, AuthFormPane.Mode.SIGN_UP); }
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

    private void showAuth(StackPane root, Stage stage, AuthFormPane.Mode mode) {
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
                    
                    // If signed in, show game menu
                    if (mode == AuthFormPane.Mode.SIGN_IN || mode == AuthFormPane.Mode.SIGN_UP) {
                        showGameMenu(root, stage, username);
                    }
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

    private void showGameMenu(StackPane root, Stage stage, String username) {
        // Clear the root and show game menu
        root.getChildren().clear();
        
        GameMenuPane gameMenu = new GameMenuPane(username);
        root.getChildren().add(gameMenu);
        
        // Enable debug mode to see hotspot positions - press F2 to toggle
        gameMenu.setDebug(true);
        
        // Adjust hotspots for Lenovo LOQ 15 (15.6-inch)
        // These are SEPARATE button hotspots at the bottom
        gameMenu.setHotspotsNormalized(
            new javafx.geometry.Rectangle2D(0.25, 0.28, 0.50, 0.10),  // Play button (center)
            new javafx.geometry.Rectangle2D(0.25, 0.42, 0.50, 0.10),  // Options button (center)
            new javafx.geometry.Rectangle2D(0.25, 0.56, 0.50, 0.10),  // More button (center)
            new javafx.geometry.Rectangle2D(0.02, 0.82, 0.28, 0.12),  // Logout button (bottom left)
            new javafx.geometry.Rectangle2D(0.36, 0.82, 0.28, 0.12),  // Delete Account button (bottom center)
            new javafx.geometry.Rectangle2D(0.70, 0.82, 0.28, 0.12)   // Exit button (bottom right)
        );
        
        // Adjust username position (x, y in pixels, fontSize, color)
        gameMenu.setUsernamePosition(55, 100, 18, javafx.scene.paint.Color.WHITE);
        
        gameMenu.setHandler(new GameMenuPane.Handler() {
            @Override public void onPlay() {
                show(Alert.AlertType.INFORMATION, "Play feature coming soon!");
            }
            
            @Override public void onOptions() {
                show(Alert.AlertType.INFORMATION, "Options feature coming soon!");
            }
            
            @Override public void onMore() {
                show(Alert.AlertType.INFORMATION, "More feature coming soon!");
            }
            
            @Override public void onLogout() {
                // Ask for confirmation before logging out
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Logout");
                confirm.setHeaderText("Logout");
                confirm.setContentText("Are you sure you want to logout?");
                
                if (confirm.showAndWait().isPresent() && 
                    confirm.getResult() == javafx.scene.control.ButtonType.OK) {
                    store.signOut();
                    // Return to main menu
                    root.getChildren().clear();
                    ImageMenuPane menu = new ImageMenuPane();
                    root.getChildren().add(menu);
                    menu.setHandler(new ImageMenuPane.Handler() {
                        @Override public void onSignIn() { showAuth(root, stage, AuthFormPane.Mode.SIGN_IN); }
                        @Override public void onSignUp() { showAuth(root, stage, AuthFormPane.Mode.SIGN_UP); }
                        @Override public void onExit() { Platform.exit(); }
                    });
                    show(Alert.AlertType.INFORMATION, "Logged out successfully.");
                }
            }
            
            @Override public void onDeleteAccount() {
                // Ask for confirmation before deleting account
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Account");
                confirm.setHeaderText("Delete Account");
                confirm.setContentText("Are you sure you want to delete your account? This cannot be undone.");
                
                if (confirm.showAndWait().isPresent() && 
                    confirm.getResult() == javafx.scene.control.ButtonType.OK) {
                    // Delete account from store
                    if (store.deleteAccount(username, "")) {
                        show(Alert.AlertType.INFORMATION, "Account deleted successfully.");
                        store.signOut();
                        // Return to main menu
                        root.getChildren().clear();
                        ImageMenuPane menu = new ImageMenuPane();
                        root.getChildren().add(menu);
                        menu.setHandler(new ImageMenuPane.Handler() {
                            @Override public void onSignIn() { showAuth(root, stage, AuthFormPane.Mode.SIGN_IN); }
                            @Override public void onSignUp() { showAuth(root, stage, AuthFormPane.Mode.SIGN_UP); }
                            @Override public void onExit() { Platform.exit(); }
                        });
                    } else {
                        show(Alert.AlertType.ERROR, "Failed to delete account.");
                    }
                }
            }
            
            @Override public void onExit() {
                // Ask for confirmation before exiting
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Exit");
                confirm.setHeaderText("Exit Application");
                confirm.setContentText("Are you sure you want to exit the application?");
                
                if (confirm.showAndWait().isPresent() && 
                    confirm.getResult() == javafx.scene.control.ButtonType.OK) {
                    Platform.exit();
                }
            }
        });
    }

    

    public static void main(String[] args) { launch(args); }
}
