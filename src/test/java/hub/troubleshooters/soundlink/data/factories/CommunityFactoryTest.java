package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.StatementPreparer;
import hub.troubleshooters.soundlink.data.models.Community;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommunityFactoryTest {

    @Mock
    private DatabaseConnection mockConnection;

    @InjectMocks
    private CommunityFactory communityFactory;

    private Community testCommunity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Prepare a sample community object for tests
        testCommunity = new Community(1, "Rock Community", "A community for rock music fans", "Rock", new java.util.Date());
    }

    @Test
    void testSave() throws SQLException {
        // Arrange
        doNothing().when(mockConnection).executeUpdate(anyString(), any(), any());

        // Act
        communityFactory.save(testCommunity);

        // Assert
        ArgumentCaptor<StatementPreparer> captor = ArgumentCaptor.forClass(StatementPreparer.class);
        verify(mockConnection).executeUpdate(eq("UPDATE Communities SET Name = ?, Description = ?, Genre = ?, Created = ? WHERE Id = ?"), captor.capture(), any());

        // Verify the correct values were set in the prepared statement
        StatementPreparer preparer = captor.getValue();
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        preparer.prepare(mockStatement);
        verify(mockStatement).setString(1, testCommunity.getName());
        verify(mockStatement).setString(2, testCommunity.getDescription());
        verify(mockStatement).setString(3, testCommunity.getGenre());
        verify(mockStatement).setDate(4, new java.sql.Date(testCommunity.getCreated().getTime()));
        verify(mockStatement).setInt(5, testCommunity.getId());
    }

    @Test
    void testGet_CommunityFound() throws SQLException {
        // Arrange
        Community testCommunity = new Community(1, "Test Community", "Description", "Genre", new java.util.Date());

        // Mock the executeQuery method to return the testCommunity
        when(mockConnection.executeQuery(anyString(), any(), any())).thenReturn(testCommunity);

        // Act
        Optional<Community> result = communityFactory.get(testCommunity.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCommunity.getId(), result.get().getId());
        assertEquals(testCommunity.getName(), result.get().getName());
        assertEquals(testCommunity.getDescription(), result.get().getDescription());
        assertEquals(testCommunity.getGenre(), result.get().getGenre());
        assertEquals(testCommunity.getCreated(), result.get().getCreated());
    }

    @Test
    void testGet_CommunityNotFound() throws SQLException {
        // Arrange
        when(mockConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

        // Act
        Optional<Community> result = communityFactory.get(999);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCreate() throws SQLException {
        // Arrange
        doNothing().when(mockConnection).executeUpdate(anyString(), any(), any());

        // Act
        communityFactory.create(testCommunity);

        // Assert
        ArgumentCaptor<StatementPreparer> captor = ArgumentCaptor.forClass(StatementPreparer.class);
        verify(mockConnection).executeUpdate(eq("INSERT INTO Communities (Id, Name, Description, Genre, Created) VALUES (?, ?, ?, ?, ?)"), captor.capture(), any());

        // Verify the correct values were set in the prepared statement
        StatementPreparer preparer = captor.getValue();
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        preparer.prepare(mockStatement);
        verify(mockStatement).setInt(1, testCommunity.getId());
        verify(mockStatement).setString(2, testCommunity.getName());
        verify(mockStatement).setString(3, testCommunity.getDescription());
        verify(mockStatement).setString(4, testCommunity.getGenre());
        verify(mockStatement).setDate(5, new java.sql.Date(testCommunity.getCreated().getTime()));
    }
}
