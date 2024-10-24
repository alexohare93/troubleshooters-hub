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

    /**
     * Functional interface for applying validation logic.
     *
     * <p>This interface is used in conjunction with the {@link #ifPresent(String, Object, ValidationFunction)}
     * method to perform specific validation checks on fields that are not null.</p>
     */
    public interface ValidationFunction {
        /**
         * Applies a validation function to a value.
         *
         * @param name The name of the field being validated.
         * @param value The value of the field to validate.
         * @return An {@link Optional} containing a {@link ValidationError} if the validation fails, or {@link Optional#empty()} if the validation passes.
         */
        Optional<ValidationError> apply(String name, Object value);
    }

    /**
     * Applies a validation function to a value if it is present (not null).
     *
     * <p>This method allows for conditional validation. If the value is null, no validation will occur,
     * and an empty {@link Optional} will be returned. If the value is non-null, the provided validation
     * function will be executed.</p>
     *
     * @param name The name of the field being validated.
     * @param value The value of the field to validate.
     * @param function The validation function to apply if the value is present.
     * @return An {@link Optional} containing a {@link ValidationError} if the validation fails, or {@link Optional#empty()} if the validation passes.
     */
    protected final Optional<ValidationError> ifPresent(String name, Object value, ValidationFunction function) {
        return value == null ? Optional.empty() : function.apply(name, value);
    }

    /**
     * Checks whether the given file is a valid image.
     *
     * <p>This method checks if a file is a valid image based on its file extension. Valid image formats
     * include PNG, JPG, and JPEG. If the file is not an image, a {@link ValidationError} is returned.</p>
     *
     * @param name The name of the field being validated.
     * @param value The file to validate.
     * @return An {@link Optional} containing a {@link ValidationError} if the file is not an image, or {@link Optional#empty()} if it is a valid image.
     */
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
