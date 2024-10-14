package hub.troubleshooters.soundlink.core.profile.models;

import hub.troubleshooters.soundlink.data.models.Image;

import java.util.Date;
import java.util.Optional;

public record UserProfileModel(int id, int userId, String displayName, String bio, Optional<Image> profileImage) {
}
