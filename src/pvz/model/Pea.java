package pvz.model;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import pvz.util.AssetLoader;

/**
 * Simple projectile fired by peashooters.
 */
public class Pea extends Characters {
    private final int damage;
    private final Plant owner;

    public Pea(int damage, Plant owner) {
        this.damage = damage;
        this.owner = owner;
        this.health = 1;
        ImageView view = new ImageView(AssetLoader.loadImage("images/others/pea.png"));
        view.setFitWidth(30);
        view.setFitHeight(30);
        view.setPreserveRatio(true);
        this.elementImage = view;
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

    @Override
    public void run() {
        setAlive(true);
        try {
            while (isAlive()) {
                Platform.runLater(() -> elementImage.setLayoutX(elementImage.getLayoutX() + 6));
                checkCollisions();
                if (elementImage.getLayoutX() > Yard.WIDTH) {
                    break;
                }
                Thread.sleep(20);
            }
        } catch (InterruptedException ignored) {
        } finally {
            disappear(Yard.root);
            synchronized (Yard.peas) {
                Yard.peas.remove(this);
            }
        }
    }

    private void checkCollisions() {
        synchronized (Yard.zombies) {
            for (Zombie zombie : Yard.zombies) {
                if (zombie == null || zombie.getElementImage() == null) {
                    continue;
                }
                boolean hit = elementImage.getBoundsInParent().intersects(zombie.getElementImage().getBoundsInParent());
                if (hit) {
                    zombie.takeDamage(damage);
                    setAlive(false);
                    break;
                }
            }
        }
    }
}
