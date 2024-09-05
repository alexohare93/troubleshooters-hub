package hub.troubleshooters.soundlink.core.data;

import hub.troubleshooters.soundlink.core.data.models.UserModel;
import hub.troubleshooters.soundlink.data.models.User;

/**
 * Contains methods for mapping between database entities and core business models.
 */
public class Map {
    /**
     * Maps a user to a user model.
     * @param user The user to map.
     * @return The user model.
     */
    public static UserModel userModel(User user) {
        return new UserModel(user.getId(), user.getUsername(), user.getCreated(), user.getLastLogin(), user.getPermission());
    }
}
