package hub.troubleshooters.soundlink.core.auth;

public interface LoginService {
    AuthResult login(String username, String password);
    boolean isLoggedIn();
    void logout();
}
