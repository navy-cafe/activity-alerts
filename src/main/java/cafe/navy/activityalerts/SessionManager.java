package cafe.navy.activityalerts;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.util.datafix.DataFixerOptimizationOption;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    private final @NonNull Map<UUID, Session> sessions;
    private final @NonNull JavaPlugin plugin;
    private final @NonNull MiniMessage miniMessage;

    public SessionManager(final @NonNull JavaPlugin plugin) {
        this.sessions = new HashMap<>();
        this.miniMessage = MiniMessage.miniMessage();
        this.plugin = plugin;
    }

    public void createSession(final @NonNull UUID uuid) {
        final Session session = new Session(uuid, this.plugin, Instant.now());
        this.sessions.put(uuid, session);
    }

    public void endSession(final @NonNull UUID uuid) {
        this.sessions.remove(uuid);
    }

    public @NonNull Optional<@NonNull Session> getSession(final @NonNull UUID uuid) {
        return Optional.ofNullable(this.sessions.get(uuid));
    }

}
