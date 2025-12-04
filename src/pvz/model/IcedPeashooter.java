package pvz.model;

import javafx.scene.image.ImageView;

import pvz.util.AssetLoader;

public class IcedPeashooter extends Plant {
    public IcedPeashooter() {
        super(175, 20, 100);
    }

    public IcedPeashooter(int x, int y) {
        this();
        this.x = x;
        this.y = y;
        ImageView sprite = new ImageView(AssetLoader.loadImage("images/plants/icedpeashooter.gif"));
        sprite.setFitWidth(90);
        sprite.setFitHeight(85);
        sprite.setPreserveRatio(true);
        sprite.setLayoutX((x - sprite.getFitWidth() / 2) + 5);
        sprite.setLayoutY((y - sprite.getFitHeight() / 2) - 25);
        setSprite(sprite);
    }

    @Override
    public void run() {
        // Placeholder behaviour similar to peashooter (ice effect handled later)
    }
}
