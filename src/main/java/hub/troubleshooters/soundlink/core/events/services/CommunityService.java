package hub.troubleshooters.soundlink.core.events.services;

import hub.troubleshooters.soundlink.core.events.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.Community;
import java.util.List;

public interface CommunityService {
    void createCommunity(CreateCommunityModel model);
    List<Community> searchCommunities(String genre, String location);
}
