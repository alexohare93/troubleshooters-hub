package hub.troubleshooters.soundlink.core.auth.services;

import java.util.Arrays;
import java.util.List;

/**
 * Service class responsible for handling operations related to communities.
 * This class provides methods for managing community-related data for users.
 */
public class CommunityService {
    /**
     * Retrieves a list of community names that a given user is part of.
     * <p>
     * This is a placeholder method returning a static list of communities for demonstration purposes.
     * </p>
     *
     * @param userId The ID of the user whose communities are to be retrieved.
     * @return A list of community names associated with the specified user.
     */
    public List<String> getCommunitiesForUser(Long userId) {
        return Arrays.asList(
                "Jazz Lovers Community",
                "Indie Rock Fans",
                "Music Producers Group"
        );
    }
}
