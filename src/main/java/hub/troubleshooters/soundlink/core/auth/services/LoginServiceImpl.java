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

/**
 * Implementation of the {@link LoginService} interface responsible for user authentication,
 * registration, and session management. This class validates login and registration models,
 * handles user authentication, and manages user context during login and logout operations.
 */
public class LoginServiceImpl implements LoginService {
    private final UserFactory userFactory;
    private final UserProfileFactory userProfileFactory;
    private final CommunityMemberFactory communityMemberFactory;
    private final IdentityService identityService;
    private final LoginModelValidator loginModelValidator;
    private final RegisterModelValidator registerModelValidator;

    /**
     * Constructs a new {@code LoginServiceImpl} with the necessary dependencies.
     *
     * @param identityService The service responsible for managing user identity.
     * @param userFactory The factory responsible for managing user data.
     * @param communityUserFactory The factory responsible for managing community membership data.
     * @param loginModelValidator The validator for login models.
     * @param registerModelValidator The validator for registration models.
     * @param userProfileFactory The factory responsible for managing user profiles.
     */
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

    /**
     * Authenticates the user based on the provided login model. If the username and password are valid,
     * the user is logged in and their context is set.
     *
     * @param model The {@link LoginModel} containing the user's login credentials.
     * @return An {@link AuthResult} that indicates whether authentication was successful.
     */
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

    /**
     * Logs out the currently authenticated user by clearing their session context.
     */
    @Override
    public void logout() {
        identityService.setUserContext(null);
    }

    /**
     * Registers a new user based on the provided registration model. If the username is not already
     * taken, the user is created and their profile is initialized.
     *
     * @param model The {@link RegisterModel} containing the user's registration details.
     * @return An {@link AuthResult} that indicates whether registration was successful.
     */
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

    /**
     * Hashes the user's password using BCrypt with a default strength of 12.
     *
     * @param password The plain-text password to hash.
     * @return The hashed password.
     */
    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Verifies the provided plain-text password against the hashed password.
     *
     * @param password The plain-text password.
     * @param hashedPassword The hashed password to compare against.
     * @return {@code true} if the password is valid, {@code false} otherwise.
     */
    private boolean verifyPassword(String password, String hashedPassword) {
        var result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}
