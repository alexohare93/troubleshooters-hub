package hub.troubleshooters.soundlink.core.admin.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.models.ApprovalRequest;
import hub.troubleshooters.soundlink.app.areas.notification.NotificationController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ApprovalService} that handles the approval process for community join requests.
 * This class manages a list of approval requests and interacts with the {@link NotificationController}
 * to update notification counts based on actions.
 */
public class ApprovalServiceImpl implements ApprovalService {

    private final List<ApprovalRequest> approvalRequests = new ArrayList<>();
    private final NotificationController notificationController;

    /**
     * Constructs an {@code ApprovalServiceImpl} with a {@link NotificationController} to manage notifications.
     *
     * @param notificationController The controller responsible for handling notifications.
     */
    @Inject
    public ApprovalServiceImpl(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    /**
     * Submits a join request for a community. Adds the request to the list of pending approval requests
     * and increments the notification count.
     *
     * @param communityId The ID of the community to join.
     * @param userId The ID of the user submitting the join request.
     */
    @Override
    public void submitJoinRequest(int communityId, int userId) {
        ApprovalRequest request = new ApprovalRequest(approvalRequests.size() + 1, communityId, userId, new Date(), false);
        approvalRequests.add(request);
        notificationController.incrementNotifications();
    }

    /**
     * Retrieves a list of pending approval requests for a specific community.
     * Filters requests that are not yet approved.
     *
     * @param communityId The ID of the community.
     * @return A list of {@link ApprovalRequest} objects that are pending approval for the community.
     */
    @Override
    public List<ApprovalRequest> getPendingRequestsForCommunity(int communityId) {
        return approvalRequests.stream()
                .filter(request -> request.getCommunityId() == communityId && !request.isApproved())
                .collect(Collectors.toList());
    }

    /**
     * Approves a pending join request by its request ID and clears notifications.
     *
     * @param requestId The ID of the request to approve.
     */
    @Override
    public void approveRequest(int requestId) {
        ApprovalRequest request = findRequestById(requestId);
        if (request != null) {
            request.setApproved(true);
            notificationController.clearNotifications();
        }
    }

    /**
     * Rejects a pending join request by its request ID and removes the request from the list.
     * Also clears notifications.
     *
     * @param requestId The ID of the request to reject.
     */
    @Override
    public void rejectRequest(int requestId) {
        ApprovalRequest request = findRequestById(requestId);
        if (request != null) {
            approvalRequests.remove(request);
            notificationController.clearNotifications();
        }
    }

    /**
     * Finds an approval request by its request ID.
     *
     * @param requestId The ID of the request to find.
     * @return The {@link ApprovalRequest} if found, otherwise {@code null}.
     */
    private ApprovalRequest findRequestById(int requestId) {
        return approvalRequests.stream()
                .filter(req -> req.getRequestId() == requestId)
                .findFirst()
                .orElse(null);
    }
}