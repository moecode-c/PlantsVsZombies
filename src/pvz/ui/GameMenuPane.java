package pvz.ui;

import java.util.Objects;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameMenuPane extends StackPane {

    public interface Handler {
        void onPlay();
        void onOptions();
        void onMore();
        void onLogout();
        void onDeleteAccount();
        void onExit();
        default void onLevelSelected(int level) {}
    }

    private final Image baseImg;
    private final Image hoverPlay;
    private final Image hoverOptions;
    private final Image hoverMore;
    private final Image hoverExit;
    private final Image playMenuImg;

    private final ImageView view;
    private final Pane baseLayer;
    private final Pane overlayLayer;
    private final ImageView overlayImage;
    private final Label usernameLabel;

    private Rectangle2D rPlay;
    private Rectangle2D rOptions;
    private Rectangle2D rMore;
    private Rectangle2D rLogout;
    private Rectangle2D rDelete;
    private Rectangle2D rExit;
    private Rectangle2D rExit2;

    private boolean showingLevelOverlay;

    private Handler handler;
    private String playerUsername;

    private double usernameX = 55;
    private double usernameY = 120;
    private double usernameSize = 18;
    private Color usernameColor = Color.WHITE;

    public GameMenuPane(String username) {
        this.playerUsername = username;

        baseImg = load("/pvz/images/menu/main_bg.png");
        hoverPlay = load("/pvz/images/menu/hover_play.png");
        hoverOptions = load("/pvz/images/menu/hover_options.png");
        hoverMore = load("/pvz/images/menu/hover_more.png");
        hoverExit = load("/pvz/images/menu/hover_exit.png");
        playMenuImg = load("/pvz/images/menu/playmenu_bg.png");

        if (baseImg == null || hoverPlay == null || hoverOptions == null
            || hoverMore == null || hoverExit == null) {
            throw new IllegalStateException("Missing game menu images under /pvz/images/menu");
        }

        rPlay    = rect(0.52, 0.33, 0.36, 0.12);  // Play button
        rOptions = rect(0.51, 0.47, 0.35, 0.10);  // Options button
        rExit    = rect(0.52, 0.59, 0.32, 0.10);  // Exit (top)
        rMore    = rect(0.48, 0.75, 0.13, 0.12);  // More button
        rLogout  = rect(0.71, 0.80, 0.08, 0.08);  // Logout button
        rDelete  = rect(0.81, 0.84, 0.06, 0.08);  // Delete button
        rExit2   = rect(0.90, 0.82, 0.06, 0.02);  // Exit (bottom)

        baseLayer = new Pane();
        baseLayer.setPrefSize(800, 598);

        view = new ImageView(baseImg);
        view.setPreserveRatio(true);
        view.setFitWidth(800);
        view.setFitHeight(598);
        baseLayer.getChildren().add(view);

        usernameLabel = new Label(playerUsername);
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, usernameSize));
        usernameLabel.setTextFill(usernameColor);
        usernameLabel.setLayoutX(usernameX);
        usernameLabel.setLayoutY(usernameY - usernameSize);
        baseLayer.getChildren().add(usernameLabel);

        overlayLayer = new Pane();
        overlayLayer.setPrefSize(800, 598);
        overlayLayer.setVisible(false);
        overlayLayer.setMouseTransparent(true);

        Rectangle dimmer = new Rectangle(800, 598);
        dimmer.setFill(new Color(0, 0, 0, 0.75));
        overlayLayer.getChildren().add(dimmer);

        overlayImage = playMenuImg == null ? null : new ImageView(playMenuImg);
        if (overlayImage != null) {
            overlayImage.setLayoutX(120);
            overlayImage.setLayoutY(100);
            overlayImage.setFitWidth(600);
            overlayImage.setFitHeight(400);
            overlayImage.setPreserveRatio(false);
            overlayLayer.getChildren().add(overlayImage);
        }

        configureLevelButtons();

        getChildren().addAll(baseLayer, overlayLayer);

        setPrefSize(800, 598);
        setMinSize(800, 598);
        setMaxSize(800, 598);

        view.setOnMouseMoved(e -> {
            if (showingLevelOverlay) {
                setCursor(Cursor.HAND);
                return;
            }
            int which = whichHotspot(e.getX(), e.getY());
            switch (which) {
                case 1 -> { view.setImage(hoverPlay); setCursor(Cursor.HAND); }
                case 2 -> { view.setImage(hoverOptions); setCursor(Cursor.HAND); }
                case 3 -> { view.setImage(hoverMore); setCursor(Cursor.HAND); }
                case 4 -> { view.setImage(hoverExit); setCursor(Cursor.HAND); }
                case 5, 6, 7 -> { view.setImage(baseImg); setCursor(Cursor.HAND); }
                default -> { view.setImage(baseImg); setCursor(Cursor.DEFAULT); }
            }
        });

        view.setOnMouseExited(e -> {
            if (showingLevelOverlay) {
                return;
            }
            view.setImage(baseImg);
            setCursor(Cursor.DEFAULT);
        });

        view.setOnMouseClicked(e -> {
            if (handler == null) {
                return;
            }

            if (showingLevelOverlay) {
                return;
            }

            int which = whichHotspot(e.getX(), e.getY());
            switch (which) {
                case 1 -> {
                    showLevelOverlay();
                    handler.onPlay();
                }
                case 2 -> handler.onOptions();
                case 3 -> handler.onMore();
                case 4 -> handler.onExit();
                case 5 -> handler.onLogout();
                case 6 -> handler.onDeleteAccount();
                case 7 -> handler.onExit();
                default -> { }
            }
        });
    }

    private void updateUsernameLabel() {
        usernameLabel.setText(playerUsername);
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, usernameSize));
        usernameLabel.setTextFill(usernameColor);
        usernameLabel.setLayoutX(usernameX);
        usernameLabel.setLayoutY(usernameY - usernameSize);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setHotspotsNormalized(Rectangle2D play, Rectangle2D options, Rectangle2D more,
                                      Rectangle2D logout, Rectangle2D delete, Rectangle2D exit) {
        this.rPlay = Objects.requireNonNull(play);
        this.rOptions = Objects.requireNonNull(options);
        this.rMore = Objects.requireNonNull(more);
        this.rLogout = Objects.requireNonNull(logout);
        this.rDelete = Objects.requireNonNull(delete);
        this.rExit = Objects.requireNonNull(exit);
    }

    public void setUsernamePosition(double x, double y, double size, Color color) {
        this.usernameX = x;
        this.usernameY = y;
        this.usernameSize = size;
        this.usernameColor = color;
        updateUsernameLabel();
    }

    private boolean containsNormalized(Rectangle2D r, double x, double y) {
        double iw = view.getBoundsInParent().getWidth();
        double ih = view.getBoundsInParent().getHeight();

        double rx = r.getMinX() * iw;
        double ry = r.getMinY() * ih;
        double rw = r.getWidth() * iw;
        double rh = r.getHeight() * ih;

        return x >= rx && x <= rx + rw && y >= ry && y <= ry + rh;
    }

    private int whichHotspot(double x, double y) {
        if (containsNormalized(rPlay, x, y)) return 1;
        if (containsNormalized(rOptions, x, y)) return 2;
        if (containsNormalized(rMore, x, y)) return 3;
        if (containsNormalized(rExit, x, y)) return 4;
        if (containsNormalized(rLogout, x, y)) return 5;
        if (containsNormalized(rDelete, x, y)) return 6;
        if (containsNormalized(rExit2, x, y)) return 7;
        return 0;
    }

    private Image load(String path) {
        var in = getClass().getResourceAsStream(path);
        return in == null ? null : new Image(in);
    }

    private Rectangle2D rect(double x, double y, double w, double h) {
        return new Rectangle2D(x, y, w, h);
    }

    private void showLevelOverlay() {
        showingLevelOverlay = true;
        overlayLayer.setVisible(true);
        overlayLayer.setMouseTransparent(false);
        view.setMouseTransparent(true);
        setCursor(Cursor.DEFAULT);
    }

    private void hideOverlay() {
        showingLevelOverlay = false;
        overlayLayer.setVisible(false);
        overlayLayer.setMouseTransparent(true);
        view.setMouseTransparent(false);
        setCursor(Cursor.DEFAULT);
    }

    private void configureLevelButtons() {
        double overlayX = overlayImage != null ? overlayImage.getLayoutX() : 120;
        double overlayY = overlayImage != null ? overlayImage.getLayoutY() : 100;

        double[][] levelBounds = {
            {overlayX + 20,  overlayY + 20,  115, 230}, // Level 1
            {overlayX + 140, overlayY + 20,  115, 230}, // Level 2
            {overlayX + 260, overlayY + 20,  115, 230}, // Level 3
            {overlayX + 380, overlayY + 20,  115, 230}, // Level 4
            {overlayX + 500, overlayY + 20,  115, 230}  // Level 5
        };

        for (int i = 0; i < levelBounds.length; i++) {
            addLevelButton(levelBounds[i], i + 1);
        }

        double[] backBounds = {overlayX + 220, overlayY + 260, 150, 80};
        addLevelButton(backBounds, 0);
    }

    private void addLevelButton(double[] bounds, int levelNumber) {
        Button button = new Button();
        button.setLayoutX(bounds[0]);
        button.setLayoutY(bounds[1]);
        button.setPrefWidth(bounds[2]);
        button.setPrefHeight(bounds[3]);
        button.setOpacity(0.08);
        button.setFocusTraversable(false);
        button.setStyle("-fx-background-color: transparent;");
        button.setOnAction(e -> {
            if (levelNumber == 0) {
                hideOverlay();
            } else {
                handleLevelSelection(levelNumber);
            }
        });
        overlayLayer.getChildren().add(button);
    }

    private void handleLevelSelection(int levelNumber) {
        hideOverlay();
        if (handler != null) {
            handler.onLevelSelected(levelNumber);
        }
    }

    
}
