package cafe.navy.activityalerts;

import cafe.navy.bedrock.paper.core.message.Colours;
import cafe.navy.bedrock.paper.core.message.Message;
import cafe.navy.bedrock.paper.core.message.Messages;
import cloud.commandframework.Command;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

/**
 * {@code ActivityAlertsPlugin} is the entrypoint for Bukkit.
 */
public class ActivityAlertsPlugin extends JavaPlugin implements Listener {

    private final @NonNull SessionManager sessionManager;
    private @MonotonicNonNull PaperCommandManager<CommandSender> manager;

    private ActivityAlertsPlugin() {
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
                    sender.sendMessage(Messages.createVersionMessage(
                            Message.create().gradient("ActivityAlerts", Colours.Dark.RED, Colours.Light.RED),
                            Message.create().highlight("1.0.0"),
                            Message.create().main("Get support in the ")
                                    .link("navy.cafe Discord", "https://chat.navy.cafe"),
                            Message.create().link("bluely", "kaden.sh")
                    ));
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


                })
        );
    }


}
