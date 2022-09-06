package cafe.navy.activityalerts.session;

import cafe.navy.activityalerts.alert.Alert;
import cafe.navy.activityalerts.config.AlertTimes;
import cafe.navy.bedrock.core.util.NumberUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    private final @NonNull List<Alert> alerts;
    private final @NonNull List<AlertTimes> times;

    public SessionManager(final @NonNull JavaPlugin plugin) {
        this.sessions = new HashMap<>();
        this.alerts = new ArrayList<>();
        this.times = new ArrayList<>();
        this.miniMessage = MiniMessage.miniMessage();
        this.plugin = plugin;
    }

    public void createSession(final @NonNull UUID uuid) {
        final Session session = new Session(uuid, this.plugin, Instant.now(), this);
        this.sessions.put(uuid, session);

        for (final AlertTimes time : this.times) {
            session.addAlert(NumberUtil.choice(this.alerts), time.minDelay, time.maxDelay);
        }
    }

    public void endSession(final @NonNull UUID uuid) {
        this.sessions.remove(uuid);
    }

    public void registerAlert(final @NonNull Alert alert) {
        this.alerts.add(alert);
    }

    public void addTimes(final @NonNull AlertTimes times) {
        this.times.add(times);
    }

    public @NonNull Optional<@NonNull Session> getSession(final @NonNull UUID uuid) {
        return Optional.ofNullable(this.sessions.get(uuid));
    }

}
