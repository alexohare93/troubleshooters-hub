package hub.troubleshooters.soundlink.core.data;

import hub.troubleshooters.soundlink.core.data.models.*;
import hub.troubleshooters.soundlink.data.models.*;

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

    /**
     * Maps an event to an event model.
     * @param event The event to map.
     * @return The event model.
     */
    public static EventModel eventModel(Event event) {
        return new EventModel(event.getId(), event.getCommunityId(), event.getName(), event.getDescription(), event.getVenue(), event.getCapacity(), event.getScheduled(), event.getCreated());
    }

    /**
     * Maps a community to a community model.
     * @param community The community to map.
     * @return The community model.
     */
    public static CommunityModel communityModel(Community community) {
        return new CommunityModel(community.getId(), community.getName(), community.getDescription(), community.getGenre(), community.getCreated());
    }
}
