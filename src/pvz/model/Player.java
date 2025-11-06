package pvz.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

// Minimal player: only username and password, no stars/progress.
public class Player implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private final String username;
    private String password; // demo only

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public boolean passwordMatches(String pw) { return Objects.equals(this.password, pw); }
    public void setPassword(String pw) { this.password = pw; }
}
