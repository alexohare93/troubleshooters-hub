package hub.troubleshooters.soundlink.core.validation;

import hub.troubleshooters.soundlink.core.CoreResult;

import java.util.*;

public class ValidationResult extends CoreResult<Void, ValidationError> {

    private List<ValidationError> errors = new ArrayList<>();

    public ValidationResult(ValidationError error) {
        super(error);
        errors = Collections.singletonList(error);
    }

    public ValidationResult(List<ValidationError> errors) {
        super(errors.isEmpty() ? null : new ValidationError(errors));
        this.errors = errors;
    }

    public ValidationResult() {
        super((Void) null);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
