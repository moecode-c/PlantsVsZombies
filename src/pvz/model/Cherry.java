package pvz.model;

import javafx.scene.image.ImageView;

import pvz.util.AssetLoader;

public class Cherry extends Plant {
    public Cherry() {
        super(150, 7, 150);
    }

    public Cherry(int x, int y) {
        this();
        this.x = x;
        this.y = y;
        ImageView sprite = new ImageView(AssetLoader.loadImage("images/plants/cherry.png"));
        sprite.setFitWidth(90);
        sprite.setFitHeight(90);
        sprite.setPreserveRatio(true);
        sprite.setLayoutX(x - sprite.getFitWidth() / 2.0);
        sprite.setLayoutY(y - sprite.getFitHeight() / 2.0);
        setSprite(sprite);
    }

    @Override
    public void run() {
        // Placeholder: could trigger explosion behaviour later.
    }
}
