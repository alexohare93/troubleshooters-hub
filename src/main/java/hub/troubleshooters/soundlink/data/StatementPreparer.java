package hub.troubleshooters.soundlink.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementPreparer {
    void prepare(PreparedStatement statement) throws SQLException;
}