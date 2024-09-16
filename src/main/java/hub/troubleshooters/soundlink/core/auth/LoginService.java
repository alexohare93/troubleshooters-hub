package hub.troubleshooters.soundlink.core.auth;

public interface LoginService {
  /**
   * Authenticates the user provided they enter the correct username and password.
   *
   * @param username the username specified
   * @param password the plaintext password specified.
   * @return An AuthResult which may contain an error if authentication failed. Make sure to check
   *     .isSuccess() on this result.
   */
  AuthResult login(String username, String password);

  /**
   * Registers a new user with the given username and password.
   *
   * @param username the username to register
   * @param password the plaintext password to register
   * @return An AuthResult which may contain an error if registration failed. Make sure to check
   *     .isSuccess() on this result.
   */
  AuthResult register(String username, String password);

  /** Logs out the current user. */
  void logout();
}
