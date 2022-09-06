package cafe.navy.activityalerts.alert;

import cafe.navy.activityalerts.session.Session;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.LightningStrike;
import org.checkerframework.checker.nullness.qual.NonNull;
import oshi.util.tuples.Pair;

import java.time.Duration;
import java.util.List;

public interface Alert {

    void send(final @NonNull Session player);

}
