package cafe.navy.activityalerts.session;

import cafe.navy.activityalerts.alert.Alert;
import cafe.navy.bedrock.core.util.NumberUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final @NonNull UUID uuid;
    private final @NonNull Map<Alert, BukkitTask> alerts;
    private final @NonNull JavaPlugin plugin;
    private final @NonNull Instant start;
    private final @NonNull SessionManager manager;

    public Session(final @NonNull UUID uuid,
                   final @NonNull JavaPlugin plugin,
                   final @NonNull Instant start,
                   final @NonNull SessionManager manager) {
        this.uuid = uuid;
        this.plugin = plugin;
        this.start = start;
        this.manager = manager;
        this.alerts = new HashMap<>();
    }

    public @NonNull UUID uuid() {
        return this.uuid;
    }

    public @NonNull Duration playtime() {
        return Duration.between(this.start, Instant.now());
    }

    public void addAlert(final @NonNull Alert alert,
                         final @NonNull Duration min,
                         final @NonNull Duration max) {
        final long ticks = (min.equals(max) ? min.toSeconds() : NumberUtil.between(min.toSeconds(), max.toSeconds())) * 20L;
        final Session ref = this;
        final BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                alert.send(ref);
                alerts.remove(alert);
            }
        }.runTaskLater(this.plugin, ticks);
        this.alerts.put(alert, task);
    }

    public void end() {
        for (final BukkitTask task : this.alerts.values()) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }

        this.alerts.clear();
    }

}
