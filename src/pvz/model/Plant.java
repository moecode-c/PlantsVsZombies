package pvz.model;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Base class for all plants placed on the yard grid.
 */
public abstract class Plant extends Characters {
    protected int cost;
    protected double waitingTime;
    protected ImageView sprite;

    public Plant() {
    }

    public Plant(int cost, double waitingTime, int health) {
        this.cost = cost;
        this.waitingTime = waitingTime;
        this.health = health;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    /** Helper to keep sprite & elementImage references in sync. */
    protected void setSprite(ImageView sprite) {
        this.sprite = sprite;
        this.elementImage = sprite;
    }

    protected ImageView getSprite() {
        return sprite != null ? sprite : elementImage;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (health <= 0) {
            setAlive(false);
            Platform.runLater(() -> {
                disappear(Yard.root);
                synchronized (Yard.plants) {
                    Yard.plants.remove(this);
                }
            });
        }
    }

    @Override
    public void appear(Pane root) {
        ImageView node = getSprite();
        if (node != null && !root.getChildren().contains(node)) {
            root.getChildren().add(node);
        }
        setAlive(true);
    }

    @Override
    public void disappear(Pane root) {
        ImageView node = getSprite();
        if (node != null) {
            Platform.runLater(() -> root.getChildren().remove(node));
        }
        setAlive(false);
    }

    @Override
    public abstract void run();
}
