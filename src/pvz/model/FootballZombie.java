
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class DefaultZombie extends Zombie
{
    public DefaultZombie()
    {
        super(10, 0.4, 100);
        elementImage=new ImageView(new Image("images/zombies/defaultZombie.gif"));
        elementImage.setFitHeight(155);
        elementImage.setFitWidth(134);
        elementImage.setPreserveRatio(true);

    }

    public DefaultZombie(int x, int y)
    {
        this();
        super.x = x;
        super.y = y;
    }
}
