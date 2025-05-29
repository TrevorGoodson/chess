package dataaccess;

import model.AuthData;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class DataAccessSQL {
    protected final int NO_GENERATED_KEY = 0;

    public DataAccessSQL() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : getCreateStatements()) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    protected abstract String[] getCreateStatements();

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)
        ) {
            for (var i = 1; i <= params.length; i++) {
                var param = params[i - 1];
                switch (param) {
                    case String p -> ps.setString(i, p);
                    case Integer p -> ps.setInt(i, p);
                    case null -> ps.setNull(i, NULL);
                    default -> {
                        throw new DataAccessException("Unsupported data type used in SQL statement.");
                    }
                }
            }

            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

            return NO_GENERATED_KEY;
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
