package hub.troubleshooters.soundlink.core;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.communities.models.CommunityPostModel;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.profile.models.UserModel;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.factories.EventFactory;
import hub.troubleshooters.soundlink.data.factories.ImageFactory;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.models.*;

import java.sql.SQLException;
import java.util.Optional;

public class Map {
    private final EventFactory eventFactory;
    private final ImageFactory imageFactory;
    private final CommunityFactory communityFactory;
    private final UserFactory userFactory;

    @Inject
    public Map(EventFactory eventFactory, ImageFactory imageFactory, CommunityFactory communityFactory, UserFactory userFactory) {
        this.eventFactory = eventFactory;
        this.imageFactory = imageFactory;
        this.communityFactory = communityFactory;
		this.userFactory = userFactory;
	}

    public EventModel event(Event event) throws SQLException {
        var community = communityFactory.get(event.getCommunityId()).get();     // not catching these since they should be fatal errors

        // banner image id can be NULL
        Optional<Image> imageOpt = Optional.empty();
        if (event.getBannerImageId().isPresent()) {
            imageOpt = imageFactory.get(event.getBannerImageId().get());
        }

        return new EventModel(event.getId(), event.getName(), event.getDescription(), community, event.getVenue(), event.getCapacity(), event.getScheduled(), event.getCreated(), imageOpt);
    }

    public CommunityModel community(Community community) throws SQLException {

        // banner image id can be NULL
        Optional<Image> imageOpt = Optional.empty();
        if (community.getBannerImageId().isPresent()) {
            imageOpt = imageFactory.get(community.getBannerImageId().get());
        }

        return new CommunityModel(community.getId(),community.getName(), community.getDescription(), community.getGenre(), community.getCreated(), imageOpt);
    }

    public UserModel user(User user) throws SQLException {
        return new UserModel(user.getId(), user.getUsername(), user.getCreated(), user.getLastLogin());
    }

    public CommunityPostModel communityPost(CommunityPost post) throws SQLException {
        Optional<User> user = userFactory.get(post.getUserid());
        Optional<Community> community = communityFactory.get(post.getCommunityId());
        if (user.isEmpty()) throw new SQLException("Failed to get User");
        if (community.isEmpty()) throw new SQLException("Failed to get Community");
        UserModel userModel = user(user.get());
        CommunityModel communityModel = community(community.get());

        return new CommunityPostModel(post.getId(), communityModel, userModel, post.getTitle(), post.getContent(), post.getCreated());
    }


}
