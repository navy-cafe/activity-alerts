package cafe.navy.activityalerts.alert;

import cafe.navy.activityalerts.session.Session;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Alert {

    void send(final @NonNull Session player);

}
