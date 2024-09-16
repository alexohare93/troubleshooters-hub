package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.data.models.Community;

public interface IdentityService {
  void setUserContext(UserContext userContext);

  UserContext getUserContext();

  /**
   * Checks if the current user is authorized against the given scopes.
   *
   * @param scopes The scopes to check.
   * @return True if the user is authorized, false otherwise.
   */
  boolean isAuthorized(Community community, Scope... scopes);
}
