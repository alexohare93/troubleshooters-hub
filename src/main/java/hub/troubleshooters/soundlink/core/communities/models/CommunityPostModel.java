package hub.troubleshooters.soundlink.core.communities.models;

import java.sql.Date;

public record CommunityPostModel(int postId, int communityId, int userId, String title, String content, Date created) {
}
