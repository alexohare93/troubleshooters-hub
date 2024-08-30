package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.core.CoreResult;

/**
 * Represents the result of an authentication operation.
 */
public class AuthResult extends CoreResult<Void, AuthException> {
    public AuthResult() {
        super((Void) null);
    }

    public AuthResult(AuthException error) {
        super(error);
    }
}
