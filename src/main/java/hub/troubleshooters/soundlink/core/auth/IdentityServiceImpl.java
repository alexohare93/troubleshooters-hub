package hub.troubleshooters.soundlink.core.auth;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityMember;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class IdentityServiceImpl implements IdentityService {
    private final CommunityFactory communityFactory;

    private UserContext userContext;

    @Inject
    public IdentityServiceImpl(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    @Override
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public UserContext getUserContext() {
        return userContext;
    }

    @Override
    public boolean isAuthorized(Community community, Scope... scopes) {
        if (userContext == null) {
            return false;
        }

        // superadmin override
        if (userContext.getCurrentScopes(community).contains(Scope.SUPERADMIN)) {
            return true;
        }
        return userContext.getCurrentScopes(community).containsAll(List.of(scopes));
    }

    @Override
    public List<Community> getCommunities() {
        if (userContext == null) {
            return List.of();
        }

        try {
            return communityFactory.get(userContext.getCommunityMembers().stream().map(CommunityMember::getCommunityId).toList());
        } catch (SQLException e) {
            return List.of();
        }
    }
}
