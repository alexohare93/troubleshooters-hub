package hub.troubleshooters.soundlink.core.auth;

import java.util.List;
import java.util.Set;

public class IdentityServiceImpl implements IdentityService {
    private UserContext userContext;

    @Override
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public UserContext getUserContext() {
        return userContext;
    }

    @Override
    public boolean isAuthorized(Scope... scopes) {
        if (userContext == null) {
            return false;
        }

        // superadmin override
        if (userContext.getCurrentScopes().contains(Scope.SUPERADMIN)) {
            return true;
        }
        return userContext.getCurrentScopes().containsAll(List.of(scopes));
    }
}
