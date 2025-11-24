package pvz.ui;

import java.util.Objects;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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
    }

    private final Image baseImg;
    private final Image hoverPlay;
    private final Image hoverOptions;
    private final Image hoverMore;
    private final Image hoverExit;

    private final ImageView view;
    private final Canvas overlay;

    private Rectangle2D rPlay;
    private Rectangle2D rOptions;
    private Rectangle2D rMore;
    private Rectangle2D rLogout;
    private Rectangle2D rDelete;
    private Rectangle2D rExit;
    private Rectangle2D rExit2;

    private Handler handler;
    private String playerUsername;

    private double usernameX;
    private double usernameY;
    private double usernameSize;
    private Color usernameColor;

    public GameMenuPane(String username) {
        this.playerUsername = username;

        baseImg = load("/pvz/images/menu/main_bg.png");
        hoverPlay = load("/pvz/images/menu/hover_play.png");
        hoverOptions = load("/pvz/images/menu/hover_options.png");
        hoverMore = load("/pvz/images/menu/hover_more.png");
        hoverExit = load("/pvz/images/menu/hover_exit.png");

        if (baseImg == null || hoverPlay == null || hoverOptions == null ||
                hoverMore == null || hoverExit == null) {
            throw new IllegalStateException("Missing game menu images under /pvz/images/menu");
        }

        // Updated hotspots based on your pixel-to-normalized calculations
        rPlay    = rect(0.52, 0.33, 0.36, 0.12);  // Play button
        rOptions = rect(0.51, 0.47, 0.35, 0.10);  // Options button
        rExit    = rect(0.52, 0.59, 0.32, 0.10);  // Exit (top)
        rMore    = rect(0.48, 0.75, 0.13, 0.12);  // More button
        rLogout  = rect(0.71, 0.80, 0.08, 0.08);  // Logout button
        rDelete  = rect(0.81, 0.84, 0.06, 0.08);  // Delete button
        rExit2   = rect(0.90, 0.82, 0.06, 0.02);  // Exit (bottom)

        usernameX = 55;
        usernameY = 120;
        usernameSize = 18;
        usernameColor = Color.WHITE;

        view = new ImageView(baseImg);
        view.setPreserveRatio(true);
        view.setFitWidth(800);
        view.setFitHeight(598);

        overlay = new Canvas(800, 598);
        overlay.setMouseTransparent(true);

        getChildren().addAll(view, overlay);

        setPrefSize(800, 598);
        setMinSize(800, 598);
        setMaxSize(800, 598);

        drawUsernameOnOverlay();

        view.setOnMouseMoved(e -> {
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
            view.setImage(baseImg);
            setCursor(Cursor.DEFAULT);
        });

        view.setOnMouseClicked(e -> {
            if (handler == null) return;
            int which = whichHotspot(e.getX(), e.getY());
            switch (which) {
                case 1 -> {
                    // Draw play menu image on overlay with heavier full-screen shadow
                    GraphicsContext gc = overlay.getGraphicsContext2D();
                    gc.clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
                    // Draw full-screen shadow (more opaque)
                    gc.setFill(new Color(0, 0, 0, 0.7));
                    gc.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());
                    gc.setFill(usernameColor);
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, usernameSize));
                    gc.fillText(playerUsername, usernameX, usernameY);
                    java.net.URL url = getClass().getResource("/pvz/images/menu/playmenu_bg.png");
                    if (url != null) {
                        Image playImg = new Image(url.toString());
                        gc.drawImage(playImg, 120, 100, 600, 400); // Centered overlay
                    } else {
                        System.err.println("ERROR: playmenu_bg.png not found at /pvz/images/menu/playmenu_bg.png");
                    }
                }
                case 2 -> handler.onOptions();
                case 3 -> handler.onMore();
                case 5 -> handler.onLogout();
                case 6 -> handler.onDeleteAccount();
                case 4, 7 -> handler.onExit();
            }
        });

        widthProperty().addListener((obs, o, w) -> resizeToImage());
        heightProperty().addListener((obs, o, h) -> resizeToImage());
    }

    private void drawUsernameOnOverlay() {
        GraphicsContext gc = overlay.getGraphicsContext2D();
        gc.clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
        gc.setFill(usernameColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, usernameSize));
        gc.fillText(playerUsername, usernameX, usernameY);
    }


    private void resizeToImage() {
        // View is fixed at 800x598 with preserveRatio=true
        drawUsernameOnOverlay();
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    /**
     * Set custom hotspot positions (normalized coordinates 0..1)
     */
    public void setHotspotsNormalized(Rectangle2D play, Rectangle2D options, Rectangle2D more,
                                     Rectangle2D logout, Rectangle2D delete, Rectangle2D exit) {
        this.rPlay = Objects.requireNonNull(play);
        this.rOptions = Objects.requireNonNull(options);
        this.rMore = Objects.requireNonNull(more);
        this.rLogout = Objects.requireNonNull(logout);
        this.rDelete = Objects.requireNonNull(delete);
        this.rExit = Objects.requireNonNull(exit);
    }

    /**
     * Set custom username display position and appearance
     */
    public void setUsernamePosition(double x, double y, double size, javafx.scene.paint.Color color) {
        this.usernameX = x;
        this.usernameY = y;
        this.usernameSize = size;
        this.usernameColor = color;
        drawUsernameOnOverlay();
    }

    // FIXED: now uses actual ImageView size
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
}
