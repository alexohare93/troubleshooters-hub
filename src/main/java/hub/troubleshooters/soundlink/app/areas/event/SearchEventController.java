package hub.troubleshooters.soundlink.app.areas.event;

import hub.troubleshooters.soundlink.data.factories.SearchEventFactory;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.auth.UserContext;
import com.google.inject.Inject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;

public class SearchEventController {

    private final SearchEventFactory searchEventFactory;
    private final IdentityService identityService;

    @Inject
    public SearchEventController(SearchEventFactory searchEventFactory, IdentityService identityService) {
        this.searchEventFactory = searchEventFactory;
        this.identityService = identityService;
    }

    /**
     * List all upcoming events for the user, including:
     * - Events from communities the user is a member of
     * - Public events from communities the user is not a member of
     * 
     * @return A combined list of upcoming events
     * @throws SQLException if a database error occurs
     */
    public List<SearchEvent> listUpcomingEvents() throws SQLException {
        int userId = identityService.getUserContext().getUser().getId();
        
        // Fetch events from communities the user is a member of
        List<SearchEvent> userCommunityEvents = searchEventFactory.findUserCommunityEvents(userId);
        
        // Fetch public events from communities the user is not a member of
        List<SearchEvent> publicEvents = searchEventFactory.findPublicCommunityEvents(userId);
        
        // Combine both lists
        List<SearchEvent> combinedEvents = new ArrayList<>(userCommunityEvents);
        combinedEvents.addAll(publicEvents);

        return combinedEvents;
    }
}
