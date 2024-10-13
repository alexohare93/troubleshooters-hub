package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Community;
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

public class CommunityFactoryTest {

    @Mock
    private DatabaseConnection databaseConnection;

    @InjectMocks
    private CommunityFactory communityFactory;

    private Community community;

    @BeforeEach
    void setUp() {
        // Initialize mocks and open them for use in tests
        MockitoAnnotations.openMocks(this);

        // Create a sample community object for use in the tests
        community = new Community(1, "Test Community", "Description", "Genre", new Date(), null);
    }

    @Test
    void testGetCommunityById_Success() throws SQLException {
        // Mocking the database query to return the community when called
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(community);

        // Call the method and store the result
        Optional<Community> result = communityFactory.get(1);

        // Verify that the community was found and is the expected one
        assertTrue(result.isPresent());
        assertEquals(community, result.get());
    }

    @Test
    void testGetCommunityById_NotFound() throws SQLException {
        // Mocking the database query to return null (community not found)
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

        // Call the method and expect the result to be empty (community not found)
        Optional<Community> result = communityFactory.get(999);

        // Verify that no community was found
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCommunityById_SQLException() throws SQLException {
        // Mocking the behavior to throw a SQLException when querying the database
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenThrow(new SQLException());

        // Verify that the method throws SQLException when querying the community
        assertThrows(SQLException.class, () -> communityFactory.get(1));
    }

    @Test
    void testSaveCommunity_Success() throws SQLException {
        // Simulate a successful update where 1 row is affected in the database
        doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Call the save method and verify that no exceptions are thrown
        assertDoesNotThrow(() -> communityFactory.save(community));
    }

    @Test
    void testSaveCommunity_Failure() throws SQLException {
        // Simulate an SQLException being thrown during the update operation
        doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Expect an SQLException to be thrown when trying to save the community
        assertThrows(SQLException.class, () -> communityFactory.save(community));
    }
}

