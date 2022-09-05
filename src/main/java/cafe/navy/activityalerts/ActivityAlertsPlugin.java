package cafe.navy.activityalerts;

import cafe.navy.bedrock.paper.core.message.Colours;
import cafe.navy.bedrock.paper.core.message.Message;
import cafe.navy.bedrock.paper.core.message.Messages;
import cloud.commandframework.Command;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * {@code ActivityAlertsPlugin} is the entrypoint for Bukkit.
 */
public class ActivityAlertsPlugin extends JavaPlugin implements Listener {

    private final @NonNull SessionManager sessionManager;
    private @MonotonicNonNull PaperCommandManager<CommandSender> manager;

    public ActivityAlertsPlugin() {
        this.sessionManager = new SessionManager(this);
    }

    public @NonNull SessionManager sessionManager() {
        return this.sessionManager;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.setupCommands();
    }

    @EventHandler
    private void onJoin(final @NonNull PlayerJoinEvent event) {
        this.sessionManager.createSession(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onQuit(final @NonNull PlayerQuitEvent event) {
        this.sessionManager.endSession(event.getPlayer().getUniqueId());
    }

    private void setupCommands() {
        try {
            this.manager = new PaperCommandManager<>(
                    this,
                    AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final Command.Builder<CommandSender> alertsCommand = this.manager
                .commandBuilder("alerts", "activityalerts");

        this.manager.command(alertsCommand
                .literal("version")
                .handler(ctx -> {
                    final CommandSender sender = ctx.getSender();
                    sender.sendMessage(Message.create()
                            .newline()
                            .text(Messages.createVersionMessage(
                                    Message.create().gradient("ActivityAlerts", Colours.Dark.RED, Colours.Light.RED, TextDecoration.BOLD, TextDecoration.ITALIC),
                                    Message.create().text("1.0.0", Colours.Light.GREEN, TextDecoration.BOLD),
                                    Message.create().main("Get support in the ")
                                            .link("navy.cafe Discord", "https://chat.navy.cafe")
                                            .asComponent()
                                            .decorate(TextDecoration.ITALIC),
                                    Message.create().link("bluely", "kaden.sh")
                            ))
                            .newline());
                }));

        this.manager.command(this.manager
                .commandBuilder("playtime")
                .handler(ctx -> {
                    final CommandSender sender = ctx.getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Messages
                                .createError()
                                .main("You must be a player to use this command."));
                        return;
                    }

                    final Optional<Session> opt = this.sessionManager.getSession(player.getUniqueId());
                    if (opt.isEmpty()) {
                        sender.sendMessage(Messages
                                .createError()
                                .main("No session was found."));
                        return;
                    }

                    final Session session = opt.get();
                    final String time = DurationFormatUtils.formatDurationWords(
                            session.playtime().toMillis(),
                            true,
                            true
                    );

                    sender.sendMessage(Message
                            .create()
                            .main("You have played for ")
                            .highlight(time)
                            .main(" during this session."));
                })
        );
    }


}
