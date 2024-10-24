package hub.troubleshooters.soundlink.data;

import java.sql.SQLException;

/**
 * A functional interface used to handle the result of an SQL update operation, such as an `INSERT`, `UPDATE`, or `DELETE`.
 *
 * <p>This interface defines a single method, {@code execute}, which is invoked with the number of rows affected
 * by the update operation. Implementations of this interface can handle the outcome of the update, such as
 * verifying the affected rows or performing post-update logic.</p>
 *
 * <p>This interface is typically used in conjunction with the {@link SQLiteDatabaseConnection#executeUpdate} method,
 * where the result of an update operation needs to be processed.</p>
 *
 * <pre>
 * Example usage:
 * {@code
 * String sql = "UPDATE Users SET password = ? WHERE username = ?";
 * UpdateExecutor executor = affectedRows -> {
 *     if (affectedRows == 1) {
 *         System.out.println("Password updated successfully.");
 *     } else {
 *         throw new SQLException("Failed to update password.");
 *     }
 * };
 * }
 * </pre>
 *
 * @see SQLiteDatabaseConnection#executeUpdate
 */
@FunctionalInterface
public interface UpdateExecutor {
    /**
     * Executes the logic for handling the result of an SQL update operation.
     *
     * @param affectedRows The number of rows affected by the update operation.
     * @throws SQLException If an error occurs while processing the result of the update.
     */
    void execute(int affectedRows) throws SQLException;
}