package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.data.models.Community;

import java.util.List;

public interface IdentityService {
    void setUserContext(UserContext userContext);
    UserContext getUserContext();

    /**
     * Gets all communities that the logged-in user is a member of
     * @return a possible empty list of communities
     */
    List<Community> getCommunities();

    /**
     * Checks if the current user is authorized against the given scopes.
     * @param scopes The scopes to check.
     * @return True if the user is authorized, false otherwise.
     */
    boolean isAuthorized(Community community, Scope... scopes);
}
