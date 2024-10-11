package hub.troubleshooters.soundlink.core.communities.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.communities.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.factories.CommunityMemberFactory;
import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import hub.troubleshooters.soundlink.core.validation.ModelValidator;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.core.communities.validation.CreateCommunityModelValidator;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommunityServiceImpl implements CommunityService {

    private static final Logger LOGGER = Logger.getLogger(CommunityServiceImpl.class.getName());

    private final CommunityFactory communityFactory;

    private final CommunityMemberFactory communityMemberFactory;

    private final CreateCommunityModelValidator createCommunityModelValidator;

    private final ImageUploaderService imageUploaderService;

    @Inject
    public CommunityServiceImpl(CommunityFactory communityFactory, CommunityMemberFactory communityMemberFactory, CreateCommunityModelValidator createCommunityModelValidator, ImageUploaderService imageUploaderService) {
        this.communityFactory = communityFactory;
        this.communityMemberFactory = communityMemberFactory;
        this.createCommunityModelValidator = createCommunityModelValidator;
        this.imageUploaderService = imageUploaderService;
    }

    @Override
    public ValidationResult createCommunity(CreateCommunityModel model) {

        // validate
        var result = createCommunityModelValidator.validate(model);
        if (!result.isSuccess()) {
            return result;
        }

        try {
            imageUploaderService.upload(model.bannerImage());
            Community community = new Community(
                    0, model.name(), model.description(), model.genre(), null
            );
            communityFactory.create(community);
        } catch (SQLException | IOException e) {
            return new ValidationResult(new ValidationError("Internal error: please contact SoundLink Support."));
        }
        return new ValidationResult();
    }

    @Override
    public List<Community> searchCommunities(String searchText) {
        try {
            List<Community> communities = communityFactory.getAllCommunities();

            // Filter based on name, description, or genre
            return communities.stream()
                    .filter(community -> searchText == null || searchText.isEmpty() ||
                            community.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            community.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                            community.getGenre().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching communities", e);
            return List.of();
        }
    }

    @Override
    public boolean signUpForCommunity(int userId, int communityId) throws SQLException {
        // Check if the user is already signed up for the community
        Optional<CommunityMember> existingMember = communityMemberFactory.get(userId);

        if (existingMember.isPresent()) {
            return false;
        } else {
            int permission = 6;  // can change this
            communityMemberFactory.create(communityId, userId, permission);
            return true;
        }
    }
}
