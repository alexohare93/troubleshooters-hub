package hub.troubleshooters.soundlink.core.auth;

/** Represents an exception that occurs during authentication or authorization. */
public class AuthException extends Exception {
  public AuthException(String message) {
    super(message);
  }
}
