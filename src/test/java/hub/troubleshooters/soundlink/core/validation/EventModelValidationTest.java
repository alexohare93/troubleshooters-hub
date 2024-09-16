package hub.troubleshooters.soundlink.core.validation;

import hub.troubleshooters.soundlink.core.events.models.CreateEventModel;
import hub.troubleshooters.soundlink.core.events.validation.CreateEventModelValidator;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.models.Community;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class EventModelValidationTest {

    @Mock
    private CommunityFactory communityFactory;

    // providing our mocked dependencies to our service we are actually testing
    @InjectMocks
    private CreateEventModelValidator createEventModelValidator;

    private Community community;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // setting up test community
        community = new Community(1, "test", "test", "test", Date.from(Instant.now()));
    }

    @Test
    void testCreateEventModel_Valid() throws SQLException {
        var model = new CreateEventModel("Valid event", "Valid description", new Date(Instant.now().toEpochMilli() + 1000), "test location", 1, 1);

        when(communityFactory.get(model.communityId())).thenReturn(Optional.of(community));
        var result = createEventModelValidator.validate(model);

        assertTrue(result.isSuccess());
    }

    @Test
    void testCreateEventModel_Invalid() throws SQLException {
        var model = new CreateEventModel("", null, new Date(Instant.now().toEpochMilli() - 1000), null, -1, 0);
        when(communityFactory.get(model.communityId())).thenReturn(Optional.empty());
        var result = createEventModelValidator.validate(model);

        assertFalse(result.isSuccess());
        assertEquals(6, result.getErrors().size());
    }

    @Test
    void testCreateEventModel_NullInvalid() {
        var result = createEventModelValidator.validate(null);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getErrors().size());
    }
}
