package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.core.CoreResult;

public class AuthResult extends CoreResult<String, AuthException> {
    public AuthResult() {
        super("");
    }

    public AuthResult(AuthException error) {
        super(error);
    }
}
