package hub.troubleshooters.soundlink.app;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import hub.troubleshooters.soundlink.core.auth.LoginService;
import hub.troubleshooters.soundlink.core.auth.LoginServiceImpl;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.SQLiteDatabaseConnection;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LoginService.class).to(LoginServiceImpl.class);
        bind(DatabaseConnection.class).to(SQLiteDatabaseConnection.class);
    }

    @Provides
    SQLiteDatabaseConnection provideSQLiteDatabaseConnection() {
        String connectionString = "jdbc:sqlite:database.db";
        return new SQLiteDatabaseConnection(connectionString);
    }
}
