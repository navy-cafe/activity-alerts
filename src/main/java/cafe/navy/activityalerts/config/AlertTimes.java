package cafe.navy.activityalerts.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;

@ConfigSerializable
public class AlertTimes {

    public @NonNull Duration minDelay = Duration.ofSeconds(5);
    public @NonNull Duration maxDelay = Duration.ofSeconds(10);

}
