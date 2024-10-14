package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Booking;
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

public class BookingFactoryTest {

	@Mock
	private DatabaseConnection databaseConnection;

	@InjectMocks
	private BookingFactory bookingFactory;

	private Booking booking;
	private User user;
	private Event event;

	@BeforeEach
	void setUp() {
		// Initialize mocks and open them for use in tests
		MockitoAnnotations.openMocks(this);

		// Initialize a Booking object with mock data
		booking = new Booking(1, 1, 1, new Date(), 2);

		// Initialize a User object with mock data
		user = new User(1, "username", "hashed_password", new Date(), new Date());

		// Initialise an Event object with mock data
		event = new Event(1, 1, "title", "description", "venue", 100, new Date(), new Date(), null);
	}

	@Test
	void testSaveBooking_Success() throws SQLException {
		// Simulate a successful database update (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that no exception is thrown
		assertDoesNotThrow(() -> bookingFactory.save(booking));
	}

	@Test
	void testSaveBooking_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> bookingFactory.save(booking));
	}

	@Test
	void testGetBookingById_Success() throws SQLException {
		// Mock the database query to return a specific Booking
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(booking);

		// Call the method to retrieve a Booking by ID
		var result = bookingFactory.get(1);

		// Assert that the result is present and matches the expected Booking
		assertTrue(result.isPresent());
		assertEquals(booking, result.get());
	}

	@Test
	void testGetBookingById_NotFound() throws SQLException {
		// Mock the database query to return null (indicating no result found)
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

		// Call the method to retrieve a Booking by an ID that doesn't exist
		var result = bookingFactory.get(999);

		// Assert that no result is found (empty Optional)
		assertFalse(result.isPresent());
	}

	@Test
	void testGetBookingByUserIdAndEventId_Success() throws SQLException {
		// Mock the database query to return a specific Booking
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(booking);

		// Call the method to retrieve a Booking by UserId and EventId
		var result = bookingFactory.get(1, 1);

		// Assert that the result is present and matches the expected Booking
		assertTrue(result.isPresent());
		assertEquals(booking, result.get());
	}

	@Test
	void testGetBookingByUserIdAndEventId_NotFound() throws SQLException {

		// Mock the database query to return a specific Booking
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

		// Call the method to retrieve a Booking by non-existent UserId and EventId
		var result = bookingFactory.get(999, 999);

		// Assert that no result is found (empty Optional)
		assertFalse(result.isPresent());
	}

	@Test
	void testGetBookingsByUser_Success() throws SQLException {
		// Mock the database query to return a list containing one Booking
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(List.of(booking));

		// Call the method to retrieve all Bookings for the given User
		var result = bookingFactory.get(user);

		// Assert that the result contains the expected Booking
		assertEquals(1, result.size());
		assertTrue(result.contains(booking));
	}

	@Test
	void testGetBookingsByUser_NotFound() throws SQLException {
		// Mock the database query to return an empty list
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(new ArrayList<Booking>());

		// Call method to retrieve all Event Attendees for this non-existent User
		var result = bookingFactory.get(user);

		// Assert that the result contains the expected Booking
		assertEquals(0, result.size());
	}

	@Test
	void testGetBookingsByEvent_Success() throws SQLException {
		// Mock the database query to return a list containing one Booking
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(List.of(booking));

		// Call the method to retrieve all Bookings for the given Event
		var result = bookingFactory.get(event);

		// Assert that the result contains the expected Booking
		assertEquals(1, result.size());
		assertTrue(result.contains(booking));
	}

	@Test
	void testGetBookingsByEvent_NotFound() throws SQLException {
		// Mock the database query to return an empty list
		when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(new ArrayList<Booking>());

		// Call method to retrieve all Event Attendees for this non-existent Event
		var result = bookingFactory.get(event);

		// Assert that the result contains the expected Booking
		assertEquals(0, result.size());
	}

	@Test
	void CreateBooking_Success() throws SQLException {
		// Simulate a successful database insertion (1 row affected)
		doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the create method and assert that no exception is thrown
		assertDoesNotThrow(() -> bookingFactory.create(1, 1, 2));
	}

	@Test
	void CreateBooking_Failure() throws SQLException {
		// Simulate an SQLException being thrown during the update
		doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

		// Call the save method and assert that an SQLException is thrown
		assertThrows(SQLException.class, () -> bookingFactory.create(1, 1, 2));
	}
}

