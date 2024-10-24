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

/**
 * Implementation of the {@link UserProfileService} interface for managing user profiles.
 *
 * <p>This service handles the retrieval and updating of user profiles, including validation and
 * image uploads. It interacts with the {@link UserProfileFactory} for database operations and uses the
 * {@link ImageUploaderService} for managing profile images.</p>
 */
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileFactory userProfileFactory;
    private final UserProfileValidator validator;
    private final ImageUploaderService imageUploaderService;

    /**
     * Constructs a new instance of {@link UserProfileServiceImpl}.
     *
     * @param userProfileFactory The factory for performing database operations on user profiles.
     * @param validator          The validator used for validating profile updates.
     * @param imageUploaderService The service used for handling profile image uploads.
     */
    @Inject
    public UserProfileServiceImpl(UserProfileFactory userProfileFactory, UserProfileValidator validator, ImageUploaderService imageUploaderService) {
        this.userProfileFactory = userProfileFactory;
        this.validator = validator;
        this.imageUploaderService = imageUploaderService;
    }

    /**
     * Retrieves the user profile for the given user ID.
     *
     * <p>This method fetches the {@link UserProfile} associated with the provided user ID using the
     * {@link UserProfileFactory}. If an SQL exception occurs, an empty {@link Optional} is returned.</p>
     *
     * @param userId The ID of the user whose profile is being retrieved.
     * @return An {@link Optional} containing the {@link UserProfile} if found, or an empty {@link Optional} if not.
     */
    @Override
    public Optional<UserProfile> getUserProfile(int userId) {
        try {
            return userProfileFactory.getByUserId(userId);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    /**
     * Updates the user profile based on the provided {@link UserProfileUpdateModel}.
     *
     * <p>This method validates the update model and then proceeds to update the user profile in the database.
     * If a new profile image is provided, it is uploaded via the {@link ImageUploaderService}, and the image
     * ID is set on the user profile. Otherwise, the image is cleared.</p>
     *
     * @param model  The {@link UserProfileUpdateModel} containing the updated user profile details.
     * @param userId The ID of the user whose profile is being updated.
     * @return A {@link ValidationResult} object indicating success or containing any validation errors.
     */
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
        } else {
            userProfile.setProfileImageId(null);
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
