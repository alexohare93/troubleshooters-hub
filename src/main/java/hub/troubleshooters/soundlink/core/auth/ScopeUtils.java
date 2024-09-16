package hub.troubleshooters.soundlink.core.auth;

import java.util.EnumSet;
import java.util.Set;

public class ScopeUtils {
  /**
   * Combines multiple scopes into a single int. This is useful for storing multiple distinct scopes
   * in a single field for storage.
   *
   * @param scopes The scopes to combine.
   * @return The combined scopes created by bitwise ORing the bitmask of each scope.
   */
  public static int combineScopes(Scope... scopes) {
    var combined = 0;
    for (Scope scope : scopes) {
      combined |= scope.getBitmask(); // bitwise OR to combine the bits into a single int
    }
    return combined;
  }

  /**
   * Checks if a scope is present in a combined scope.
   *
   * @param combinedScopes The combined scopes to check.
   * @param scope The scope to check for.
   * @return True if the scope is present, false otherwise.
   */
  public static boolean hasScope(int combinedScopes, Scope scope) {
    return (combinedScopes & scope.getBitmask()) != 0; // bitwise AND to check if the bit is set
  }

  /**
   * Adds a scope to a combined scope.
   *
   * @param combinedScopes The combined scopes to add to.
   * @param scope The scope to add.
   * @return The combined scopes with the new scope added.
   */
  public static int removeScope(int combinedScopes, Scope scope) {
    return combinedScopes & ~scope.getBitmask(); // bitwise AND + NOT to clear the bit
  }

  /**
   * Deconstructs a combined scope into a set of individual scopes.
   *
   * @param combinedScopes The combined scopes to deconstruct.
   * @return A set of individual scopes.
   */
  public static Set<Scope> deconstructScopes(int combinedScopes) {
    var scopes = EnumSet.noneOf(Scope.class); // Creates an empty set of Scopes

    for (var scope : Scope.values()) {
      if (hasScope(combinedScopes, scope)) {
        scopes.add(scope);
      }
    }

    return scopes;
  }
}
