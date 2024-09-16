package hub.troubleshooters.soundlink.data;

import java.sql.SQLException;

@FunctionalInterface
public interface UpdateExecutor {
  void execute(int affectedRows) throws SQLException;
}
