package hub.troubleshooters.soundlink.core.auth.services;

import java.util.Arrays;
import java.util.List;

public class CommunityService {
    public List<String> getCommunitiesForUser(Long userId) {
        return Arrays.asList(
                "Jazz Lovers Community",
                "Indie Rock Fans",
                "Music Producers Group"
        );
    }
}
