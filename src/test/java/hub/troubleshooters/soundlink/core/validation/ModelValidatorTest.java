package hub.troubleshooters.soundlink.core.validation;

import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.models.Community;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelValidatorTest {

    private ModelValidator<Object> validator;

    private Community community;

    @BeforeEach
    void setUp() {
        validator = new ModelValidator<Object>() {
            @Override
            public ValidationResult validate(Object model) {
                return null;
            }
        };
    }

    @Test
    void testNotEmptySuccess() {
        Optional<ValidationError> result = validator.notEmpty("name", "valid value");
        assertTrue(result.isEmpty(), "Expected no error for a non-empty string.");
    }

    @Test
    void testNotEmptyFailure() {
        Optional<ValidationError> result = validator.notEmpty("name", "");
        assertTrue(result.isPresent(), "Expected error for an empty string.");
        assertEquals("name is null or empty", result.get().getMessage());
    }

    @Test
    void testNotEmptyNull() {
        Optional<ValidationError> result = validator.notEmpty("name", null);
        assertTrue(result.isPresent(), "Expected error for a null string.");
        assertEquals("name is null or empty", result.get().getMessage());
    }

    @Test
    void testNotNullSuccess() {
        Optional<ValidationError> result = validator.notNull("object", new Object());
        assertTrue(result.isEmpty(), "Expected no error for a non-null object.");
    }

    @Test
    void testNotNullFailure() {
        Optional<ValidationError> result = validator.notNull("object", null);
        assertTrue(result.isPresent(), "Expected error for a null object.");
        assertEquals("object is null", result.get().getMessage());
    }

    @Test
    void testIsPastSuccess() {
        Date pastDate = Date.from(Instant.now().minusSeconds(60));
        Optional<ValidationError> result = validator.isPast("date", pastDate);
        assertTrue(result.isEmpty(), "Expected no error for a past date.");
    }

    @Test
    void testIsPastFailure() {
        Date futureDate = Date.from(Instant.now().plusSeconds(60));
        Optional<ValidationError> result = validator.isPast("date", futureDate);
        assertTrue(result.isPresent(), "Expected error for a future date.");
        assertEquals("date is not in the past", result.get().getMessage());
    }

    @Test
    void testIsFutureSuccess() {
        Date futureDate = Date.from(Instant.now().plusSeconds(60));
        Optional<ValidationError> result = validator.isFuture("date", futureDate);
        assertTrue(result.isEmpty(), "Expected no error for a future date.");
    }

    @Test
    void testIsFutureFailure() {
        Date pastDate = Date.from(Instant.now().minusSeconds(60));
        Optional<ValidationError> result = validator.isFuture("date", pastDate);
        assertTrue(result.isPresent(), "Expected error for a past date.");
        assertEquals("date is not in the future", result.get().getMessage());
    }

    @Test
    void testIsPositiveSuccess() {
        Optional<ValidationError> result = validator.isPositive("number", 10);
        assertTrue(result.isEmpty(), "Expected no error for a positive number.");
    }

    @Test
    void testIsPositiveFailure() {
        Optional<ValidationError> result = validator.isPositive("number", -5);
        assertTrue(result.isPresent(), "Expected error for a non-positive number.");
        assertEquals("number is not greater than 0", result.get().getMessage());
    }
  
    @Test
    void testCommunityExistsSuccess() throws SQLException {
        community = new Community(1, "test", "test", "test", Date.from(Instant.now()));
        CommunityFactory communityFactory = mock(CommunityFactory.class);
        when(communityFactory.get(1)).thenReturn(Optional.of(community));
        Optional<ValidationError> result = validator.communityExists(communityFactory, 1);
        assertTrue(result.isEmpty(), "Expected no error for an existing community.");
    }

    @Test
    void testCommunityExistsFailure() throws SQLException {
        CommunityFactory communityFactory = mock(CommunityFactory.class);
        when(communityFactory.get(1)).thenReturn(Optional.empty());

        Optional<ValidationError> result = validator.communityExists(communityFactory, 1);
        assertTrue(result.isPresent(), "Expected error for a non-existing community.");
        assertEquals("Community not found with id: 1", result.get().getMessage());
    }

    @Test
    void testCommunityExistsSQLException() throws SQLException {
        CommunityFactory communityFactory = mock(CommunityFactory.class);
        when(communityFactory.get(1)).thenThrow(new SQLException("Database error"));

        Optional<ValidationError> result = validator.communityExists(communityFactory, 1);
        assertTrue(result.isPresent(), "Expected error due to SQLException.");
        assertEquals("Internal error: Database error", result.get().getMessage());
    }
}

