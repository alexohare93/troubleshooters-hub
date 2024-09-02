package hub.troubleshooters.soundlink.core.data;

import hub.troubleshooters.soundlink.core.data.models.UserModel;
import hub.troubleshooters.soundlink.data.models.User;

public class Map {
    public static UserModel userModel(User user) {
        return new UserModel(user.getId(), user.getUsername(), user.getCreated(), user.getLastLogin(), user.getPermission());
    }
}
