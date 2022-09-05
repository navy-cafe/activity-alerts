package cafe.navy.activityalerts;

import com.google.j2objc.annotations.ObjectiveCName;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;

public interface Alert {

    @NonNull Duration minDelay();

    @NonNull Duration maxDelay();

    void send(final @NonNull Session player);

}
