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

/**
 * Implementation of the {@link CommunityService} interface responsible for managing community-related operations.
 * This service handles creating, updating, and deleting communities, managing community memberships, and
 * retrieving community posts.
 */
public class CommunityServiceImpl implements CommunityService {

	private static final Logger LOGGER = Logger.getLogger(CommunityServiceImpl.class.getName());

	private final CommunityFactory communityFactory;

	private final CommunityPostFactory communityPostFactory;

	private final CommunityMemberFactory communityMemberFactory;

	private final UserFactory userFactory;

	private final CreateCommunityModelValidator createCommunityModelValidator;

	private final ImageUploaderService imageUploaderService;

	private final Map map;

    /**
     * Constructs a new {@code CommunityServiceImpl} with the necessary dependencies.
     *
     * @param communityFactory The factory responsible for managing community-related data.
     * @param communityPostFactory The factory responsible for managing community posts.
     * @param communityMemberFactory The factory responsible for managing community memberships.
     * @param userFactory The factory responsible for managing users.
     * @param createCommunityModelValidator The validator for community creation models.
     * @param imageUploaderService The service for uploading images.
     * @param map The mapper for converting between models.
     */
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

    /**
     * Creates a new community based on the provided model.
     *
     * @param model The {@link CreateCommunityModel} containing details for the new community.
     * @return A {@link ValidationResult} indicating success or failure of the operation.
     */
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
                signUpForCommunity(model.id(), community.getId());
            } else {
                Community community = new Community(0, model.name(), model.description(), model.genre(), null, null, model.isPrivate());
                communityFactory.create(community);
                signUpForCommunity(model.id(), community.getId());
            }
        } catch (SQLException | IOException e) {
            return new ValidationResult(new ValidationError("Internal error: please contact SoundLink Support."));
        }
        return new ValidationResult();
    }

    /**
     * Searches for communities based on the search text and privacy filter.
     *
     * @param searchText The search text to filter communities by.
     * @param showOnlyPrivate If {@code true}, only private communities will be shown.
     * @return A list of {@link Community} objects matching the search criteria.
     */
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

    /**
     * Signs a user up for a community.
     *
     * @param userId The ID of the user to sign up.
     * @param communityId The ID of the community to sign up for.
     * @return {@code true} if the sign-up is successful, {@code false} if the user is already signed up.
     * @throws SQLException If there is an error during the sign-up process.
     */
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

    /**
     * Cancels a user's membership in a community.
     *
     * @param userId The ID of the user to cancel membership for.
     * @param communityId The ID of the community to leave.
     * @return {@code true} if the cancellation is successful, {@code false} if the user is not a member.
     * @throws SQLException If there is an error during the cancellation process.
     */
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

    /**
     * Retrieves a community by its ID.
     *
     * @param id The ID of the community to retrieve.
     * @return An {@link Optional} containing the {@link CommunityModel} if found, or {@code Optional.empty()} if not found.
     */
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

    /**
     * Checks if a user is a member of a specific community.
     *
     * @param userId The ID of the user.
     * @param communityId The ID of the community.
     * @return {@code true} if the user is a member of the community, {@code false} otherwise.
     * @throws SQLException If there is an error during the query process.
     */
	@Override
	public boolean hasUserJoinedIntoCommunity(int userId, int communityId) throws SQLException {
		Optional<CommunityMember> existingMember = communityMemberFactory.get(communityId, userId);
		return existingMember.isPresent();
	}

    /**
     * Updates the details of an existing community.
     *
     * @param community The {@link CommunityModel} containing the updated community details.
     * @throws SQLException If there is an error during the update process.
     */
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

    /**
     * Retrieves the permission level of a user in a community.
     *
     * @param userId The ID of the user.
     * @param communityId The ID of the community.
     * @return An {@link Optional} containing the permission level, or {@code Optional.of(0)} for read-only access.
     * @throws SQLException If there is an error during the query process.
     */
    @Override
    public Optional<Integer> getUserPermissionLevel(int userId, int communityId) throws SQLException {
        Optional<CommunityMember> communityMember = communityMemberFactory.get(communityId, userId);
        return communityMember.map(CommunityMember::getPermission).or(() -> Optional.of(0)); // 0 = read-only access
    }

    /**
     * Deletes a community, if the requesting user has admin privileges.
     *
     * @param communityId The ID of the community to delete.
     * @param userId The ID of the user requesting the deletion.
     * @throws SQLException If there is an error during the deletion process.
     * @throws SecurityException If the user does not have admin privileges.
     */
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

    /**
     * Checks if a user is an admin of a community.
     *
     * @param userId The ID of the user.
     * @param communityId The ID of the community.
     * @return {@code true} if the user is an admin, {@code false} otherwise.
     * @throws SQLException If there is an error during the query process.
     */
    private boolean isAdmin(int userId, int communityId) throws SQLException {
        Optional<Integer> permissionLevel = getUserPermissionLevel(userId, communityId);
        return permissionLevel.map(level -> level == 1).orElse(false);  // Assume 1 is admin level
    }

    /**
     * Retrieves a list of posts for a specific community.
     *
     * @param communityId The ID of the community.
     * @return A list of {@link CommunityPostModel} objects representing the posts in the community.
     * @throws SQLException If there is an error during the query process.
     */
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
