package hub.troubleshooters.soundlink.core.auth.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.auth.Scope;
import hub.troubleshooters.soundlink.core.auth.UserContext;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityMember;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of the {@link IdentityService} interface responsible for managing
 * user identity and authorization. This class handles the retrieval of user context,
 * authorization checks, and fetching communities associated with the logged-in user.
 */
public class IdentityServiceImpl implements IdentityService {

    private final CommunityFactory communityFactory;
    private UserContext userContext;

    /**
     * Constructs a new {@code IdentityServiceImpl} with the specified community factory.
     *
     * @param communityFactory The factory responsible for managing community-related data.
     */
    @Inject
    public IdentityServiceImpl(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    /**
     * Sets the user context for the currently logged-in user.
     *
     * @param userContext The {@link UserContext} containing information about the logged-in user.
     */
    @Override
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    /**
     * Retrieves the current user context for the logged-in user.
     *
     * @return The {@link UserContext} containing information about the logged-in user.
     */
    @Override
    public UserContext getUserContext() {
        return userContext;
    }

    /**
     * Checks if the current user is authorized for the given scopes in the specified community.
     * <p>
     * If the user has the {@code SUPERADMIN} scope, they are automatically authorized for all actions.
     * </p>
     *
     * @param community The {@link Community} to check authorization for.
     * @param scopes The {@link Scope} values representing the required permissions.
     * @return {@code true} if the user is authorized for the specified scopes, {@code false} otherwise.
     */
    @Override
    public boolean isAuthorized(Community community, Scope... scopes) {
        if (userContext == null) {
            return false;
        }

        // Superadmin override: superadmins are authorized for all actions
        if (userContext.getCurrentScopes(community).contains(Scope.SUPERADMIN)) {
            return true;
        }
        return userContext.getCurrentScopes(community).containsAll(List.of(scopes));
    }

    /**
     * Retrieves a list of communities that the logged-in user is a member of.
     *
     * @return A list of {@link Community} objects the user is a member of, or an empty list if the user is not logged in.
     */
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