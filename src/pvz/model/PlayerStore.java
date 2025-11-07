package pvz.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Minimal in-memory user store.
 * - Keeps users in a HashMap for this session only (no file/database persistence).
 * - Supports createAccount, signIn, signOut and querying current user.
 */
public class PlayerStore {
    /** All registered users for this run (keyed by username). */
    private final Map<String, Player> users = new HashMap<>();
    /** The user who most recently signed in (null if none). */
    private Player current;

    /**
     * Create a new account.
     * @return true if created; false if username is blank or already exists.
     */
    public synchronized boolean createAccount(String username, String password) {
        if (username == null || username.isBlank()) return false;
        if (users.containsKey(username)) return false; // duplicate
        users.put(username, new Player(username, password));
        return true;
    }

    /**
     * Attempt to sign in.
     * @return true on success and sets the current user.
     */
    public synchronized boolean signIn(String username, String password) {
        Player p = users.get(username);
        if (p != null && p.passwordMatches(password)) {
            current = p;
            return true;
        }
        return false;
    }

    /** @return the currently signed-in player, if any. */
    public synchronized Optional<Player> getCurrentPlayer() { return Optional.ofNullable(current); }
    /** Clears the current user. */
    public synchronized void signOut() { current = null; }

    // Compatibility no-ops (kept so callers don't break if we add persistence later)
    public void load() { }
    public void save() { }
}
