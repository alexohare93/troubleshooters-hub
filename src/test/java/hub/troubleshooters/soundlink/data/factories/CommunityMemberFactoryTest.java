package hub.troubleshooters.soundlink.data.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import hub.troubleshooters.soundlink.data.DatabaseConnection;
import hub.troubleshooters.soundlink.data.StatementPreparer;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import hub.troubleshooters.soundlink.data.models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CommunityMemberFactoryTest {

  @Mock private DatabaseConnection mockConnection;

  @Mock private PreparedStatement mockPreparedStatement;

  @Mock private ResultSet mockResultSet;

  @InjectMocks private CommunityMemberFactory communityMemberFactory;

  private CommunityMember testCommunityMember;
  private User testUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testCommunityMember = new CommunityMember(1, 2, 3, new java.util.Date(), 1);
    testUser =
        new User(3, "testuser", "hashedpassword", new java.util.Date(), new java.util.Date());
  }

  @Test
  void testSave() throws SQLException {
    // Arrange
    doNothing().when(mockConnection).executeUpdate(anyString(), any(), any());

    // Act
    communityMemberFactory.save(testCommunityMember);

    // Assert
    ArgumentCaptor<StatementPreparer> captor = ArgumentCaptor.forClass(StatementPreparer.class);
    verify(mockConnection)
        .executeUpdate(
            eq("UPDATE CommunityMembers SET Permission = ? WHERE Id = ?;"),
            captor.capture(),
            any());

    // Verify the correct values were set in the prepared statement
    StatementPreparer preparer = captor.getValue();
    PreparedStatement mockStatement = mock(PreparedStatement.class);
    preparer.prepare(mockStatement);
    verify(mockStatement).setInt(1, testCommunityMember.getPermission());
    verify(mockStatement).setInt(2, testCommunityMember.getId());
  }

  @Test
  void testGetById_CommunityMemberFound() throws SQLException {
    // Arrange
    String sql = "SELECT * FROM CommunityMembers WHERE Id = ?;";
    when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(testCommunityMember);

    // Act
    Optional<CommunityMember> result = communityMemberFactory.get(testCommunityMember.getId());

    // Assert
    assertTrue(result.isPresent());
    assertEquals(testCommunityMember, result.get());
  }

  @Test
  void testGetById_CommunityMemberNotFound() throws SQLException {
    // Arrange
    String sql = "SELECT * FROM CommunityMembers WHERE Id = ?;";
    when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(null);

    // Act
    Optional<CommunityMember> result = communityMemberFactory.get(999);

    // Assert
    assertFalse(result.isPresent());
  }

  @Test
  void testGetByUser_CommunityMembersFound() throws SQLException {
    // Arrange
    String sql = "SELECT * FROM CommunityMembers WHERE UserId = ?;";
    List<CommunityMember> communityMembers = new ArrayList<>();
    communityMembers.add(testCommunityMember);
    when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(communityMembers);

    // Act
    List<CommunityMember> result = communityMemberFactory.get(testUser);

    // Assert
    assertFalse(result.isEmpty());
    assertEquals(communityMembers.size(), result.size());
    assertEquals(communityMembers.get(0), result.get(0));
  }

  @Test
  void testGetByUser_NoCommunityMembersFound() throws SQLException {
    // Arrange
    String sql = "SELECT * FROM CommunityMembers WHERE UserId = ?;";
    when(mockConnection.executeQuery(eq(sql), any(), any())).thenReturn(new ArrayList<>());

    // Act
    List<CommunityMember> result = communityMemberFactory.get(testUser);

    // Assert
    assertTrue(result.isEmpty());
  }
}
