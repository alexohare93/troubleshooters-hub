package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.core.events.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.Community;

import java.sql.SQLException;
import java.util.List;

public interface CommunityService {
    ValidationResult createCommunity(CreateCommunityModel model);
    List<Community> searchCommunities(String searchText);
    boolean signUpForCommunity(int userId, int communityId) throws SQLException;
}
