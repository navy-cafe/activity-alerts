package cafe.navy.activityalerts.alert;

import cafe.navy.activityalerts.session.Session;
import cafe.navy.bedrock.core.message.Message;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.List;

public class ChatAlert implements Alert {

    public static @NonNull ChatAlert of(final @NonNull List<String> template) {
        return new ChatAlert(template);
    }

    private final @NonNull List<String> template;

    private ChatAlert(final @NonNull List<String> template) {
        this.template = template;
    }

    public @NonNull List<@NonNull String> template() {
        return List.copyOf(this.template);
    }

    public void send(final @NonNull Session session) {
        final @Nullable Player player = Bukkit.getPlayer(session.uuid());
        if (player == null) {
            throw new RuntimeException("Tried to send a ChatAlert to offline player " + session.uuid() + "!");
        }

        final String name = player.getName();
        final String time = DurationFormatUtils.formatDurationWords(
                session.playtime().toMillis(),
                true,
                true
        );

        final Message message = Message.create()
                .tag("name", Tag.preProcessParsed(name))
                .tag("time", Tag.preProcessParsed(time));

        final Iterator<String> it = this.template.iterator();
        while (it.hasNext()) {
            final String template = it.next();

            message.text(template);

            if (it.hasNext()) {
                message.newline();
            }
        }

        player.sendMessage(message);
    }

}
