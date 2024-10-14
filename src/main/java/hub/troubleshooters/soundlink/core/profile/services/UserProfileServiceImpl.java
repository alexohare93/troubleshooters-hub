package hub.troubleshooters.soundlink.core.profile.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileUpdateModel;
import hub.troubleshooters.soundlink.core.profile.validation.UserProfileValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.factories.UserProfileFactory;
import hub.troubleshooters.soundlink.data.models.UserProfile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileFactory userProfileFactory;
    private final UserProfileValidator validator;
    private final ImageUploaderService imageUploaderService;

    @Inject
    public UserProfileServiceImpl(UserProfileFactory userProfileFactory, UserProfileValidator validator, ImageUploaderService imageUploaderService) {
        this.userProfileFactory = userProfileFactory;
        this.validator = validator;
        this.imageUploaderService = imageUploaderService;
    }

    @Override
    public Optional<UserProfile> getUserProfile(int userId) {
        try {
            return userProfileFactory.getByUserId(userId);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public ValidationResult update(UserProfileUpdateModel model, int userId) {
        var result = validator.validate(model);
        if (!result.isSuccess()) {
            return result;
        }

        var userProfile = getUserProfile(userId).get();     // todo: error handling
        userProfile.setDisplayName(model.displayName());
        userProfile.setBio(model.bio());

        // save image
        if (model.profileImage() != null) {
            try {
                var img = imageUploaderService.upload(model.profileImage());
                userProfile.setProfileImageId(img.getId());
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);  // todo: error handling
            }
        }

        // save profile
        try {
            userProfileFactory.save(userProfile);
        } catch (SQLException e) {
            throw new RuntimeException(e);      // todo: error handling
        }

        return result;
    }
}
