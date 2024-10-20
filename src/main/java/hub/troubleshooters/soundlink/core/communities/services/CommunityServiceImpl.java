package hub.troubleshooters.soundlink.core.communities.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.communities.models.CommunityPostModel;
import hub.troubleshooters.soundlink.core.communities.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.data.factories.CommunityFactory;
import hub.troubleshooters.soundlink.data.factories.CommunityMemberFactory;
import hub.troubleshooters.soundlink.data.factories.CommunityPostFactory;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.models.*;
import hub.troubleshooters.soundlink.core.validation.ValidationError;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.core.communities.validation.CreateCommunityModelValidator;
import hub.troubleshooters.soundlink.core.images.ImageUploaderService;
import hub.troubleshooters.soundlink.core.Map;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommunityServiceImpl implements CommunityService {

	private static final Logger LOGGER = Logger.getLogger(CommunityServiceImpl.class.getName());

	private final CommunityFactory communityFactory;

	private final CommunityPostFactory communityPostFactory;

	private final CommunityMemberFactory communityMemberFactory;

	private final UserFactory userFactory;

	private final CreateCommunityModelValidator createCommunityModelValidator;

	private final ImageUploaderService imageUploaderService;

	private final Map map;

	@Inject
	public CommunityServiceImpl(CommunityFactory communityFactory, CommunityPostFactory communityPostFactory,
								CommunityMemberFactory communityMemberFactory, UserFactory userFactory,
								CreateCommunityModelValidator createCommunityModelValidator,
								ImageUploaderService imageUploaderService, Map map) {
		this.communityFactory = communityFactory;
		this.communityPostFactory = communityPostFactory;
		this.communityMemberFactory = communityMemberFactory;
		this.userFactory = userFactory;
		this.createCommunityModelValidator = createCommunityModelValidator;
		this.imageUploaderService = imageUploaderService;
		this.map = map;
	}

	@Override
	public ValidationResult createCommunity(CreateCommunityModel model) {

		// validate
		var result = createCommunityModelValidator.validate(model);
		if (!result.isSuccess()) {
			return result;
		}

        try {
            if (model.bannerImage() != null) {
                var img = imageUploaderService.upload(model.bannerImage());
                Community community = new Community(0, model.name(), model.description(), model.genre(), null, img.getId(), model.isPrivate());
                communityFactory.create(community);
            } else {
                Community community = new Community(0, model.name(), model.description(), model.genre(), null, null, model.isPrivate());
                communityFactory.create(community);
            }
        } catch (SQLException | IOException e) {
            return new ValidationResult(new ValidationError("Internal error: please contact SoundLink Support."));
        }
        return new ValidationResult();
    }

    @Override
    public List<Community> searchCommunities(String searchText, boolean showOnlyPrivate) {
        try {
            List<Community> communities = communityFactory.getAllCommunities();

            // Filter based on name, description, genre, and privacy status
            return communities.stream()
                    .filter(community -> (searchText == null || searchText.isEmpty() ||
                            community.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            community.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                            community.getGenre().toLowerCase().contains(searchText.toLowerCase())))
                    .filter(community -> !showOnlyPrivate || community.isPrivate())
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching communities", e);
            return List.of();
        }
    }


	@Override
	public boolean signUpForCommunity(int userId, int communityId) throws SQLException {
		// Check if the user is already signed up for the community
		Optional<CommunityMember> existingMember = communityMemberFactory.get(communityId, userId);

		if (existingMember.isPresent()) {
			return false;
		} else {
			int permission = 6;  // can change this
			communityMemberFactory.create(communityId, userId, permission);
			return true;
		}
	}

    @Override
    public boolean cancelJoin(int userId, int communityId) throws SQLException {
        Optional<CommunityMember> existingMember = communityMemberFactory.get(communityId, userId);

        if (existingMember.isPresent()) {
            try {
                communityMemberFactory.delete(communityId, userId);
                return true;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error removing user from community", e);
                throw new SQLException("Error removing user from the community.", e);
            }
        } else {
            LOGGER.log(Level.WARNING, "User is not a member of this community: CommunityId = " + communityId + ", UserId = " + userId);
            return false;
        }
    }


	@Override
	public Optional<CommunityModel> getCommunity(int id) {
		try {
			var communityOpt = communityFactory.get(id);
			if (communityOpt.isPresent()) {
				return Optional.of(map.community(communityOpt.get()));
			}
			return Optional.empty();
		} catch (SQLException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean hasUserJoinedIntoCommunity(int userId, int communityId) throws SQLException {
		Optional<CommunityMember> existingMember = communityMemberFactory.get(communityId, userId);
		return existingMember.isPresent();
	}

    // TODO: Check if the update is similar to what is already in the db, if it is, don't update the db.
    @Override
    public void updateCommunity(CommunityModel community) throws SQLException {
        try {
            Integer bannerImageId = community.bannerImage().map(img -> img.getId()).orElse(null);

            Community updatedCommunity = new Community(
                    community.communityId(),
                    community.name(),
                    community.description(),
                    community.genre(),
                    community.created(),
                    bannerImageId,
					community.isPrivate()
            );

            communityFactory.save(updatedCommunity);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating community", e);
            throw e;
        }
    }

    @Override
    public Optional<Integer> getUserPermissionLevel(int userId, int communityId) throws SQLException {
        Optional<CommunityMember> communityMember = communityMemberFactory.get(communityId, userId);
        return communityMember.map(CommunityMember::getPermission).or(() -> Optional.of(1)); // 0 = read-only access
    }

    @Override
    public void deleteCommunity(int communityId, int userId) throws SQLException {
        if (!isAdmin(userId, communityId)) {
            throw new SecurityException("Only admins can delete communities.");
        }
        try {
            Optional<Community> communityOpt = communityFactory.get(communityId);

            if (communityOpt.isEmpty()) {
                throw new SQLException("Community with ID " + communityId + " not found.");
            }
            communityFactory.delete(communityOpt.get().getId());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting community", e);
            throw e;
        }
    }

    private boolean isAdmin(int userId, int communityId) throws SQLException {
        Optional<Integer> permissionLevel = getUserPermissionLevel(userId, communityId);
        return permissionLevel.map(level -> level == 1).orElse(false);  // Assume 1 is admin level
    }

	@Override
	public List<CommunityPostModel> getCommunityPosts(int communityId) throws SQLException {
		List<CommunityPost> posts = communityPostFactory.getPosts(communityId);
		List<CommunityPostModel> postModels = new ArrayList<>();
		for (CommunityPost post : posts) {
			postModels.add(map.communityPost(post));
		}
		return postModels;
	}
}
