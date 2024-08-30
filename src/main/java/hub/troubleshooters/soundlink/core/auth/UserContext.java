package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.core.data.models.UserModel;

import java.util.Set;

/**
 * Represents the context of the current user. This includes the user model and the current permissions of the user.
 */
public class UserContext {
    private UserModel user;

    public UserContext(UserModel user) {
        this.user = user;
    }

    public Set<Scope> getCurrentScopes() {
        return ScopeUtils.deconstructScopes(user.permission());
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}