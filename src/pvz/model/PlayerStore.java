package pvz.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Simple file-backed user store.
 *
 * Storage format (very small and easy to read for this demo):
 * - data/users.txt    -> one user per line: username\tpassword
 * - data/session.txt  -> a single line with the username of the signed-in user
 *
 * Notes
 * - This is intentionally simple/plain-text for learning purposes. Don't store
 *   real passwords like this in production. Use hashing + salting and proper
 *   credential storage instead.
 */
public class PlayerStore {
    // Where we keep the tiny text files for this demo (relative to app working dir)
    private static final Path DATA_DIR = Path.of("data");
    private static final Path USERS_FILE = DATA_DIR.resolve("users.txt");
    private static final Path SESSION_FILE = DATA_DIR.resolve("session.txt");

    /** All registered users for this run (keyed by username). */
    private final Map<String, Player> users = new HashMap<>();
    /** The user who most recently signed in (null if none). */
    private Player current;

    /** Ensure the data directory exists. */
    private static void ensureDataDir() {
        try { Files.createDirectories(DATA_DIR); } catch (IOException ignored) { }
    }

    /**
     * Create a new account and append it to users.txt.
     * @return true if created; false if username is blank or already exists.
     */
    public synchronized boolean createAccount(String username, String password) {
        if (username == null || username.isBlank()) return false;
        // Load from disk if needed so duplicate checks are accurate
        load();
        if (users.containsKey(username)) return false; // duplicate

        Player p = new Player(username, password);
        users.put(username, p);
        passwords.put(username, password);

        // Append to file (create directory/file if they don't exist)
        ensureDataDir();
        String line = username + "\t" + password + System.lineSeparator();
        try {
            Files.writeString(
                    USERS_FILE,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            // If we can't persist, roll back the in-memory add for consistency
            users.remove(username);
            return false;
        }
        return true;
    }

    /**
     * Attempt to sign in; on success writes session.txt with the username.
     * @return true on success and sets the current user.
     */
    public synchronized boolean signIn(String username, String password) {
        load();
        Player p = users.get(username);
        if (p != null && p.passwordMatches(password)) {
            current = p;
            writeSession(username);
            return true;
        }
        return false;
    }

    /** @return the currently signed-in player, if any. */
    public synchronized Optional<Player> getCurrentPlayer() { return Optional.ofNullable(current); }

    /**
     * Sign out the current user; clears session.txt if present.
     */
    public synchronized void signOut() {
        current = null;
        try { Files.deleteIfExists(SESSION_FILE); } catch (IOException ignored) { }
    }

    /**
     * Delete an account (requires matching username+password).
     * - Removes the user line from users.txt by rewriting the file.
     * - If the deleted user was signed in, this also signs out.
     * @return true if a user was deleted; false if credentials didn't match.
     */
    public synchronized boolean deleteAccount(String username, String password) {
        load();
        Player p = users.get(username);
        if (p == null || !p.passwordMatches(password)) return false;

        // Remove from memory
        users.remove(username);
        passwords.remove(username);

        // Rewrite users.txt without this user
        ensureDataDir();
        try {
            List<String> lines = users.values().stream()
                    .map(u -> u.getUsername() + "\t" + /* demo, plain text */ getPasswordFor(u))
                    .collect(Collectors.toList());
            Files.write(USERS_FILE, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            // If rewrite fails, reload from disk to avoid diverging state
            reloadFromDisk();
            return false;
        }

        // If that user was logged in, sign them out
        if (current != null && username.equals(current.getUsername())) {
            signOut();
        }
        return true;
    }

    // -------- Persistence helpers (tiny and explicit for readability) --------

    /** Load users and session from disk into memory (idempotent and cheap). */
    public synchronized void load() { reloadFromDisk(); }

    /** Save all users back to users.txt and current session (if any). */
    public synchronized void save() {
        ensureDataDir();
        try {
            List<String> lines = users.values().stream()
                    .map(u -> u.getUsername() + "\t" + getPasswordFor(u))
                    .collect(Collectors.toList());
            Files.write(USERS_FILE, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException ignored) { }
        if (current != null) writeSession(current.getUsername());
    }

    /** Read users.txt and session.txt freshly. */
    private void reloadFromDisk() {
        users.clear();
        passwords.clear();
        // Users
        if (Files.exists(USERS_FILE)) {
            try {
                for (String line : Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8)) {
                    if (line.isBlank()) continue;
                    int tab = line.indexOf('\t');
                    String u = tab >= 0 ? line.substring(0, tab) : line;
                    String pw = tab >= 0 ? line.substring(tab + 1) : "";
                    users.put(u, new Player(u, pw));
                    passwords.put(u, pw);
                }
            } catch (IOException ignored) { }
        }
        // Session
        current = null;
        if (Files.exists(SESSION_FILE)) {
            try {
                List<String> lines = Files.readAllLines(SESSION_FILE, StandardCharsets.UTF_8);
                if (!lines.isEmpty()) {
                    String u = lines.get(0).trim();
                    Player p = users.get(u);
                    if (p != null) current = p; // only set if user still exists
                }
            } catch (IOException ignored) { }
        }
    }

    /** Write session.txt containing just the username. */
    private void writeSession(String username) {
        ensureDataDir();
        try {
            Files.writeString(SESSION_FILE, username + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ignored) { }
    }

    /**
     * Demo helper: return the plain-text password we keep alongside the user
     * so we can rewrite users.txt. (Never do this in a real app.)
     */
    private String getPasswordFor(Player u) { return passwords.getOrDefault(u.getUsername(), ""); }

    // Mirror of plain-text passwords keyed by username for persistence only (demo).
    private final Map<String, String> passwords = new HashMap<>();
}
