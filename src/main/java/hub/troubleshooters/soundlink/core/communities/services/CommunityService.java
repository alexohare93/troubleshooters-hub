package hub.troubleshooters.soundlink.core.communities.services;

import hub.troubleshooters.soundlink.core.communities.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.Community;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CommunityService {
    ValidationResult createCommunity(CreateCommunityModel model);
    List<Community> searchCommunities(String searchText);
    boolean signUpForCommunity(int userId, int communityId) throws SQLException;
    Optional<CommunityModel> getCommunity(int id);
    boolean hasUserJoinedIntoCommunity(int userId, int communityId) throws SQLException;
    boolean cancelJoin(int userId, int communityId) throws SQLException;
    void updateCommunity(CommunityModel community) throws SQLException;
    Optional<Integer> getUserPermissionLevel(int userId, int communityId) throws SQLException;
    void deleteCommunity(int communityId, int userId) throws SQLException;
}
