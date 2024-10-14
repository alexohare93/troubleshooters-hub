package hub.troubleshooters.soundlink.core.profile.models;

import java.io.File;

public record UserProfileUpdateModel(int id, String displayName, String bio, File profileImage) {

}
