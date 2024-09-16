package hub.troubleshooters.soundlink.core.auth;

/**
 * Set of authentication scopes (permissions) available for users. Different user types will have a
 * different available set of scopes. i.e. Admin will have more permissions than standard user.
 *
 * <p>These scopes are represented as a bitmask for storage in the database. For methods to encode
 * and decode between a set of Scopes and an integer, use ScopeUtils.
 *
 * @see ScopeUtils
 */
public enum Scope {
  SUPERADMIN(1), // can do everything. This permission acts as an override.
  EVENT_READ(1 << 1),
  EVENT_WRITE(1 << 2),
  COMMUNITY_READ(1 << 3),
  COMMUNITY_WRITE(1 << 4);

  private final int bitmask;

  Scope(final int bitmask) {
    this.bitmask = bitmask;
  }

  public int getBitmask() {
    return bitmask;
  }
}
