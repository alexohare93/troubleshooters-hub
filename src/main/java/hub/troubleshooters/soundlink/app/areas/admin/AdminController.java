package hub.troubleshooters.soundlink.app.areas.admin;

import com.google.inject.Inject;
import hub.troubleshooters.soundlink.core.admin.services.ApprovalService;
import hub.troubleshooters.soundlink.data.models.ApprovalRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Controller class responsible for handling the admin interface, including managing approval requests.
 */
public class AdminController {

    private final ApprovalService approvalService;
    private int currentCommunityId;

    @FXML
    private ListView<ApprovalRequest> approvalRequestsList;

    @FXML
    private Button approveButton;

    @FXML
    private Button rejectButton;

    @FXML
    private Label statusLabel;

    /**
     * Constructs the AdminController with the necessary {@link ApprovalService}.
     *
     * @param approvalService The service used to handle approval requests.
     */
    @Inject
    public AdminController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * Initialize the controller with a specific community ID and load pending requests for that community.
     *
     * @param communityId The ID of the community for which to load pending approval requests.
     */
    @FXML
    public void initializeWithCommunityId(int communityId) {
        this.currentCommunityId = communityId;
        loadPendingRequests(communityId);
    }

    /**
     * Load the pending requests for the current community and update the ListView.
     *
     * @param communityId The ID of the community for which to load pending requests.
     */
    private void loadPendingRequests(int communityId) {
        var pendingRequests = approvalService.getPendingRequestsForCommunity(communityId);
        approvalRequestsList.getItems().setAll(pendingRequests);
    }

    /**
     * Approve the selected request when the approve button is clicked.
     */
    @FXML
    public void onApproveButtonClick() {
        ApprovalRequest selectedRequest = approvalRequestsList.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            approvalService.approveRequest(selectedRequest.getRequestId());
            statusLabel.setText("Request Approved");
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setVisible(true);
            loadPendingRequests(currentCommunityId);
        }
    }

    /**
     * Reject the selected request when the reject button is clicked.
     */
    @FXML
    public void onRejectButtonClick() {
        ApprovalRequest selectedRequest = approvalRequestsList.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            approvalService.rejectRequest(selectedRequest.getRequestId());
            statusLabel.setText("Request Rejected");
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setVisible(true);
            loadPendingRequests(currentCommunityId);
        }
    }
}
