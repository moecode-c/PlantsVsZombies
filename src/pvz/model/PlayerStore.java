package pvz.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Minimal in-memory store: only username/password auth, no persistence.
public class PlayerStore {
    private final Map<String, Player> users = new HashMap<>();
    private Player current;

    public synchronized boolean createAccount(String username, String password) {
        if (username == null || username.isBlank()) return false;
        if (users.containsKey(username)) return false; // duplicate
        users.put(username, new Player(username, password));
        return true;
    }

    public synchronized boolean signIn(String username, String password) {
        Player p = users.get(username);
        if (p != null && p.passwordMatches(password)) {
            current = p;
            return true;
        }
        return false;
    }

    public synchronized Optional<Player> getCurrentPlayer() { return Optional.ofNullable(current); }
    public synchronized void signOut() { current = null; }

    // Compatibility no-ops (previous code called these)
    public void load() { }
    public void save() { }
}
