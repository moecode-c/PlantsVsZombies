package pvz.model;

import javafx.scene.layout.Pane;
import javafx.scene.image.ImageView;

public abstract class Plant extends Thread {
    protected int cost;
    protected double waitingTime;
    protected int health;
    protected ImageView sprite;

    public Plant() {}
    public Plant(int cost, double waitingTime, int health) {
        this.cost = cost;
        this.waitingTime = waitingTime;
        this.health = health;
    }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public void takeDamage(int damage) { health -= damage; }
    public void appear(Pane root) {}
    public void disappear(Pane root) {}

    @Override
    public abstract void run(); // Each plant must implement its own thread logic
}
