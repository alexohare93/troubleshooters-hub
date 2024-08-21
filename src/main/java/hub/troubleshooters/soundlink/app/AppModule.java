package hub.troubleshooters.soundlink.app;

import com.google.inject.AbstractModule;
import hub.troubleshooters.soundlink.core.auth.LoginService;
import hub.troubleshooters.soundlink.core.auth.LoginServiceImpl;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LoginService.class).to(LoginServiceImpl.class);
    }
}
