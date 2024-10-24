package hub.troubleshooters.soundlink.core.profile.services;

import hub.troubleshooters.soundlink.core.profile.models.UserProfileUpdateModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.UserProfile;

import java.util.Optional;

/**
 * Service interface for managing user profiles.
 *
 * <p>This interface defines methods for retrieving and updating user profiles in the system. It provides
 * functionality to get a user profile by the user ID and update a user profile based on a model.</p>
 */
public interface UserProfileService {

    /**
     * Retrieves the user profile for the given user ID.
     *
     * <p>This method fetches the {@link UserProfile} object associated with the provided user ID. If no profile
     * is found, it returns an empty {@link Optional}.</p>
     *
     * @param userId The ID of the user whose profile is being retrieved.
     * @return An {@link Optional} containing the {@link UserProfile} if found, or an empty {@link Optional} if not.
     */
    Optional<UserProfile> getUserProfile(int userId);

    /**
     * Updates the user profile based on the provided {@link UserProfileUpdateModel}.
     *
     * <p>This method performs validation on the model and updates the user profile if the validation passes.
     * It accepts a {@link UserProfileUpdateModel} that contains the updated profile information and the user ID
     * of the profile to update.</p>
     *
     * @param model  The {@link UserProfileUpdateModel} containing the updated user profile details.
     * @param userId The ID of the user whose profile is being updated.
     * @return A {@link ValidationResult} object indicating success or containing any validation errors.
     */
    ValidationResult update(UserProfileUpdateModel model, int userId);
}