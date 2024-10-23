package hub.troubleshooters.soundlink.data.models;

import java.util.Date;

/**
 * Represents a request for approval within a community.
 * This request can be approved or rejected by an admin.
 */
public class ApprovalRequest {
    private final int requestId;
    private final int communityId;
    private final int userId;
    private final Date requestDate;
    private boolean isApproved;

    /**
     * Constructs an ApprovalRequest object.
     *
     * @param requestId The unique ID of the request.
     * @param communityId The ID of the community related to the request.
     * @param userId The ID of the user making the request.
     * @param requestDate The date when the request was made.
     * @param isApproved The initial approval status of the request.
     */
    public ApprovalRequest(int requestId, int communityId, int userId, Date requestDate, boolean isApproved) {
        this.requestId = requestId;
        this.communityId = communityId;
        this.userId = userId;
        this.requestDate = requestDate;
        this.isApproved = isApproved;
    }

    /**
     * Gets the unique ID of the request.
     *
     * @return The request ID.
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Gets the ID of the community related to the request.
     *
     * @return The community ID.
     */
    public int getCommunityId() {
        return communityId;
    }

    /**
     * Gets the ID of the user who made the request.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the date when the request was made.
     *
     * @return The request date.
     */
    public Date getRequestDate() {
        return requestDate;
    }

    /**
     * Checks whether the request is approved.
     *
     * @return True if the request is approved, otherwise false.
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Sets the approval status of the request.
     *
     * @param approved True to approve the request, false to reject it.
     */
    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
