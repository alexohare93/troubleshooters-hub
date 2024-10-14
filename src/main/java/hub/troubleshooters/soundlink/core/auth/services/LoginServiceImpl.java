package hub.troubleshooters.soundlink.core.auth.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.auth.models.LoginModel;
import hub.troubleshooters.soundlink.core.auth.models.RegisterModel;
import hub.troubleshooters.soundlink.core.auth.validation.AuthError;
import hub.troubleshooters.soundlink.core.auth.validation.AuthResult;
import hub.troubleshooters.soundlink.core.auth.UserContext;
import hub.troubleshooters.soundlink.core.auth.validation.LoginModelValidator;
import hub.troubleshooters.soundlink.core.auth.validation.RegisterModelValidator;
import hub.troubleshooters.soundlink.data.factories.CommunityMemberFactory;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.factories.UserProfileFactory;

import java.sql.SQLException;

public class LoginServiceImpl implements LoginService {
    private final UserFactory userFactory;
    private final UserProfileFactory userProfileFactory;
    private final CommunityMemberFactory communityMemberFactory;
    private final IdentityService identityService;
    private final LoginModelValidator loginModelValidator;
    private final RegisterModelValidator registerModelValidator;

    @Inject
    public LoginServiceImpl(
            IdentityService identityService,
            UserFactory userFactory,
            CommunityMemberFactory communityUserFactory,
            LoginModelValidator loginModelValidator,
            RegisterModelValidator registerModelValidator,
            UserProfileFactory userProfileFactory
    ) {
        this.identityService = identityService;
        this.userFactory = userFactory;
        this.communityMemberFactory = communityUserFactory;
        this.loginModelValidator = loginModelValidator;
        this.registerModelValidator = registerModelValidator;
        this.userProfileFactory = userProfileFactory;
    }

    @Override
    public AuthResult login(LoginModel model) {
        // validate the model is well-formed first
        var result = loginModelValidator.validate(model);
        if (!result.isSuccess()) {
            return new AuthResult(new AuthError("Username and password must have values"));
        }

        try {
            var userOption = userFactory.get(model.username());
            if (userOption.isEmpty()) {
                return new AuthResult(new AuthError("Incorrect username or password"));
            }
            var user = userOption.get();
            if (user.getHashedPassword().isEmpty() || !verifyPassword(model.password(), user.getHashedPassword())) {
                return new AuthResult(new AuthError("Incorrect username or password"));
            }
            user.setLastLogin(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

            // get all community memberships for this user
            var memberships = communityMemberFactory.get(user);

            identityService.setUserContext(new UserContext(user, memberships));
            userFactory.save(user);
            return new AuthResult(); // successful login
        } catch (SQLException e) {
            return new AuthResult(new AuthError("Internal server error. Please contact SoundLink support. Error: " + e.getMessage()));
        }
    }

    @Override
    public void logout() {
        identityService.setUserContext(null);
    }

    @Override
    public AuthResult register(RegisterModel model) {
        // validate the model is well-formed first
        var result = registerModelValidator.validate(model);
        if (!result.isSuccess()) {
            return new AuthResult(new AuthError("Username and password must have values"));
        }

        try {
            if (userFactory.get(model.username()).isPresent()) {
                return new AuthResult(new AuthError("User already exists"));
            }
            var hashedPassword = hashPassword(model.password());
            var user = userFactory.create(model.username(), hashedPassword);

            // create new user profile
            userProfileFactory.create(user.getId(), user.getUsername());

            return new AuthResult(); // registration successful
        } catch (SQLException e) {
            return new AuthResult(new AuthError("Internal server error. Please contact SoundLink support. Error: " + e.getMessage()));
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
