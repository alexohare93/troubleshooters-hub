package hub.troubleshooters.soundlink.core.communities.models;

import hub.troubleshooters.soundlink.core.profile.models.UserModel;

import java.util.Date;

public record CommunityPostModel(int id, CommunityModel community, UserModel user, String title, String content, Date created) {
}
