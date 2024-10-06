package hub.troubleshooters.soundlink.core.events.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.events.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.core.events.services.CommunityService;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommunityServiceImpl implements CommunityService {

    private static final Logger LOGGER = Logger.getLogger(CommunityServiceImpl.class.getName());

    private final CommunityFactory communityFactory;

    @Inject
    public CommunityServiceImpl(CommunityFactory communityFactory) {
        this.communityFactory = communityFactory;
    }

    @Override
    public void createCommunity(CreateCommunityModel model) {
        try {
            Community community = new Community(
                    model.id(), model.name(), model.description(), model.genre(), model.created()
            );
            communityFactory.create(community);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating community", e);
        }
    }

    @Override
    public List<Community> searchCommunities(String genre, String location) {
        try {
            List<Community> communities = communityFactory.getAllCommunities();

            return communities.stream()
                    .filter(community -> (genre == null || genre.isEmpty() || community.getGenre().equalsIgnoreCase(genre)))
                    .filter(community -> (location == null || location.isEmpty() || community.getDescription().contains(location)))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching communities", e);
            return List.of();
        }
    }
}
