package hub.troubleshooters.soundlink.app;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import hub.troubleshooters.soundlink.app.services.SceneManager;
import hub.troubleshooters.soundlink.app.services.SceneManagerImpl;
import hub.troubleshooters.soundlink.core.Map;
import hub.troubleshooters.soundlink.core.communities.services.CommunityService;
import hub.troubleshooters.soundlink.core.communities.services.CommunityServiceImpl;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.auth.services.IdentityServiceImpl;
import hub.troubleshooters.soundlink.core.admin.services.ApprovalServiceImpl;
import hub.troubleshooters.soundlink.core.admin.services.ApprovalService;
import hub.troubleshooters.soundlink.core.auth.services.LoginService;
import hub.troubleshooters.soundlink.core.auth.services.LoginServiceImpl;
import hub.troubleshooters.soundlink.core.auth.validation.LoginModelValidator;
import hub.troubleshooters.soundlink.core.auth.validation.RegisterModelValidator;
import hub.troubleshooters.soundlink.core.events.services.EventService;
import hub.troubleshooters.soundlink.core.events.services.EventServiceImpl;
import hub.troubleshooters.soundlink.core.events.validation.CreateEventModelValidator;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.images.ImageUploaderServiceImpl;
import hub.troubleshooters.soundlink.core.profile.services.UserProfileService;
import hub.troubleshooters.soundlink.core.profile.services.UserProfileServiceImpl;
import hub.troubleshooters.soundlink.core.profile.validation.UserProfileValidator;
import hub.troubleshooters.soundlink.data.factories.*;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.SQLiteDatabaseConnection;
import javafx.stage.Stage;

public class AppModule extends AbstractModule {
    private final Stage primaryStage;

    public AppModule(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    protected void configure() {
        bind(Stage.class).toInstance(primaryStage);
        bind(DatabaseConnection.class).to(SQLiteDatabaseConnection.class).in(Singleton.class);
        bind(SceneManager.class).to(SceneManagerImpl.class).in(Singleton.class);

        // core services
        bind(LoginService.class).to(LoginServiceImpl.class).in(Singleton.class);
        bind(EventService.class).to(EventServiceImpl.class).in(Singleton.class);
        bind(CommunityService.class).to(CommunityServiceImpl.class).in(Singleton.class);
        bind(IdentityService.class).to(IdentityServiceImpl.class).in(Singleton.class);
        bind(ImageUploaderService.class).to(ImageUploaderServiceImpl.class).in(Singleton.class);
        bind(UserProfileService.class).to(UserProfileServiceImpl.class).in(Singleton.class);
        bind(ApprovalService.class).to(ApprovalServiceImpl.class).in(Singleton.class);

        // bind UserDataStore as a singleton
        bind(UserDataStore.class).in(Singleton.class);

        // data factories
        bind(UserFactory.class).in(Singleton.class);
        bind(CommunityFactory.class).in(Singleton.class);
        bind(CommunityPostFactory.class).in(Singleton.class);
        bind(CommunityMemberFactory.class).in(Singleton.class);
        bind(ImageFactory.class).in(Singleton.class);
        bind(UserProfileFactory.class).in(Singleton.class);
        bind(BookingFactory.class).in(Singleton.class);
        bind(CommunityPostFactory.class).in(Singleton.class);
        bind(EventCommentFactory.class).in(Singleton.class);

        // validators
        bind(CreateEventModelValidator.class).in(Singleton.class);
        bind(LoginModelValidator.class).in(Singleton.class);
        bind(RegisterModelValidator.class).in(Singleton.class);
        bind(UserProfileValidator.class).in(Singleton.class);

        // mapper (potentially in future have mapper profiles?)
        bind(Map.class).in(Singleton.class);
    }

    @Provides
    SQLiteDatabaseConnection provideSQLiteDatabaseConnection() {
        String connectionString = "jdbc:sqlite:database.db";
        return new SQLiteDatabaseConnection(connectionString);
    }
}

