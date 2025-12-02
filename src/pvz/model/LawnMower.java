package pvz.model;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import pvz.util.AssetLoader;

/**
 * Handles the classic row-clearing lawn mower behaviour.
 */
public class LawnMower extends Characters {
    private final int row;
    private volatile boolean active;

    public LawnMower(int row) {
        this.row = row;
        this.health = Integer.MAX_VALUE;
        ImageView view = new ImageView(AssetLoader.loadImage("images/yard-related/lawnmower.png"));
        view.setFitWidth(90);
        view.setFitHeight(70);
        view.setPreserveRatio(true);
        this.elementImage = view;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void appear(Pane root) {
        if (!root.getChildren().contains(elementImage)) {
            root.getChildren().add(elementImage);
        }
        setAlive(true);
    }

    @Override
    public void disappear(Pane root) {
        Platform.runLater(() -> root.getChildren().remove(elementImage));
        setAlive(false);
    }

    public void activate(AnchorPane root) {
        if (active) {
            return;
        }
        active = true;
        Thread mover = new Thread(() -> {
            try {
                while (elementImage.getLayoutX() < Yard.WIDTH && Yard.gameOn) {
                    Platform.runLater(() -> elementImage.setLayoutX(elementImage.getLayoutX() + 8));
                    squashZombies();
                    Thread.sleep(20);
                }
            } catch (InterruptedException ignored) {
            } finally {
                disappear(root);
            }
        });
        mover.setDaemon(true);
        mover.start();
    }

    private void squashZombies() {
        synchronized (Yard.zombies) {
            Yard.zombies.forEach(zombie -> {
                if (zombie != null && zombie.getElementImage() != null &&
                        elementImage.getBoundsInParent().intersects(zombie.getElementImage().getBoundsInParent())) {
                    zombie.takeDamage(Integer.MAX_VALUE);
                }
            });
        }
    }

    @Override
    public void run() {
        // Lawn mowers are animated manually via activate().
    }
}
