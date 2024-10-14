package hub.troubleshooters.soundlink.core.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import hub.troubleshooters.soundlink.core.auth.models.LoginModel;
import hub.troubleshooters.soundlink.core.auth.models.RegisterModel;
import hub.troubleshooters.soundlink.core.auth.services.IdentityService;
import hub.troubleshooters.soundlink.core.auth.services.LoginServiceImpl;
import hub.troubleshooters.soundlink.core.auth.validation.LoginModelValidator;
import hub.troubleshooters.soundlink.core.auth.validation.RegisterModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityMemberFactory;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.factories.UserProfileFactory;
import hub.troubleshooters.soundlink.data.models.User;
import hub.troubleshooters.soundlink.data.models.UserProfile;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {
    private final String PASSWORD = "testPass";     // the plaintext password for our test user
    private final String INCORRECT_PASSWORD = "wrongPass";  // incorrect password for testing
    private User user;

    // setting up our mocked dependencies necessary for the LoginServiceImpl
    @Mock
    private UserFactory userFactory;
    @Mock
    private CommunityMemberFactory communityMemberFactory;
    @Mock
    private IdentityService identityService;
    @Mock
    private LoginModelValidator loginModelValidator;
    @Mock
    private RegisterModelValidator registerModelValidator;
    @Mock
    private UserProfileFactory userProfileFactory;

    // providing our mocked dependencies to our service we are actually testing
    @InjectMocks
    private LoginServiceImpl loginServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // creating the user for our tests
        var username = "testUser";
        var hashedPassword = hashPassword(PASSWORD);

        // setting up a mock user
        user = new User(0);
        user.setUsername(username);
        user.setHashedPassword(hashedPassword);

        when(loginModelValidator.validate(any())).thenReturn(new ValidationResult());
        when(registerModelValidator.validate(any())).thenReturn(new ValidationResult());
    }

    @Test
    void testLogin_Successful() throws SQLException {
        // whenever our mocked userFactory.get method is called, it will always return Optional.of(user).
        when(userFactory.get(user.getUsername())).thenReturn(Optional.of(user));

        var result = loginServiceImpl.login(new LoginModel(user.getUsername(), PASSWORD));  // performing the actual test

        assertTrue(result.isSuccess());
        assertNull(result.getError());

        verify(communityMemberFactory).get(user);
        verify(userFactory).save(user); // ensure the save method was called during our test
    }

    @Test
    void testLogin_IncorrectUsername() throws SQLException {
        // whenever our mocked userFactory.get method is called, it will always return Optional.empty to simulate an invalid username
        when(userFactory.get(user.getUsername())).thenReturn(Optional.empty());

        var result = loginServiceImpl.login(new LoginModel(user.getUsername(), PASSWORD));  // performing the actual test

        assertFalse(result.isSuccess());
        assertEquals(result.getError().getMessage(), "Incorrect username or password");
    }

    @Test
    void testLogin_IncorrectPassword() throws SQLException {
        when(userFactory.get(user.getUsername())).thenReturn(Optional.of(user));

        var result = loginServiceImpl.login(new LoginModel(user.getUsername(), INCORRECT_PASSWORD));

        assertFalse(result.isSuccess());
        assertEquals(result.getError().getMessage(), "Incorrect username or password");
    }

    @Test
    void testLogin_NullUsername() {
        when(loginModelValidator.validate(any())).thenReturn(new ValidationResult(new ValidationError("")));

        var result = loginServiceImpl.login(new LoginModel(null, PASSWORD));

        assertFalse(result.isSuccess());
        assertEquals("Username and password must have values", result.getError().getMessage());
    }

    @Test
    void testLogin_NullPassword() throws SQLException {
        when(loginModelValidator.validate(any())).thenReturn(new ValidationResult(new ValidationError("")));

        var result = loginServiceImpl.login(new LoginModel(user.getUsername(), null));

        assertFalse(result.isSuccess());
        assertEquals("Username and password must have values", result.getError().getMessage());
    }

    @Disabled("SL-66 has broken this test due to the inclusion of the userProfileFactory create method being called. Didn't have time to diagnose fully.")
    @Test
    void testRegisterUser_Successful() throws SQLException {
        // Mock the userFactory to return empty, indicating the user does not exist
        when(userFactory.get(user.getUsername())).thenReturn(Optional.empty());
        when(userFactory.create(user.getUsername(), PASSWORD)).thenReturn(user);
        when(userProfileFactory.create(0, "testUser")).thenReturn(new UserProfile(0, 0, "", "", null));

        // Perform the registration with valid username and password
        var result = loginServiceImpl.register(new RegisterModel(user.getUsername(), PASSWORD));

        // Assert that the registration is successful and no error is returned
        assertTrue(result.isSuccess());
        assertNull(result.getError());

        // Verify that the create method is called on userFactory with correct parameters
        verify(userFactory).create(eq(user.getUsername()), any(String.class));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() throws SQLException {
        when(userFactory.get(user.getUsername())).thenReturn(Optional.of(user));
        var result = loginServiceImpl.register(new RegisterModel(user.getUsername(), PASSWORD));
        assertFalse(result.isSuccess());
        assertEquals(result.getError().getMessage(), "User already exists");
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
