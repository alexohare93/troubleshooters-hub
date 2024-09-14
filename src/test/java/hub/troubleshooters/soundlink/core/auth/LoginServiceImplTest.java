package hub.troubleshooters.soundlink.core.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import hub.troubleshooters.soundlink.data.factories.CommunityMemberFactory;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.models.User;
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
    }

    @Test
    void testLogin_Successful() throws SQLException {
        // whenever our mocked userFactory.get method is called, it will always return Optional.of(user).
        when(userFactory.get(user.getUsername())).thenReturn(Optional.of(user));

        var result = loginServiceImpl.login(user.getUsername(), PASSWORD);  // performing the actual test

        assertTrue(result.isSuccess());
        assertNull(result.getError());

        verify(communityMemberFactory).get(user);
        verify(userFactory).save(user); // ensure the save method was called during our test
    }

    @Test
    void testLogin_IncorrectUsername() throws SQLException {
        // whenever our mocked userFactory.get method is called, it will always return Optional.empty to simulate an invalid username
        when(userFactory.get(user.getUsername())).thenReturn(Optional.empty());

        var result = loginServiceImpl.login(user.getUsername(), PASSWORD);  // performing the actual test

        assertFalse(result.isSuccess());
        assertEquals(result.getError().getMessage(), "Incorrect username or password");
    }

    // Test for correct username but incorrect password
    @Test
    void testLogin_IncorrectPassword() throws SQLException {
        when(userFactory.get(user.getUsername())).thenReturn(Optional.of(user));
        var result = loginServiceImpl.login(user.getUsername(), INCORRECT_PASSWORD);
        assertFalse(result.isSuccess());
        assertEquals(result.getError().getMessage(), "Incorrect username or password");
    }

    // Test for null username
    @Test
    void testLogin_NullUsername() throws SQLException {
        // Simulate that the userFactory returns Optional.empty() when null is passed
        when(userFactory.get(null)).thenReturn(Optional.empty());
        var result = loginServiceImpl.login(null, PASSWORD);
        // Assert that the result is a failure and the error message matches your expectations
        assertFalse(result.isSuccess());
        assertEquals("Incorrect username or password", result.getError().getMessage());  // Expecting this behavior for null username
    }

    // Test for null password
    @Test
    void testLogin_NullPassword() throws SQLException {
        // Mock the userFactory to return a valid user
        when(userFactory.get(user.getUsername())).thenReturn(Optional.of(user));
        // Perform the login action with a null password
        try {
            var result = loginServiceImpl.login(user.getUsername(), null);
            // The following assertions are expected if no exception occurs
            assertFalse(result.isSuccess());
            assertEquals("Incorrect username or password", result.getError().getMessage());
        } catch (NullPointerException e) {
            // Catch the NullPointerException and assert that it occurred due to the null password
            assertTrue(e.getMessage().contains("password"));
        }
        // Verify that the userFactory.get was called, but no save operation occurred
        verify(userFactory).get(user.getUsername());
        verify(userFactory, never()).save(any());
    }

    // Test for user registration
    @Test
    void testRegisterUser_Successful() throws SQLException {
        // Mock the userFactory to return empty, indicating the user does not exist
        when(userFactory.get(user.getUsername())).thenReturn(Optional.empty());
        // Perform the registration with valid username and password
        var result = loginServiceImpl.register(user.getUsername(), PASSWORD);
        // Assert that the registration is successful and no error is returned
        assertTrue(result.isSuccess());
        assertNull(result.getError());
        // Verify that the create method is called on userFactory with correct parameters
        verify(userFactory).create(eq(user.getUsername()), any(String.class));
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
