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
				Community community = new Community(0, model.name(), model.description(), model.genre(), null, img.getId());
				communityFactory.create(community);
			} else {
				Community community = new Community(0, model.name(), model.description(), model.genre(), null, null);
				communityFactory.create(community);
			}
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

	@Override
	public boolean cancelJoin(int userId, int communityId) throws SQLException {
		// Check if the user is currently signed up for the community
		Optional<CommunityMember> existingMember = communityMemberFactory.get(userId, communityId);

		if (existingMember.isPresent()) {
			communityMemberFactory.delete(userId, communityId); // Assumes a delete method exists to remove the user booking
			return true;
		} else {
			return false; // User was not signed up for this community, so cannot cancel
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
		Optional<CommunityMember> existingMember = communityMemberFactory.get(userId, communityId);
		return existingMember.isPresent();
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
