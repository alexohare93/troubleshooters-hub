package hub.troubleshooters.soundlink.core.admin.services;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.data.models.ApprovalRequest;
import hub.troubleshooters.soundlink.app.areas.notification.NotificationController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ApprovalServiceImpl implements ApprovalService {

    private final List<ApprovalRequest> approvalRequests = new ArrayList<>();
    private final NotificationController notificationController;

    @Inject
    public ApprovalServiceImpl(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public void submitJoinRequest(int communityId, int userId) {
        ApprovalRequest request = new ApprovalRequest(approvalRequests.size() + 1, communityId, userId, new Date(), false);
        approvalRequests.add(request);
        notificationController.incrementNotifications();
    }

    public List<ApprovalRequest> getPendingRequestsForCommunity(int communityId) {
        return approvalRequests.stream()
                .filter(request -> request.getCommunityId() == communityId && !request.isApproved())
                .collect(Collectors.toList());
    }

    public void approveRequest(int requestId) {
        ApprovalRequest request = findRequestById(requestId);
        if (request != null) {
            request.setApproved(true);
            notificationController.clearNotifications();
        }
    }

    public void rejectRequest(int requestId) {
        ApprovalRequest request = findRequestById(requestId);
        if (request != null) {
            approvalRequests.remove(request);
            notificationController.clearNotifications();
        }
    }

    private ApprovalRequest findRequestById(int requestId) {
        return approvalRequests.stream()
                .filter(req -> req.getRequestId() == requestId)
                .findFirst()
                .orElse(null);
    }
}

