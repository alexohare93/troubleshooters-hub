package hub.troubleshooters.soundlink.core.communities.services;

import hub.troubleshooters.soundlink.core.communities.models.CommunityPostModel;
import hub.troubleshooters.soundlink.core.communities.models.CreateCommunityModel;
import hub.troubleshooters.soundlink.core.communities.models.CommunityModel;
import hub.troubleshooters.soundlink.core.validation.ValidationResult;
import hub.troubleshooters.soundlink.data.models.Community;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing community-related operations, such as creating communities,
 * searching for communities, managing community memberships, and handling community posts.
 */
public interface CommunityService {

    /**
     * Creates a new community based on the provided model.
     *
     * @param model The model containing the details of the community to create.
     * @return The result of the validation process for creating the community.
     */
    ValidationResult createCommunity(CreateCommunityModel model);

    /**
     * Searches for communities based on the search text and privacy settings.
     *
     * @param searchText The text to search for within community names and descriptions.
     * @param showOnlyPrivate Whether to filter results to only show private communities.
     * @return A list of communities matching the search criteria.
     */
    List<Community> searchCommunities(String searchText, boolean showOnlyPrivate);

    /**
     * Signs a user up for a community.
     *
     * @param userId The ID of the user signing up for the community.
     * @param communityId The ID of the community to sign up for.
     * @return {@code true} if the sign-up was successful, {@code false} otherwise.
     * @throws SQLException If there is an error during the sign-up process.
     */
    boolean signUpForCommunity(int userId, int communityId) throws SQLException;

    /**
     * Retrieves details of a specific community by its ID.
     *
     * @param id The ID of the community to retrieve.
     * @return An {@link Optional} containing the community details, or {@code Optional.empty()} if not found.
     */
    Optional<CommunityModel> getCommunity(int id);

    /**
     * Checks if a user has already joined a specific community.
     *
     * @param userId The ID of the user.
     * @param communityId The ID of the community.
     * @return {@code true} if the user has joined the community, {@code false} otherwise.
     * @throws SQLException If there is an error during the check.
     */
    boolean hasUserJoinedIntoCommunity(int userId, int communityId) throws SQLException;

    /**
     * Cancels a user's request to join a community.
     *
     * @param userId The ID of the user.
     * @param communityId The ID of the community to cancel the join request for.
     * @return {@code true} if the cancellation was successful, {@code false} otherwise.
     * @throws SQLException If there is an error during the cancellation.
     */
    boolean cancelJoin(int userId, int communityId) throws SQLException;

    /**
     * Updates the details of a community.
     *
     * @param community The model containing the updated community details.
     * @throws SQLException If there is an error during the update.
     */
    void updateCommunity(CommunityModel community) throws SQLException;

    /**
     * Retrieves the permission level of a user within a specific community.
     *
     * @param userId The ID of the user.
     * @param communityId The ID of the community.
     * @return An {@link Optional} containing the user's permission level, or {@code Optional.empty()} if not found.
     * @throws SQLException If there is an error during the retrieval.
     */
    Optional<Integer> getUserPermissionLevel(int userId, int communityId) throws SQLException;

    /**
     * Deletes a community.
     *
     * @param communityId The ID of the community to delete.
     * @param userId The ID of the user requesting the deletion.
     * @throws SQLException If there is an error during the deletion process.
     */
    void deleteCommunity(int communityId, int userId) throws SQLException;

    /**
     * Retrieves a list of posts for a specific community.
     *
     * @param communityId The ID of the community to retrieve posts from.
     * @return A list of {@link CommunityPostModel} objects representing the posts in the community.
     * @throws SQLException If there is an error during the retrieval process.
     */
    List<CommunityPostModel> getCommunityPosts(int communityId) throws SQLException;
}
