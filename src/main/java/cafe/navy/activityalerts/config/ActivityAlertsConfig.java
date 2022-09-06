package cafe.navy.activityalerts.config;

import cafe.navy.activityalerts.alert.Alert;
import cafe.navy.activityalerts.alert.ChatAlert;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class ActivityAlertsConfig {

    public @NonNull List<@NonNull Alert> alerts = new ArrayList<>();
    public @NonNull List<@NonNull AlertTimes> times = new ArrayList<>();

}
