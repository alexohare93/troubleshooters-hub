package hub.troubleshooters.soundlink.data.factories;

import hub.troubleshooters.soundlink.data.StatementPreparer;
import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.models.Event;
import hub.troubleshooters.soundlink.data.factories.EventFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventFactoryTest {

    @Mock
    private DatabaseConnection mockConnection;

    @InjectMocks
    private EventFactory eventFactory;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testEvent = new Event(1, 1, "Music Event", "A live music concert", "The Venue", 100, new Date(), new Date());
    }

    @Test
    void testSave() throws SQLException {
        // Arrange
        doNothing().when(mockConnection).executeUpdate(anyString(), any(), any());

        // Act
        eventFactory.save(testEvent);

        // Assert
        ArgumentCaptor<StatementPreparer> captor = ArgumentCaptor.forClass(StatementPreparer.class);
        verify(mockConnection).executeUpdate(eq("UPDATE Events SET CommunityId = ?, Name = ?, Description = ?, Venue = ?, Capacity = ?, Scheduled = ?, Created = ? WHERE Id = ?"), captor.capture(), any());

        // Verify the correct values were set in the prepared statement
        StatementPreparer preparer = captor.getValue();
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        preparer.prepare(mockStatement);
        verify(mockStatement).setInt(1, testEvent.getCommunityId());
        verify(mockStatement).setString(2, testEvent.getName());
        verify(mockStatement).setString(3, testEvent.getDescription());
        verify(mockStatement).setString(4, testEvent.getVenue());
        verify(mockStatement).setInt(5, testEvent.getCapacity());
        verify(mockStatement).setDate(6, new java.sql.Date(testEvent.getScheduled().getTime()));
        verify(mockStatement).setDate(7, new java.sql.Date(testEvent.getCreated().getTime()));
        verify(mockStatement).setInt(8, testEvent.getId());
    }

    @Test
    void testGet_EventFound() throws SQLException {
        // Arrange
        Event testEvent = new Event(1, 1, "Test Event", "A test description", "Test Venue", 200, new java.util.Date(), new java.util.Date());

        // Mock the executeQuery method to return the testEvent
        when(mockConnection.executeQuery(anyString(), any(), any())).thenReturn(testEvent);

        // Act
        Optional<Event> result = eventFactory.get(testEvent.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testEvent.getId(), result.get().getId());
        assertEquals(testEvent.getCommunityId(), result.get().getCommunityId());
        assertEquals(testEvent.getName(), result.get().getName());
        assertEquals(testEvent.getDescription(), result.get().getDescription());
        assertEquals(testEvent.getVenue(), result.get().getVenue());
        assertEquals(testEvent.getCapacity(), result.get().getCapacity());
        assertEquals(testEvent.getScheduled(), result.get().getScheduled());
        assertEquals(testEvent.getCreated(), result.get().getCreated());
    }

    @Test
    void testGet_EventNotFound() throws SQLException {
        // Arrange
        when(mockConnection.executeQuery(anyString(), any(), any())).thenReturn(null);

        // Act
        Optional<Event> result = eventFactory.get(999);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCreate() throws SQLException {
        // Arrange
        doNothing().when(mockConnection).executeUpdate(anyString(), any(), any());

        // Act
        eventFactory.create(testEvent);

        // Assert
        ArgumentCaptor<StatementPreparer> captor = ArgumentCaptor.forClass(StatementPreparer.class);
        verify(mockConnection).executeUpdate(eq("INSERT INTO Events (CommunityId, Name, Description, Venue, Capacity, Scheduled, Created) VALUES (?, ?, ?, ? ,? ,?, ?)"), captor.capture(), any());

        // Verify the correct values were set in the prepared statement
        StatementPreparer preparer = captor.getValue();
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        preparer.prepare(mockStatement);
        verify(mockStatement).setInt(1, testEvent.getCommunityId());
        verify(mockStatement).setString(2, testEvent.getName());
        verify(mockStatement).setString(3, testEvent.getDescription());
        verify(mockStatement).setString(4, testEvent.getVenue());
        verify(mockStatement).setInt(5, testEvent.getCapacity());
        verify(mockStatement).setDate(6, new java.sql.Date(testEvent.getScheduled().getTime()));
        verify(mockStatement).setDate(7, new java.sql.Date(testEvent.getCreated().getTime()));
    }
}

