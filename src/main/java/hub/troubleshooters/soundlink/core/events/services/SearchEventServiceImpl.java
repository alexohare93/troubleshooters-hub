package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.data.factories.SearchEventFactory;
import hub.troubleshooters.soundlink.data.models.SearchEvent;
import java.util.List;
import com.google.inject.Inject;
import java.sql.SQLException;
import java.util.Optional;

public class SearchEventServiceImpl implements SearchEventService{

    private final SearchEventFactory searchEventFactory;

    @Inject
    public SearchEventServiceImpl(SearchEventFactory searchEventFactory) {
        this.searchEventFactory = searchEventFactory;
    }

    @Override
    public List<SearchEvent> getUserCommunityEvents(int userId) throws SQLException {
        // Implementation to fetch events where the user is a member
        return searchEventFactory.findUserCommunityEvents(userId);
    }

    @Override
    public List<SearchEvent> getPublicCommunityEvents(int userId) throws SQLException {
        // Implementation to fetch public events where the user is not a member
        return searchEventFactory.findPublicCommunityEvents(userId);
    }
}
