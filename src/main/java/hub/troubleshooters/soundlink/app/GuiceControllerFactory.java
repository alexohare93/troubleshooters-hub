package hub.troubleshooters.soundlink.app;

import com.google.inject.Injector;
import javafx.util.Callback;

/**
 * GuiceControllerFactory is the manager for the Google Guice controller - factory pattern.
 */
public class GuiceControllerFactory implements Callback<Class<?>, Object> {
    private final Injector injector;

    /**
     * Contructs a {@code GuiceControllerFactory} with the given injector.
     * @param injector A {@link Injector} for this controller - factory pattern.
     */
    public GuiceControllerFactory(Injector injector) {
        this.injector = injector;
    }

    /**
     * The call method for returning the instance of the pattern.
     * @param type A {@code Class<?>}.
     * @return The instance of the injector for that type.
     */
    @Override
    public Object call(Class<?> type) {
        return injector.getInstance(type);
    }
}
