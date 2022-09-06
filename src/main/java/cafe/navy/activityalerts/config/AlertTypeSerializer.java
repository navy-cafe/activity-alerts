package cafe.navy.activityalerts.config;

import cafe.navy.activityalerts.alert.Alert;
import cafe.navy.activityalerts.alert.ChatAlert;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class AlertTypeSerializer implements TypeSerializer<Alert> {

    @Override
    public @NonNull Alert deserialize(final @NonNull Type type,
                                      final @NonNull ConfigurationNode node) throws SerializationException {
        if (!node.hasChild("type") || node.node("type").getString("chat").equals("chat")) {
            final List<String> template = Objects.requireNonNull(node.node("template").getList(String.class));

            return ChatAlert.of(template);
        }

        throw new RuntimeException("Unable to deserialize type");
    }

    @Override
    public void serialize(final @NonNull Type type,
                          final @Nullable Alert obj,
                          final @NonNull ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            throw new RuntimeException("unable to  serialzie null");
        }

        if (obj instanceof ChatAlert alert) {
            final List<String> template = alert.template();
            node.node("template").setList(String.class, template);
            return;
        }

        throw new RuntimeException("Unable to serialize type " + type);
    }

}
