package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.EventAttendee;
import hub.troubleshooters.soundlink.data.models.User;
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

public class EventAttendeeFactoryTest {

	@Mock
	private DatabaseConnection databaseConnection;

	@InjectMocks
	private EventAttendeeFactory eventAttendeeFactory;

	private EventAttendee eventAttendee;
	private User user;
	private Event event;

	@BeforeEach
	void setUp() {
		// Initialize mocks and open them for use in tests
		MockitoAnnotations.openMocks(this);

		// Initialize a EventAttendee object with mock data
		eventAttendee = new EventAttendee(1, 1, 1, new Date(), 2);

		// Initialize a User object with mock data
		user = new User(1, "username", "hashed_password", new Date(), new Date());

		// Initialise an Event object with mock data
		event = new Event(1, 1, "title", "description", "venue", 100, new Date(), new Date(), null);
	}

	@Test
	void testSaveEventAttendee_Success() throws SQLException {
		// Simulate a successful database update (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that no exception is thrown
		assertDoesNotThrow(() -> eventAttendeeFactory.save(eventAttendee));
	}

	@Test
	void testSaveEventAttendee_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> eventAttendeeFactory.save(eventAttendee));
	}

	@Test
	void testGetEventAttendeeById_Success() throws SQLException {
		// Mock the database query to return a specific EventAttendee
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(eventAttendee);

		// Call the method to retrieve a EventAttendee by ID
		var result = eventAttendeeFactory.get(1);

		// Assert that the result is present and matches the expected EventAttendee
		assertTrue(result.isPresent());
		assertEquals(eventAttendee, result.get());
	}

	@Test
	void testGetEventAttendeeById_NotFound() throws SQLException {
		// Mock the database query to return null (indicating no result found)
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

		// Call the method to retrieve a EventAttendee by an ID that doesn't exist
		var result = eventAttendeeFactory.get(999);

		// Assert that no result is found (empty Optional)
		assertFalse(result.isPresent());
	}

	@Test
	void testGetEventAttendeeByUserIdAndEventId_Success() throws SQLException {
		// Mock the database query to return a specific EventAttendee
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(eventAttendee);

		// Call the method to retrieve a EventAttendee by UserId and EventId
		var result = eventAttendeeFactory.get(1, 1);

		// Assert that the result is present and matches the expected EventAttendee
		assertTrue(result.isPresent());
		assertEquals(eventAttendee, result.get());
	}

	@Test
	void testGetEventAttendeeByUserIdAndEventId_NotFound() throws SQLException {

		// Mock the database query to return a specific EventAttendee
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

		// Call the method to retrieve a EventAttendee by non-existent UserId and EventId
		var result = eventAttendeeFactory.get(999, 999);

		// Assert that no result is found (empty Optional)
		assertFalse(result.isPresent());
	}

	@Test
	void testGetEventAttendeesByUser_Success() throws SQLException {
		// Mock the database query to return a list containing one EventAttendee
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(List.of(eventAttendee));

		// Call the method to retrieve all EventAttendees for the given User
		var result = eventAttendeeFactory.get(user);

		// Assert that the result contains the expected EventAttendee
		assertEquals(1, result.size());
		assertTrue(result.contains(eventAttendee));
	}

	@Test
	void testGetEventAttendeesByUser_NotFound() throws SQLException {
		// Mock the database query to return an empty list
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(new ArrayList<EventAttendee>());

		// Call method to retrieve all Event Attendees for this non-existent User
		var result = eventAttendeeFactory.get(user);

		// Assert that the result contains the expected EventAttendee
		assertEquals(0, result.size());
	}

	@Test
	void testGetEventAttendeesByEvent_Success() throws SQLException {
		// Mock the database query to return a list containing one EventAttendee
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(List.of(eventAttendee));

		// Call the method to retrieve all EventAttendees for the given Event
		var result = eventAttendeeFactory.get(event);

		// Assert that the result contains the expected EventAttendee
		assertEquals(1, result.size());
		assertTrue(result.contains(eventAttendee));
	}

	@Test
	void testGetEventAttendeesByEvent_NotFound() throws SQLException {
		// Mock the database query to return an empty list
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(new ArrayList<EventAttendee>());

		// Call method to retrieve all Event Attendees for this non-existent Event
		var result = eventAttendeeFactory.get(event);

		// Assert that the result contains the expected EventAttendee
		assertEquals(0, result.size());
	}

	@Test
	void CreateEventAttendee_Success() throws SQLException {
		// Simulate a successful database insertion (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the create method and assert that no exception is thrown
		assertDoesNotThrow(() -> eventAttendeeFactory.create(1, 1, 2));
	}

	@Test
	void CreateEventAttendee_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> eventAttendeeFactory.create(1, 1, 2));
	}
}

