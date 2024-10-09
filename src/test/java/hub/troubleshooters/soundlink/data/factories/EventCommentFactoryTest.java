package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.EventComment;
import hub.troubleshooters.soundlink.data.models.Event;
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

public class EventCommentFactoryTest {

	@Mock
	private DatabaseConnection databaseConnection;

	@InjectMocks
	private EventCommentFactory eventCommentFactory;

	private EventComment eventComment;
	private Event event;

	@BeforeEach
	void setUp() {
		// Initialize mocks and open them for use in tests
		MockitoAnnotations.openMocks(this);

		// Initialize a EventComment object with mock data
		eventComment = new EventComment(1, 1, 1, "title", new Date());

		// Initialize a Event object with mock data
		event = new Event(1, 1, "name", "description", "venue", 100, new Date(), new Date(), null);
	}

	@Test
	void testGetEventCommentById_Success() throws SQLException {
		// Mock the database query to return a specific EventComment
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(eventComment);

		// Call the method to retrieve a EventComment by ID
		var result = eventCommentFactory.get(1);

		// Assert that the result is present and matches the expected EventComment
		assertTrue(result.isPresent());
		assertEquals(eventComment, result.get());
	}

	@Test
	void testGetEventCommentById_NotFound() throws SQLException {
		// Mock the database query to return null (indicating no result found)
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

		// Call the method to retrieve a EventComment by an ID that doesn't exist
		var result = eventCommentFactory.get(999);

		// Assert that no result is found (empty Optional)
		assertFalse(result.isPresent());
	}

	@Test
	void testGetEventCommentsByEvent_Success() throws SQLException {
		// Mock the database query to return a list containing one EventComment
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(List.of(eventComment));

		// Call the method to retrieve all EventComments for the given Event
		var result = eventCommentFactory.get(event);

		// Assert that the result contains the expected EventComment
		assertEquals(1, result.size());
		assertTrue(result.contains(eventComment));
	}

	@Test
	void testGetEventCommentsByEvent_NotFound() throws SQLException {
		// Mock the database query to return an empty list
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(new ArrayList<EventComment>());

		// Call method to retrieve all  Event Comments for this non-existent Event
		var result = eventCommentFactory.get(event);

		// Assert that the result contains the expected EventComment
		assertEquals(0, result.size());
	}

	@Test
	void testSaveEventComment_Success() throws SQLException {
		// Simulate a successful database update (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that no exception is thrown
		assertDoesNotThrow(() -> eventCommentFactory.save(eventComment));
	}

	@Test
	void testSaveEventComment_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> eventCommentFactory.save(eventComment));
	}

	@Test
	void CreateEventComment_Success() throws SQLException {
		// Simulate a successful database insertion (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the create method and assert that no exception is thrown
		assertDoesNotThrow(() -> eventCommentFactory.create(1, 1, "content"));
	}

	@Test
	void CreateEventComment_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> eventCommentFactory.create(1, 1, "content"));
	}
}

