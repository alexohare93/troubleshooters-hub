package hub.troubleshooters.soundlink.core.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.data.Map;
import hub.troubleshooters.soundlink.core.data.models.UserModel;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.models.User;

import java.sql.SQLException;

public class LoginServiceImpl implements LoginService {
    private final UserFactory userFactory;
    private final IdentityService identityService;

    @Inject
    public LoginServiceImpl(IdentityService identityService, UserFactory userFactory) {
        this.identityService = identityService;
        this.userFactory = userFactory;
    }

    @Override
    public AuthResult login(String username, String password) {
        try {
            var userOption = userFactory.get(username);
            if (userOption.isEmpty()) {
                return new AuthResult(new AuthException("Incorrect username or password"));
            }
            var user = userOption.get();
            if (user.getHashedPassword().isEmpty() || !verifyPassword(password, user.getHashedPassword())) {
                return new AuthResult(new AuthException("Incorrect username or password"));
            }
            user.setLastLogin(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            var userModel = Map.userModel(user);
            identityService.setUserContext(new UserContext(userModel));
            userFactory.save(user);
            return new AuthResult(); // successful login
        } catch (SQLException e) {
            return new AuthResult(new AuthException("Internal server error. Please contact SoundLink support. Error: " + e.getMessage()));
        }
    }

    @Override
    public void logout() {
        identityService.setUserContext(null);
    }

    @Override
    public AuthResult register(String username, String password) {
        try {
            if (userFactory.get(username).isPresent()) {
                return new AuthResult(new AuthException("User already exists"));
            }
            var hashedPassword = hashPassword(password);
            userFactory.create(username, hashedPassword);
            return new AuthResult(); // registration successful
        } catch (SQLException e) {
            return new AuthResult(new AuthException("Internal server error. Please contact SoundLink support. Error: " + e.getMessage()));
        }
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        var result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}
