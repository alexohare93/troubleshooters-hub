package hub.troubleshooters.soundlink.core.auth;

import java.util.Set;

public interface IdentityService {
    void setUserContext(UserContext userContext);
    UserContext getUserContext();

    /**
     * Checks if the current user is authorized against the given scopes.
     * @param scopes The scopes to check.
     * @return True if the user is authorized, false otherwise.
     */
    boolean isAuthorized(Scope... scopes);
}
