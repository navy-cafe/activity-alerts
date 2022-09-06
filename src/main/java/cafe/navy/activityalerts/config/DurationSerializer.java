package cafe.navy.activityalerts.config;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationSerializer implements TypeSerializer<Duration> {

    @Override
    public Duration deserialize(Type type, ConfigurationNode node) throws SerializationException {
        int millis = this.parseValue(node, "milliseconds", "millis", "ms");
        int seconds = this.parseValue(node, "seconds", "secs", "s");
        int minutes = this.parseValue(node, "minutes", "mins", "m");
        int hours = this.parseValue(node, "hours", "h");
        int days = this.parseValue(node, "days", "d");

        return Duration.of(days, ChronoUnit.DAYS)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds)
                .plusMillis(millis);
    }

    @Override
    public void serialize(Type type, @Nullable Duration obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            throw new RuntimeException("impossible");
        }

        final int millis = obj.toMillisPart();
        final int seconds = obj.toSecondsPart();
        final int minutes = obj.toMinutesPart();
        final int hours = obj.toHoursPart();
        final long days = obj.toDaysPart();

        node.node("millis").set(millis);
        node.node("seconds").set(seconds);
        node.node("minutes").set(minutes);
        node.node("hours").set(hours);
        node.node("days").set(days);
    }

    private int parseValue(final @NonNull ConfigurationNode node,
                           final @NonNull String... aliases) {
        for (final String alias : aliases) {
            if (node.hasChild(alias)) {
                return node.node(alias).getInt(0);
            }
        }

        return 0;
    }
}
