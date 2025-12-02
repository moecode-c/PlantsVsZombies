package pvz.model;

import javafx.scene.image.ImageView;

import pvz.util.AssetLoader;

public class Repeater extends Plant {
    public Repeater() {
        super(200, 15, 120);
    }

    public Repeater(int x, int y) {
        this();
        this.x = x;
        this.y = y;
        ImageView sprite = new ImageView(AssetLoader.loadImage("images/plants/repeater.png"));
        sprite.setFitWidth(90);
        sprite.setFitHeight(85);
        sprite.setPreserveRatio(true);
        sprite.setLayoutX((x - sprite.getFitWidth() / 2) + 5);
        sprite.setLayoutY((y - sprite.getFitHeight() / 2) - 25);
        setSprite(sprite);
    }

    @Override
    public void run() {
        // Placeholder – could fire two peas per cycle later.
    }
}
