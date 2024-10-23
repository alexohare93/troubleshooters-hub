package hub.troubleshooters.soundlink.core.admin.services;

import hub.troubleshooters.soundlink.data.models.ApprovalRequest;

import java.util.List;

/**
 * Service interface responsible for handling community join requests and approval processes.
 */
public interface ApprovalService {

    /**
     * Submit a join request for a community.
     * @param communityId The ID of the community to join.
     * @param userId The ID of the user requesting to join.
     */
    void submitJoinRequest(int communityId, int userId);

    /**
     * Get a list of pending approval requests for a specific community.
     * @param communityId The ID of the community.
     * @return List of ApprovalRequests that are not yet approved.
     */
    List<ApprovalRequest> getPendingRequestsForCommunity(int communityId);

    /**
     * Approve a pending join request.
     * @param requestId The ID of the request to approve.
     */
    void approveRequest(int requestId);

    /**
     * Reject a pending join request.
     * @param requestId The ID of the request to reject.
     */
    void rejectRequest(int requestId);
}
