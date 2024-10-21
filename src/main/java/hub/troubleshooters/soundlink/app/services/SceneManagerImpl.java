package hub.troubleshooters.soundlink.app.services;

import hub.troubleshooters.soundlink.app.SoundLinkApplication;
import hub.troubleshooters.soundlink.app.areas.Routes;
import hub.troubleshooters.soundlink.app.areas.admin.AdminController;
import hub.troubleshooters.soundlink.app.areas.communities.CommunityFeedController;
import hub.troubleshooters.soundlink.app.areas.events.EventDetailsController;
import hub.troubleshooters.soundlink.app.areas.communities.CommunityDetailsController;
import hub.troubleshooters.soundlink.app.areas.notification.NotificationController;
import hub.troubleshooters.soundlink.app.areas.shared.SharedController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class SceneManagerImpl implements SceneManager {
    private final Stage primaryStage;
    private final Injector injector;

    private Parent root = null;
    private SharedController sharedController = null;

    private final int STAGE_WIDTH = 1152;
    private final int STAGE_HEIGHT = 900;

    private Stack<String> backStack = new Stack<>();
    private Stack<String> forwardStack = new Stack<>();
    private String currentRoute = Routes.LOGIN;  // program starts at the login route

    private final List<NavigationListener> listeners = new ArrayList<>();

    @Inject
    public SceneManagerImpl(Stage primaryStage, Injector injector) {
        this.primaryStage = primaryStage;
        this.injector = injector;
    }

    @Override
    public void switchToScene(String fxmlFileName, String sceneName, int width, int height) {
        try {
            var loader = new FXMLLoader(SoundLinkApplication.class.getResource(fxmlFileName));
            loader.setControllerFactory(injector::getInstance);
            root = loader.load();
            var scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/hub/troubleshooters/soundlink/app/areas/common.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle(sceneName);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);

            // remove all history + future since this is total context change
            currentRoute = sceneName;
            backStack = new Stack<>();
            forwardStack = new Stack<>();

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // TODO: replace with proper error handling
        }
    }

    @Override
    public void switchToScene(String fxmlFileName) {
        switchToScene(fxmlFileName, "SoundLink", 768, 540);     // 768x540 is login stage's size
    }

    @Override
    public void switchToOutletScene(String fxmlFileName) {
        switchToOutletScene(fxmlFileName, controller -> {
            // No additional logic for the controller
        });
    }

    @Override
    public <T> void switchToOutletScene(String fxmlFileName, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(SoundLinkApplication.class.getResource(fxmlFileName));
            loader.setControllerFactory(injector::getInstance);
            Parent outletContent = loader.load();

            if (sharedController == null || root == null || !Objects.equals(root.getId(), "root")) {
                initializeSharedView();
            }

            sharedController.setOutlet(outletContent);
            currentRoute = fxmlFileName;

            T controller = loader.getController();
            controllerConsumer.accept(controller);

        } catch (IOException e) {
            e.printStackTrace();  // TODO: replace with proper error handling
        }
    }

    @Override
    public void alert(Alert alert) {
        alert.showAndWait();
    }

    @Override
    public void navigate(String newRoute) {
        if (!newRoute.equals(currentRoute)) {
            backStack.push(currentRoute);
            forwardStack.clear();       // clear forward stack as we're navigating somewhere new
            currentRoute = newRoute;
            switchToOutletScene(currentRoute);
            notifyListeners();
        }
    }

    @Override
    public void navigateBack() {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentRoute);
            currentRoute = backStack.pop();
            switchToOutletScene(currentRoute);
            notifyListeners();
        }
    }

    @Override
    public void navigateForward() {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentRoute);
            currentRoute = forwardStack.pop();
            switchToOutletScene(currentRoute);
            notifyListeners();
        }
    }

    @Override
    public boolean hasHistory() {
        return !backStack.isEmpty();
    }

    @Override
    public boolean hasFuture() {
        return !forwardStack.isEmpty();
    }

    @Override
    public void addNavigationListener(NavigationListener listener) {
        listeners.add(listener);
    }

    @Override
    public File openFileDialog() {
        var fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(primaryStage);
    }

    // TODO: this doesn't actually use navigation; need to change the stack objects to include parameter Object info
    @Override
    public void navigateToEventDetailsView(int eventId) {
        switchToOutletScene(Routes.EVENT_DETAILS, (EventDetailsController controller) -> {
            controller.loadEventDetails(eventId);  // pass the eventId to the controller
        });
    }

    @Override
    public void navigateToCommunityDetailsView(int communityId) {
        switchToOutletScene(Routes.COMMUNITY_DETAILS, (CommunityDetailsController controller) -> {
            controller.loadCommunityDetails(communityId);
        });
    }

    @Override
    public void navigateToCommunityFeedView(int communityId) {
        switchToOutletScene(Routes.COMMUNITY_FEED, (CommunityFeedController controller) -> {
            controller.LoadFeed(communityId);
        });
    }

    @Override
    public void onNotificationButtonClick(int communityId) {
        switchToOutletScene(Routes.ADMIN, (AdminController controller) -> {
            controller.initializeWithCommunityId(communityId);
        });
    }

    // Notify all listeners about a state change
    private void notifyListeners() {
        for (NavigationListener listener : listeners) {
            listener.onNavigationStateChange();
        }
    }

    private void initializeSharedView() {
        try {
            var loader = new FXMLLoader(SoundLinkApplication.class.getResource("areas/shared/shared_view.fxml"));
            loader.setControllerFactory(injector::getInstance);
            root = loader.load();
            sharedController = loader.getController();
            var scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/hub/troubleshooters/soundlink/app/areas/common.css").toExternalForm());
            primaryStage.setScene(scene);
            // first time changing to the shared view will resize the window.
            primaryStage.setWidth(STAGE_WIDTH);
            primaryStage.setHeight(STAGE_HEIGHT);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // TODO: replace with proper error handling
        }
    }

    @Override
    public void navigateToSearchCommunityView() {
        navigate(Routes.SEARCH_COMMUNITIES);
    }

    @Override
    public void navigateToSearchEventView() {
        navigate(Routes.SEARCH_EVENTS);
    }

}
