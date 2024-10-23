package hub.troubleshooters.soundlink.core.validation;

import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.io.File;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Class responsible for model validation.
 *
 * @param <T> The type of the model to be validated.
 */
public abstract class ModelValidator<T> {

    /**
     * Validates a given model using rules that have been specified.
     * @param model the model that is being validated
     * @return a ValidationResult which can contain potentially many validation errors that occurred during validation.
     */
    public abstract ValidationResult validate(T model);

    public interface ValidationFunction {
        Optional<ValidationError> apply(String name, Object value);
    }

    protected final Optional<ValidationError> ifPresent(String name, Object value, ValidationFunction function) {
        return value == null ? Optional.empty() : function.apply(name, value);
    }

    protected final Optional<ValidationError> isImage(String name, File value) {
        if (value == null) {
            return Optional.of(new ValidationError(name + " is null"));
        }

        if (value.getName().toLowerCase().endsWith(".png") || value.getName().toLowerCase().endsWith(".jpg") || value.getName().toLowerCase().endsWith(".jpeg")) {
            return Optional.empty();
        } else {
            return Optional.of(new ValidationError(name + " is not an image"));
        }
    }

    /**
     * Performs a list of checks and ensure they all pass. If any does not pass, then it is added to the returning ValidationResult's error list.
     * @param checks a list of checks to be performed, e.g. notEmpty(), isFuture(), etc.
     * @return An empty ValidationResult if all the checks succeeded, else a ValidationResult with errors
     */
    @SafeVarargs
    protected final ValidationResult ensure(Optional<ValidationError>... checks) {
        List<ValidationError> errors = new ArrayList<>();
        for (var check : checks) {
            check.ifPresent(errors::add);
        }
        return new ValidationResult(errors);
    }

    /**
     * Ensures a string is not null or empty (== "")
     * @param name The name of the property displayed in the ValidationError
     * @param value The String value being checked
     * @return Optional.empty() if validation succeeds, else an Optional.of the validation error.
     */
    protected final Optional<ValidationError> notEmpty(String name, String value) {
        return value != null && !value.isEmpty() ? Optional.empty() : Optional.of(new ValidationError(name + " is null or empty"));
    }

    /**
     * Ensures an object is not null
     * @param name The name of the property displayed in the ValidationError
     * @param value The Object value being checked
     * @return Optional.empty() if validation succeeds, else an Optional.of the validation error.
     */
    protected final Optional<ValidationError> notNull(String name, Object value) {
        return value != null ? Optional.empty() : Optional.of(new ValidationError(name + " is null"));
    }

    /**
     * Ensures a Date is not null and is in the past
     * @param name The name of the property displayed in the ValidationError
     * @param value The Date value being checked
     * @return Optional.empty() if validation succeeds, else an Optional.of the validation error.
     */
    protected final Optional<ValidationError> isPast(String name, Date value) {
        if (value == null) {
            return Optional.of(new ValidationError(name + " is null"));
        }
        return value.before(Date.from(Instant.now())) ? Optional.empty() : Optional.of(new ValidationError(name + " is not in the past"));
    }

    /**
     * Ensures a Date is not null and is in the future
     * @param name The name of the property displayed in the ValidationError
     * @param value The Date value being checked
     * @return Optional.empty() if validation succeeds, else an Optional.of the validation error.
     */
    protected final Optional<ValidationError> isFuture(String name, Date value) {
        if (value == null) {
            return Optional.of(new ValidationError(name + " is null"));
        }
        return value.after(Date.from(Instant.now())) ? Optional.empty() : Optional.of(new ValidationError(name + " is not in the future"));
    }

    /**
     * Ensures an int is greater than 0
     * @param name The name of the property displayed in the ValidationError
     * @param value The int value being checked
     * @return Optional.empty() if validation succeeds, else an Optional.of the validation error.
     */
    protected final Optional<ValidationError> isPositive(String name, int value) {
        return value > 0 ? Optional.empty() : Optional.of(new ValidationError(name + " is not greater than 0"));
    }

    /**
     * Ensures a community exists given a community ID and an injected CommunityFactory
     * @param communityFactory an implementation of CommunityFactory to be injected
     * @param communityId The ID of the community being checked
     * @return Optional.empty() if validation succeeds, else an Optional.of the validation error.
     */
    protected final Optional<ValidationError> communityExists(CommunityFactory communityFactory, int communityId) {
        try {
            var community = communityFactory.get(communityId);
            return community.isEmpty() ? Optional.of(new ValidationError("Community not found with id: " + communityId)) : Optional.empty();
        } catch (SQLException e) {
            return Optional.of(new ValidationError("Internal error: " + e.getMessage()));
        }
    }
}
