package cafe.navy.activityalerts;

import cafe.navy.bedrock.paper.core.message.Colours;
import cafe.navy.bedrock.paper.core.message.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

public class ChatAlert implements Alert {

    private final @NonNull List<String> template;
    private final @NonNull Duration minDelay;
    private final @NonNull Duration maxDelay;
    private final @NonNull MiniMessage miniMessage;

    private ChatAlert(final @NonNull List<String> template,
                      final @NonNull Duration minDelay,
                      final @NonNull Duration maxDelay,
                      final @NonNull MiniMessage miniMessage) {
        this.template = template;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.miniMessage = miniMessage;
    }

    public @NonNull List<@NonNull String> template() {
        return List.copyOf(this.template);
    }

    public @NonNull Duration minDelay() {
        return this.minDelay;
    }

    public @NonNull Duration maxDelay() {
        return this.maxDelay;
    }

    public void send(final @NonNull Session session) {
        final @Nullable Player player = Bukkit.getPlayer(session.uuid());
        if (player == null) {
            throw new RuntimeException("Tried to send a ChatAlert to offline player " + session.uuid() + "!");
        }

        final Message message = Message.create();

        final Iterator<String> it = this.template.iterator();
        while (it.hasNext()) {
            final String template = it.next();
            final String name = player.getName();
            final String time = DurationFormatUtils.formatDurationWords(
                    session.playtime().toMillis(),
                    true,
                    true
            );

            message.text(this.miniMessage.deserialize(
                    template, TagResolver.builder()
                            .tag("name", Tag.preProcessParsed(name))
                            .tag("time", Tag.preProcessParsed(time))
                            .build()
            ));

            if (it.hasNext()) {
                message.newline();
            }
        }

        player.sendMessage(message);
    }

}
