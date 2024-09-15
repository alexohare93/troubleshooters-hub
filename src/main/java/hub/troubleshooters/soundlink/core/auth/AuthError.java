package hub.troubleshooters.soundlink.core.auth;

/**
 * Represents an error that occurs during authentication or authorization.
 */
public class AuthError extends RuntimeException {
    public AuthError(String message) {
        super(message);
    }
}
