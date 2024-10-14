package hub.troubleshooters.soundlink.core;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.profile.models.UserProfileModel;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.factories.EventFactory;
import hub.troubleshooters.soundlink.data.factories.ImageFactory;
import hub.troubleshooters.soundlink.data.models.Event;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.Image;
import hub.troubleshooters.soundlink.data.models.UserProfile;

import java.sql.SQLException;
import java.util.Optional;

public class Map {
    private final EventFactory eventFactory;
    private final ImageFactory imageFactory;
    private final CommunityFactory communityFactory;

    @Inject
    public Map(EventFactory eventFactory, ImageFactory imageFactory, CommunityFactory communityFactory) {
        this.eventFactory = eventFactory;
        this.imageFactory = imageFactory;
        this.communityFactory = communityFactory;
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

    public UserProfileModel userProfile(UserProfile userProfile) throws SQLException {

        // profile image ID can be NULL
        Optional<Image> imageOpt = Optional.empty();
        if (userProfile.getProfileImageId().isPresent()) {
            imageOpt = imageFactory.get(userProfile.getProfileImageId().get());
        }

        return new UserProfileModel(userProfile.getId(), userProfile.getUserId(), userProfile.getDisplayName(), userProfile.getBio(), imageOpt);
    }

}
