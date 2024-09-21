package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.data.models.SearchEvent;

import java.util.List;
import java.sql.SQLException;

public interface SearchEventService {
    List<SearchEvent> getUserCommunityEvents(int userId) throws SQLException;

    List<SearchEvent> getPublicCommunityEvents(int userId) throws SQLException;
}
