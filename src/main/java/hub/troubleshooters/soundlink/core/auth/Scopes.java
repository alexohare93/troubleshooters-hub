package hub.troubleshooters.soundlink.core.auth;

/**
 * Set of authentication scopes (permissions) available for users.
 * Different user types will have a different available set of scopes.
 * i.e. Admin will have more permissions than standard user.
 */
public enum Scopes {
    EventRead,
    EventWrite,
    CommunityRead,
    CommunityWrite,
    SuperAdmin  // can do everything. This permission acts as an override.
}
