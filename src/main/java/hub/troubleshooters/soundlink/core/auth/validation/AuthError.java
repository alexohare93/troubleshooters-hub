package hub.troubleshooters.soundlink.core.auth.validation;

/**
 * Represents an error that occurs during authentication or authorization.
 */
public class AuthError extends RuntimeException {
    /**
     * Constructs a new {@code AuthError} with the specified detail message.
     *
     * @param message The detail message explaining the cause of the error.
     */
    public AuthError(String message) {
        super(message);
    }
}
