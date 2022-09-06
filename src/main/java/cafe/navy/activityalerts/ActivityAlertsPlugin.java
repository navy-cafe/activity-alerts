package cafe.navy.activityalerts;

import cafe.navy.activityalerts.alert.Alert;
import cafe.navy.activityalerts.config.ActivityAlertsConfig;
import cafe.navy.activityalerts.config.AlertTimes;
import cafe.navy.activityalerts.config.AlertTypeSerializer;
import cafe.navy.activityalerts.config.DurationSerializer;
import cafe.navy.activityalerts.session.Session;
import cafe.navy.activityalerts.session.SessionManager;
import cafe.navy.bedrock.core.message.Colours;
import cafe.navy.bedrock.core.message.Message;
import cafe.navy.bedrock.core.message.Messages;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@code ActivityAlertsPlugin} is the entrypoint for Bukkit.
 */
public class ActivityAlertsPlugin extends JavaPlugin implements Listener {

    private static final @NonNull Message PLUGIN_PREFIX = Message.create()
            .gradient("ActivityAlerts",
                    TextColor.fromCSSHexString("#cf483e"),
                    TextColor.fromCSSHexString("#e08c1d"),
                    TextDecoration.BOLD);
    private final @NonNull SessionManager sessionManager;
    private @MonotonicNonNull PaperCommandManager<CommandSender> commandManager;
    private @MonotonicNonNull HoconConfigurationLoader configLoader;

    public ActivityAlertsPlugin() {
        this.sessionManager = new SessionManager(this);
    }

    public @NonNull SessionManager sessionManager() {
        return this.sessionManager;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.setupConfig();
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

    private void setupConfig() {
        final var durationType = new DurationSerializer();
        final var alertType = new AlertTypeSerializer();

        this.configLoader = HoconConfigurationLoader.builder().prettyPrinting(true).emitComments(true).file(new File(this.getDataFolder(), "alerts.conf")).build();

        try {
            final ConfigurationNode root = this.configLoader.load(ConfigurationOptions.defaults().serializers(serializers -> {
                serializers.register(Duration.class, durationType);
                serializers.register(Alert.class, alertType);
            }));
            final ActivityAlertsConfig config = root.get(ActivityAlertsConfig.class);

            if (config == null) {
                throw new RuntimeException("Config could not load");
            }

            for (final Alert alert : config.alerts) {
                this.sessionManager.registerAlert(alert);
            }

            for (final AlertTimes times : config.times) {
                this.sessionManager.addTimes(times);
            }

            root.set(config);
            this.configLoader.save(root);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

    }

    private void setupCommands() {
        try {
            this.commandManager = new PaperCommandManager<>(this, AsynchronousCommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final Command.Builder<CommandSender> alertsCommand = this.commandManager.commandBuilder("activity", "activityalerts");
        final Command.Builder<CommandSender> playtimeCommand = this.commandManager.commandBuilder("playtime");

        this.commandManager.command(alertsCommand
                .literal("version")
                .handler(this::handleAlertsVersion));

        this.commandManager.command(alertsCommand
                .argument(IntegerArgument.optional("page"))
                .handler(this::handleAlertsHelp));

        this.commandManager.command(alertsCommand
                .literal("help")
                .argument(IntegerArgument.optional("page"))
                .handler(this::handleAlertsHelp));

        this.commandManager.command(playtimeCommand
                .handler(this::handleAlertsPlaytime));
    }

    private void handleAlertsHelp(final @NonNull CommandContext<CommandSender> ctx) {
        final CommandSender sender = ctx.getSender();

        final int page = ctx.getOrDefault("page", 1);
        final int pages = 5;

        if (page == 0 || page == 1) {
            sender.sendMessage(Message.create()
                    // | ActivityAlerts help (1/5)
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .newline()
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .text(PLUGIN_PREFIX)
                    .main(" help ")
                    .text("(", Colours.Tones.DARK_GRAY)
                    .text(Integer.toString(page), Colours.Light.BLUE)
                    .text("/", Colours.Tones.DARK_GRAY)
                    .text(Integer.toString(pages), Colours.Light.BLUE)
                    .text(")", Colours.Tones.DARK_GRAY)
                    .newline()
                    // |
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .newline()
                    // | /activity
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .command(Component.text("/activity", Colours.Light.RED).decoration(TextDecoration.UNDERLINED, false), "/activity")
                    // |
                    .newline()
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .main("  Help information.")
                    .newline()
                    // | /activity version
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .command(Component.text("/activity version", Colours.Light.RED).decoration(TextDecoration.UNDERLINED, false), "/activity version")
                    // |
                    .newline()
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .main("  Plugin version information.")
                    .newline()
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .command(Component.text("/playtime", Colours.Light.RED).decoration(TextDecoration.UNDERLINED, false), "/playtime")
                    // |
                    .newline()
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .main("  Returns your current playtime.")
                    .newline()
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .newline()
                    .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                    .command(Component.text("   ◁◁◁", page == 0 || page == 1 ? Colours.Tones.DARK_GRAY : Colours.Light.GREEN)
                            .decoration(TextDecoration.ITALIC, false), "/activity help " + (page - 1))
                    .text("   ")
                    .command(Component.text("   ▷▷▷", page == pages ? Colours.Tones.DARK_GRAY : Colours.Light.ORANGE)
                            .decoration(TextDecoration.ITALIC, false), "/activity help " + (page + 1))
            );
        } else {
            sender.sendMessage(Message.create()
                    .error("▎ ")
                    .main("There are no more help pages."));
        }


    }

    private void handleAlertsVersion(final @NonNull CommandContext<CommandSender> ctx) {
        final CommandSender sender = ctx.getSender();
        sender.sendMessage(Message.create()
                .text("▎ ", Colours.Tones.LIGHTER_GRAY)
                .newline()
                .text(Messages.createVersionMessage(
                        PLUGIN_PREFIX,
                        Message.create().text("1.0.0", Colours.Tones.LIGHTER_GRAY, TextDecoration.BOLD),
                        Message.create().text("Need help? ", Colours.Light.YELLOW, TextDecoration.BOLD)
                                .main("Get support in the ")
                                .link(Component.text("navy.cafe Discord", TextColor.fromCSSHexString("#f5b25f")), "https://chat.navy.cafe")
                                .asComponent()
                                .decorate(TextDecoration.ITALIC),
                        Colours.Tones.LIGHTER_GRAY,
                        Message.create().link(Component.text("bluely")
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.UNDERLINED, false), "kaden.sh"))
                )
                .newline()
                .text("▎ ", Colours.Tones.LIGHTER_GRAY));

    }

    private void handleAlertsPlaytime(final @NonNull CommandContext<CommandSender> ctx) {
        final CommandSender sender = ctx.getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.createError().main("You must be a player to use this command."));
            return;
        }

        final Optional<Session> opt = this.sessionManager.getSession(player.getUniqueId());
        if (opt.isEmpty()) {
            sender.sendMessage(Messages.createError().main("No session was found."));
            return;
        }

        final Session session = opt.get();
        final String time = DurationFormatUtils.formatDurationWords(session.playtime().toMillis(), true, true);

        sender.sendMessage(Message.create().main("You have played for ").highlight(time).main(" during this session."));
    }


}
