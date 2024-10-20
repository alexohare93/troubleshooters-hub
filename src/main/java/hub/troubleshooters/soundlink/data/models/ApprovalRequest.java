package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

public class ApprovalRequest {
    private final int requestId;
    private final int communityId;
    private final int userId;
    private final Date requestDate;
    private boolean isApproved;

    public ApprovalRequest(int requestId, int communityId, int userId, Date requestDate, boolean isApproved) {
        this.requestId = requestId;
        this.communityId = communityId;
        this.userId = userId;
        this.requestDate = requestDate;
        this.isApproved = isApproved;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public int getUserId() {
        return userId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
