package hub.troubleshooters.soundlink.core.profile.models;

import java.util.Date;

public record UserModel(int id, String username, Date create, Date LastLogin) {
}
