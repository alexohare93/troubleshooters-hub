package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.StatementPreparer;
import hub.troubleshooters.soundlink.data.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserFactoryTest {

    @Mock
    private DatabaseConnection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private UserFactory userFactory;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(1, "testuser", "hashedpassword", new java.util.Date(), new java.util.Date());
    }

    @Test
    void testSave() throws SQLException {
        // Arrange
        doNothing().when(mockConnection).executeUpdate(anyString(), any(), any());

        // Act
        userFactory.save(testUser);

        // Assert
        ArgumentCaptor<StatementPreparer> captor = ArgumentCaptor.forClass(StatementPreparer.class);
        verify(mockConnection).executeUpdate(eq("UPDATE Users SET Username = ?, HashedPassword = ?, Created = ?, LastLogin = ? WHERE Id = ?;"), captor.capture(), any());

        // Verify the correct values were set in the prepared statement
        StatementPreparer preparer = captor.getValue();
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        preparer.prepare(mockStatement);
        verify(mockStatement).setString(1, testUser.getUsername());
        verify(mockStatement).setString(2, testUser.getHashedPassword());
        verify(mockStatement).setDate(3, new Date(testUser.getCreated().getTime()));
        verify(mockStatement).setDate(4, new Date(testUser.getLastLogin().getTime()));
        verify(mockStatement).setInt(5, testUser.getId());
    }

    @Test
    void testGetById_UserFound() throws SQLException {
        // Arrange
        String sql = "SELECT * FROM Users WHERE Id = ?";
        when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(testUser);

        // Act
        Optional<User> result = userFactory.get(testUser.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetById_UserNotFound() throws SQLException {
        // Arrange
        String sql = "SELECT * FROM Users WHERE Id = ?";
        when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(null);

        // Act
        Optional<User> result = userFactory.get(999);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByUsername_UserFound() throws SQLException {
        // Arrange
        String sql = "SELECT * FROM Users WHERE Username = ?";
        when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(testUser);

        // Act
        Optional<User> result = userFactory.get(testUser.getUsername());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetByUsername_UserNotFound() throws SQLException {
        // Arrange
        String sql = "SELECT * FROM Users WHERE Username = ?";
        when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(null);

        // Act
        Optional<User> result = userFactory.get("nonexistentuser");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCreate() throws SQLException {
        // Arrange
        String sql = "INSERT INTO Users (Username, HashedPassword) VALUES (?, ?)";
        doNothing().when(mockConnection).executeUpdate(anyString(), any(), any());

        // Act
        userFactory.create(testUser.getUsername(), testUser.getHashedPassword());

        // Assert
        ArgumentCaptor<StatementPreparer> captor = ArgumentCaptor.forClass(StatementPreparer.class);
        verify(mockConnection).executeUpdate(eq(sql), captor.capture(), any());

        // Verify the correct values were set in the prepared statement
        StatementPreparer preparer = captor.getValue();
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        preparer.prepare(mockStatement);
        verify(mockStatement).setString(1, testUser.getUsername());
        verify(mockStatement).setString(2, testUser.getHashedPassword());
    }
}
