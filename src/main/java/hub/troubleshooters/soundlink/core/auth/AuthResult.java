package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.core.CoreResult;

/**
 * Represents the result of an authentication operation.
 */
public class AuthResult extends CoreResult<Void, AuthError> {
    public AuthResult() {
        super((Void) null);
    }

    public AuthResult(AuthError error) {
        super(error);
    }
}
