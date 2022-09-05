package cafe.navy.activityalerts;

import cafe.navy.bedrock.paper.core.util.NumberUtil;
import com.sun.jna.platform.win32.Netapi32Util;
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
    private final @NonNull Map<ChatAlert, BukkitTask> alerts;
    private final @NonNull JavaPlugin plugin;
    private final @NonNull Instant start;

    public Session(final @NonNull UUID uuid,
                   final @NonNull JavaPlugin plugin,
                   final @NonNull Instant start) {
        this.uuid = uuid;
        this.plugin = plugin;
        this.start = start;
        this.alerts = new HashMap<>();
    }

    public @NonNull UUID uuid() {
        return this.uuid;
    }

    public @NonNull Duration playtime() {
        return Duration.between(this.start, Instant.now());
    }

    public void addAlert(final @NonNull ChatAlert alert,
                         final @NonNull Duration minDuration,
                         final @NonNull Duration maxDuration) {
        final long millis = NumberUtil.between(minDuration.toMillis(), maxDuration.toMillis()) * 20L;
        final Session ref = this;
        final BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                alert.send(ref);
                alerts.remove(alert);
            }
        }.runTaskLater(this.plugin, millis);
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
