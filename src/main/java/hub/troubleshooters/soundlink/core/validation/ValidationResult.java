package hub.troubleshooters.soundlink.core.validation;

import hub.troubleshooters.soundlink.core.CoreResult;

import java.util.*;

public class ValidationResult extends CoreResult<Void, ValidationException> {

    private List<ValidationException> errors;

    public ValidationResult(ValidationException error) {
        super(error);
        errors = Collections.singletonList(error);
    }

    public ValidationResult(List<ValidationException> errors) {
        super(new ValidationException(errors));
        this.errors = errors;
    }

    public ValidationResult() {
        super((Void) null);
    }

    public List<ValidationException> getErrors() {
        return errors;
    }
}
