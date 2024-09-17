package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import hub.troubleshooters.soundlink.data.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommunityMemberFactoryTest {

    @Mock
    private DatabaseConnection databaseConnection;

    @InjectMocks
    private CommunityMemberFactory communityMemberFactory;

    private CommunityMember communityMember;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialize mocks and open them for use in tests
        MockitoAnnotations.openMocks(this);

        // Initialize a CommunityMember object with mock data
        communityMember = new CommunityMember(1, 1, 1, new Date(), 2);

        // Initialize a User object with mock data
        user = new User(1, "username", "hashed_password", new Date(), new Date());
    }

    @Test
    void testGetCommunityMemberById_Success() throws SQLException {
        // Mock the database query to return a specific CommunityMember
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(communityMember);

        // Call the method to retrieve a CommunityMember by ID
        var result = communityMemberFactory.get(1);

        // Assert that the result is present and matches the expected CommunityMember
        assertTrue(result.isPresent());
        assertEquals(communityMember, result.get());
    }

    @Test
    void testGetCommunityMemberById_NotFound() throws SQLException {
        // Mock the database query to return null (indicating no result found)
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

        // Call the method to retrieve a CommunityMember by an ID that doesn't exist
        var result = communityMemberFactory.get(999);

        // Assert that no result is found (empty Optional)
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCommunityMembersByUser_Success() throws SQLException {
        // Mock the database query to return a list containing one CommunityMember
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(List.of(communityMember));

        // Call the method to retrieve all CommunityMembers for the given User
        var result = communityMemberFactory.get(user);

        // Assert that the result contains the expected CommunityMember
        assertEquals(1, result.size());
        assertTrue(result.contains(communityMember));
    }

    @Test
    void testSaveCommunityMember_Success() throws SQLException {
        // Simulate a successful database update (1 row affected)
        doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Call the save method and assert that no exception is thrown
        assertDoesNotThrow(() -> communityMemberFactory.save(communityMember));
    }

    @Test
    void testSaveCommunityMember_Failure() throws SQLException {
        // Simulate an SQLException being thrown during the update
        doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Call the save method and assert that an SQLException is thrown
        assertThrows(SQLException.class, () -> communityMemberFactory.save(communityMember));
    }
}

