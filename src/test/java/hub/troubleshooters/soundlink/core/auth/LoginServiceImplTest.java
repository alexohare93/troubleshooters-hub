package hub.troubleshooters.soundlink.core.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.favre.lib.crypto.bcrypt.BCrypt;
import hub.troubleshooters.soundlink.data.factories.CommunityMemberFactory;
import hub.troubleshooters.soundlink.data.factories.UserFactory;
import hub.troubleshooters.soundlink.data.models.User;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LoginServiceImplTest {
  private final String PASSWORD = "testPass"; // the plaintext password for our test user
  private User user;

  // setting up our mocked dependencies necessary for the LoginServiceImpl
  @Mock private UserFactory userFactory;
  @Mock private CommunityMemberFactory communityMemberFactory;
  @Mock private IdentityService identityService;

  // providing our mocked dependencies to our service we are actually testing
  @InjectMocks private LoginServiceImpl loginServiceImpl;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // creating the user for our tests
    var username = "testUser";
    var hashedPassword = hashPassword(PASSWORD);

    // setting up a mock user
    user = new User(0);
    user.setUsername(username);
    user.setHashedPassword(hashedPassword);
  }

  @Test
  void testLogin_Successful() throws SQLException {
    // whenever our mocked userFactory.get method is called, it will always return
    // Optional.of(user).
    when(userFactory.get(user.getUsername())).thenReturn(Optional.of(user));

    var result = loginServiceImpl.login(user.getUsername(), PASSWORD); // performing the actual test

    assertTrue(result.isSuccess());
    assertNull(result.getError());

    verify(communityMemberFactory).get(user);
    verify(userFactory).save(user); // ensure the save method was called during our test
  }

  @Test
  void testLogin_IncorrectUsername() throws SQLException {
    // whenever our mocked userFactory.get method is called, it will always return Optional.empty to
    // simulate an invalid username
    when(userFactory.get(user.getUsername())).thenReturn(Optional.empty());

    var result = loginServiceImpl.login(user.getUsername(), PASSWORD); // performing the actual test

    assertFalse(result.isSuccess());
    assertEquals(result.getError().getMessage(), "Incorrect username or password");
  }

  private String hashPassword(String password) {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray());
  }
}
