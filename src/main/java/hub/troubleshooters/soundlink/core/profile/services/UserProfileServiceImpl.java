package hub.troubleshooters.soundlink.core.profile.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.factories.UserProfileFactory;
import hub.troubleshooters.soundlink.data.models.UserProfile;

import java.sql.SQLException;
import java.util.Optional;

public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileFactory userProfileFactory;

    @Inject
    public UserProfileServiceImpl(UserProfileFactory userProfileFactory) {
        this.userProfileFactory = userProfileFactory;
    }

    @Override
    public Optional<UserProfile> getUserProfile(int userId) {
        try {
            return userProfileFactory.getByUserId(userId);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
}
