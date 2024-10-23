package hub.troubleshooters.soundlink.core.auth.validation;

import hub.troubleshooters.soundlink.core.CoreResult;

/**
 * Represents the result of an authentication operation.
 */
public class AuthResult extends CoreResult<Void, AuthError> {

    /**
     * Constructs an {@code AuthResult} representing a successful authentication with no result data.
     */
    public AuthResult() {
        super((Void) null);
    }

    /**
     * Constructs an {@code AuthResult} representing a failed authentication with the specified {@link AuthError}.
     *
     * @param error The {@link AuthError} representing the reason for the authentication failure.
     */
    public AuthResult(AuthError error) {
        super(error);
    }
}
