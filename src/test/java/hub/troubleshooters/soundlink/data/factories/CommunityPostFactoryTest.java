package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.CommunityPost;
import hub.troubleshooters.soundlink.data.models.Community;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CommunityPostFactoryTest {

	@Mock
	private DatabaseConnection databaseConnection;

	@InjectMocks
	private CommunityPostFactory communityPostFactory;

	private CommunityPost communityPost;
	private Community community;
	private int communityId;

	@BeforeEach
	void setUp() {
		// Initialize mocks and open them for use in tests
		MockitoAnnotations.openMocks(this);

		// Initialize a CommunityPost object with mock data
		communityPost = new CommunityPost(1, 1, 1, "title", "content", new Date());

		communityId = 1;
		// Initialize a Community object with mock data
		community = new Community(communityId, "name", "description", "genre", new Date(), null, false);
	}

	@Test
	void testGetCommunityPostById_Success() throws SQLException {
		// Mock the database query to return a specific CommunityPost
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(communityPost);

		// Call the method to retrieve a CommunityPost by ID
		var result = communityPostFactory.get(1);

		// Assert that the result is present and matches the expected CommunityPost
		assertTrue(result.isPresent());
		assertEquals(communityPost, result.get());
	}

	@Test
	void testGetCommunityPostById_NotFound() throws SQLException {
		// Mock the database query to return null (indicating no result found)
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

		// Call the method to retrieve a CommunityPost by an ID that doesn't exist
		var result = communityPostFactory.get(999);

		// Assert that no result is found (empty Optional)
		assertFalse(result.isPresent());
	}

	@Test
	void testGetCommunityPostsByCommunity_Success() throws SQLException {
		// Mock the database query to return a list containing one CommunityPost
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(List.of(communityPost));

		// Call the method to retrieve all CommunityPosts for the given Community
		var result = communityPostFactory.getPosts(communityId);

		// Assert that the result contains the expected CommunityPost
		assertEquals(1, result.size());
		assertTrue(result.contains(communityPost));
	}

	@Test
	void testGetCommunityPostsByCommunity_NotFound() throws SQLException {
		// Mock the database query to return an empty list
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(new ArrayList<CommunityPost>());

		// Call method to retrieve all  Community Posts for this non-existent Community
		var result = communityPostFactory.getPosts(communityId);

		// Assert that the result is empty
		assertEquals(0, result.size());
	}

	@Test
	void testSaveCommunityPost_Success() throws SQLException {
		// Simulate a successful database update (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that no exception is thrown
		assertDoesNotThrow(() -> communityPostFactory.save(communityPost));
	}

	@Test
	void testSaveCommunityPost_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> communityPostFactory.save(communityPost));
	}

	@Test
	void CreateCommunityPost_Success() throws SQLException {
		// Simulate a successful database insertion (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the create method and assert that no exception is thrown
		assertDoesNotThrow(() -> communityPostFactory.create(1, 1, "title", "content"));
	}

	@Test
	void CreateCommunityPost_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> communityPostFactory.create(1, 1, "title", "content"));
	}
}

