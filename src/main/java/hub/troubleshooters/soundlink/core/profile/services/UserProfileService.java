package hub.troubleshooters.soundlink.core.profile.services;

import hub.troubleshooters.soundlink.data.models.UserProfile;

import java.util.Optional;

public interface UserProfileService {
    Optional<UserProfile> getUserProfile(int userId);
}
