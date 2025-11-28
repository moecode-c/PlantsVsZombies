import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Peashooter extends Plant {
    public Peashooter() {
        super(100, 15, 100);
    }

    public Peashooter(int posX, int posY) {
        this();
        super.x = posX;
        super.y = posY;
        sprite = new ImageView(new Image("images/plants/peashooter.gif"));
        sprite.setFitWidth(90);
        sprite.setFitHeight(85);
        sprite.setPreserveRatio(true);
        sprite.setLayoutX((posX - sprite.getFitWidth() / 2) + 5);
        sprite.setLayoutY((posY - sprite.getFitHeight() / 2) - 25);
    }

    @Override
    public synchronized void run() {
        while (isAlive() && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(4000);
                if (!isAlive() || Thread.currentThread().isInterrupted()) {
                    this.disappear(Yard.root);
                    break;
                }
                Pea projectile = new Pea(15, this);
                projectile.sprite.setLayoutX(sprite.getLayoutX() + 65);
                projectile.sprite.setLayoutY(sprite.getLayoutY() + 31);
                projectile.appear(Yard.root);
                Yard.peas.add(projectile);
                Thread projectileThread = new Thread(projectile);
                projectileThread.setDaemon(true);
                projectileThread.start();
                playShootSound();
            } catch (Exception ex) {
                System.out.println("Peashooter interrupted: " + ex.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
        disappear(Yard.root);
        System.out.println("Peashooter stopped.");
    }

    public void playShootSound() {
        try {
            String audioPath = getClass().getResource("/music/peashooter-shoot.mp3").toExternalForm();
            Media sound = new Media(audioPath);
            MediaPlayer player = new MediaPlayer(sound);
            player.setVolume(0.3);
            player.play();
        } catch (Exception ex) {
            System.out.println("Audio error: " + ex.getMessage());
        }
    }

    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount);
        System.out.println("Peashooter damaged: " + amount + " HP left: " + this.health);
    }
}