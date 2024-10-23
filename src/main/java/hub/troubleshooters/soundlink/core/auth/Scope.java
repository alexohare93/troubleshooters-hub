package hub.troubleshooters.soundlink.core.auth;

/**
 * <p>Set of authentication scopes (permissions) available for users.
 * Different user types will have a different available set of scopes.
 * i.e. Admin will have more permissions than standard user.</p>
 *
 * <p>These scopes are represented as a bitmask for storage in the database.
 * For methods to encode and decode between a set of Scopes and an integer, use ScopeUtils.</p>
 * @see ScopeUtils
 */
public enum Scope {
    /**
     * Grants the user all permissions (admin override).
     */
    SUPERADMIN(1),  // Can do everything. This permission acts as an override.

    /**
     * Grants the user permission to read event data.
     */
    EVENT_READ(1 << 1),

    /**
     * Grants the user permission to write (create or modify) event data.
     */
    EVENT_WRITE(1 << 2),

    /**
     * Grants the user permission to read community data.
     */
    COMMUNITY_READ(1 << 3),

    /**
     * Grants the user permission to write (create or modify) community data.
     */
    COMMUNITY_WRITE(1 << 4);

    private final int bitmask;

    /**
     * Constructor for the {@code Scope} enum.
     *
     * @param bitmask The bitmask value representing the scope.
     */
    Scope(final int bitmask) {
        this.bitmask = bitmask;
    }

    /**
     * Returns the bitmask value associated with the scope.
     *
     * @return The bitmask representing the scope.
     */
    public int getBitmask() {
        return bitmask;
    }
}