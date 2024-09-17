package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Event;
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

public class EventFactoryTest {

    @Mock
    private DatabaseConnection databaseConnection;

    @InjectMocks
    private EventFactory eventFactory;

    private Event event;

    @BeforeEach
    void setUp() {
        // Initialize Mockito mocks
        MockitoAnnotations.openMocks(this);

        // Create a sample event for use in tests
        event = new Event(1, 1, "Event Name", "Description", "Venue", 100, new Date(), new Date());
    }

    @Test
    void testGetEventById_Success() throws SQLException {
        // Mock the database query to return the event when the given ID is found
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(event);

        // Call the method to get the event by its ID
        Optional<Event> result = eventFactory.get(1);

        // Verify that the event was found and matches the expected event
        assertTrue(result.isPresent());
        assertEquals(event, result.get());
    }

    @Test
    void testGetEventById_NotFound() throws SQLException {
        // Mock the database query to return null when no event is found
        when(databaseConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

        // Call the method to get the event by a non-existent ID
        Optional<Event> result = eventFactory.get(999);

        // Verify that no event was found
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveEvent_Success() throws SQLException {
        // Simulate a successful update with 1 row affected
        doAnswer(invocation -> 1).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Call the save method and verify that no exception is thrown
        assertDoesNotThrow(() -> eventFactory.save(event));
    }

    @Test
    void testSaveEvent_Failure() throws SQLException {
        // Simulate an SQLException being thrown during the update
        doThrow(new SQLException()).when(databaseConnection).executeUpdate(anyString(), any(), any());

        // Call the save method and expect an SQLException to be thrown
        assertThrows(SQLException.class, () -> eventFactory.save(event));
    }
}

