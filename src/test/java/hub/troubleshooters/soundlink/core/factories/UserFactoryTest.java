package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserFactoryTest {

    @Mock
    private DatabaseConnection databaseConnection;

    @InjectMocks
    private UserFactory userFactory;

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize mocks for this test case
        MockitoAnnotations.openMocks(this);

        // Create a sample user object for use in the tests
        user = new User(1, "username", "hashed_password", new Date(), new Date());
    }

    @Test
    void testGetUserById_Success() throws SQLException {
        // Mock the database query to return the user when the given ID is found
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(user);

        // Call the method to get the user by their ID
        Optional<User> result = userFactory.get(1);

        // Verify that the user was found and matches the expected user
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testGetUserById_NotFound() throws SQLException {
        // Mock the database query to return null when no user is found
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

        // Call the method to get the user by a non-existent ID
        Optional<User> result = userFactory.get(999);

        // Verify that no user was found
        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByUsername_Success() throws SQLException {
        // Mock the database query to return the user when the username is found
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(user);

        // Call the method to get the user by their username
        Optional<User> result = userFactory.get("username");

        // Verify that the user was found and matches the expected user
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testSaveUser_Success() throws SQLException {
        // Simulate a successful update with 1 row affected in the database
        doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Call the save method and verify that no exception is thrown
        assertDoesNotThrow(() -> userFactory.save(user));
    }

    @Test
    void testSaveUser_Failure() throws SQLException {
        // Simulate an SQLException being thrown during the update
        doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Call the save method and expect an SQLException to be thrown
        assertThrows(SQLException.class, () -> userFactory.save(user));
    }
}

