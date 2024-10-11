package hub.troubleshooters.soundlink.core.events.validation;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;

import java.io.File;
import java.util.Optional;

public class CreateCommunityModelValidator extends ModelValidator<CreateCommunityModel> {

    @Inject
    public CreateCommunityModelValidator() {
    }

    @Override
    public ValidationResult validate(CreateCommunityModel createCommunityModel) {
        if (createCommunityModel == null) {
            return new ValidationResult(new ValidationError("Model is null"));
        }

        return ensure(
                notEmpty("Name", createCommunityModel.name()),
                notEmpty("Description", createCommunityModel.description()),
                notEmpty("Genre", createCommunityModel.genre()),
                ifPresent("Banner image", createCommunityModel.bannerImage(), (name, value) -> isImage(name, (File) value))
        );
    }
}
