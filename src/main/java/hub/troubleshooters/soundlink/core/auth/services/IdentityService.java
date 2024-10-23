package hub.troubleshooters.soundlink.core.auth.services;

import hub.troubleshooters.soundlink.core.auth.Scope;
import hub.troubleshooters.soundlink.core.auth.UserContext;
import hub.troubleshooters.soundlink.data.models.Community;

import java.util.List;

/**
 * Interface responsible for managing user identity and authorization within the system.
 * Provides methods to set and retrieve user context, check authorization, and retrieve
 * communities associated with the logged-in user.
 */
public interface IdentityService {

    /**
     * Sets the user context for the currently logged-in user.
     *
     * @param userContext The {@link UserContext} containing information about the logged-in user.
     */
    void setUserContext(UserContext userContext);

    /**
     * Retrieves the current user context for the logged-in user.
     *
     * @return The {@link UserContext} containing information about the logged-in user.
     */
    UserContext getUserContext();

    /**
     * Gets all communities that the logged-in user is a member of.
     *
     * @return A list of {@link Community} objects that the user is a member of.
     *         The list may be empty if the user is not a member of any community.
     */
    List<Community> getCommunities();

    /**
     * Checks if the current user is authorized against the given scopes in the specified community.
     *
     * @param community The {@link Community} to check authorization for.
     * @param scopes The {@link Scope} values representing the required permissions.
     * @return {@code true} if the user is authorized for the specified scopes, {@code false} otherwise.
     */
    boolean isAuthorized(Community community, Scope... scopes);
}