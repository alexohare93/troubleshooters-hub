package hub.troubleshooters.soundlink.core;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.communities.models.CommunityPostModel;
import hub.troubleshooters.soundlink.core.events.models.EventModel;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.profile.models.UserModel;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.models.*;

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

/**
 * The Map class provides utility methods to convert various data model objects into their corresponding
 * model representations, such as converting a {@link Event}, {@link Community}, {@link User}, or {@link CommunityPost}
 * into their respective model counterparts. This is particularly useful for mapping database entities to
 * application layer models.
 *
 * <p>It uses several factories, including {@link EventFactory}, {@link ImageFactory}, {@link CommunityFactory},
 * and {@link UserFactory}, to fetch related entities such as {@link Image} or {@link Community} for inclusion
 * in the mapped model.</p>
 *
 * <p>Most methods in this class throw {@link SQLException} as they involve database operations through the factory classes.</p>
 */
public class Map {
    private final EventFactory eventFactory;
    private final ImageFactory imageFactory;
    private final CommunityFactory communityFactory;
    private final UserFactory userFactory;

    /**
     * Constructs a new instance of the Map class with the necessary factories for object conversions.
     *
     * @param eventFactory      The factory to retrieve events from the database.
     * @param imageFactory      The factory to retrieve images from the database.
     * @param communityFactory  The factory to retrieve communities from the database.
     * @param userFactory       The factory to retrieve users from the database.
     */
    @Inject
    public Map(EventFactory eventFactory, ImageFactory imageFactory, CommunityFactory communityFactory, UserFactory userFactory) {
        this.eventFactory = eventFactory;
        this.imageFactory = imageFactory;
        this.communityFactory = communityFactory;
		this.userFactory = userFactory;
	}

    /**
     * Converts an {@link Event} into an {@link EventModel}.
     * This involves fetching related {@link Community} and optional {@link Image} data.
     *
     * @param event The event object to convert.
     * @return The converted event model.
     * @throws SQLException If an error occurs while fetching related entities.
     */
    public EventModel event(Event event) throws SQLException {
        var community = communityFactory.get(event.getCommunityId()).get();     // not catching these since they should be fatal errors

        // banner image id can be NULL
        Optional<Image> imageOpt = Optional.empty();
        if (event.getBannerImageId().isPresent()) {
            imageOpt = imageFactory.get(event.getBannerImageId().get());
        }

        return new EventModel(event.getId(), event.getName(), event.getDescription(), community, event.getVenue(), event.getCapacity(), event.getScheduled(), event.getCreated(), imageOpt);
    }

    /**
     * Converts a {@link Community} into a {@link CommunityModel}.
     * This involves fetching the optional {@link Image} data.
     *
     * @param community The community object to convert.
     * @return The converted community model.
     * @throws SQLException If an error occurs while fetching related entities.
     */
    public CommunityModel community(Community community) throws SQLException {

        // banner image id can be NULL
        Optional<Image> imageOpt = Optional.empty();
        if (community.getBannerImageId().isPresent()) {
            imageOpt = imageFactory.get(community.getBannerImageId().get());
        }

        return new CommunityModel(community.getId(),community.getName(), community.getDescription(), community.getGenre(), community.getCreated(), imageOpt, community.isPrivate());
    }

    /**
     * Converts a {@link User} into a {@link UserModel}.
     *
     * @param user The user object to convert.
     * @return The converted user model.
     * @throws SQLException If an error occurs while fetching related entities.
     */
    public UserModel user(User user) throws SQLException {
        return new UserModel(user.getId(), user.getUsername(), user.getCreated(), user.getLastLogin());
    }

    /**
     * Converts a {@link CommunityPost} into a {@link CommunityPostModel}.
     * This involves fetching related {@link User} and {@link Community} data.
     *
     * @param post The community post object to convert.
     * @return The converted community post model.
     * @throws SQLException If an error occurs while fetching related entities.
     */
    public CommunityPostModel communityPost(CommunityPost post) throws SQLException {
        Optional<User> user = userFactory.get(post.getUserid());
        Optional<Community> community = communityFactory.get(post.getCommunityId());
        if (user.isEmpty()) throw new SQLException("Failed to get User");
        if (community.isEmpty()) throw new SQLException("Failed to get Community");
        UserModel userModel = user(user.get());
        CommunityModel communityModel = community(community.get());

        return new CommunityPostModel(post.getId(), communityModel, userModel, post.getTitle(), post.getContent(), post.getCreated());
    }

    /**
     * Converts a {@link UserProfile} into a {@link UserProfileModel}.
     * This involves fetching the optional {@link Image} data.
     *
     * @param userProfile The user profile object to convert.
     * @return The converted user profile model.
     * @throws SQLException If an error occurs while fetching related entities.
     */
    public UserProfileModel userProfile(UserProfile userProfile) throws SQLException {

        // profile image ID can be NULL
        Optional<Image> imageOpt = Optional.empty();
        if (userProfile.getProfileImageId().isPresent()) {
            imageOpt = imageFactory.get(userProfile.getProfileImageId().get());
        }

        return new UserProfileModel(userProfile.getId(), userProfile.getUserId(), userProfile.getDisplayName(), userProfile.getBio(), imageOpt);
    }
}
