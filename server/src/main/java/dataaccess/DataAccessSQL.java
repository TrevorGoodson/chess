package dataaccess;

import model.AuthData;
import model.GameData;

import java.sql.*;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class DataAccessSQL {
    protected final static int NO_GENERATED_KEY = 0;

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

    protected int executeUpdate(String statement, Object... parameters) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)
        ) {
            for (int i = 1; i <= parameters.length; i++) {
                var parameter = parameters[i - 1];
                if (parameter == null) {
                    ps.setNull(i, NULL);
                } else {
                    ps.setObject(i, parameter);
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

    protected List<Map<String, Object>> executeSelect(String tableName, String columnName, Object condition) throws DataAccessException {
        String sqlStatement = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
        ArrayList<Map<String, Object>> returnTable = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlStatement)) {

            if (condition == null) {
                ps.setNull(1, NULL);
            } else {
                ps.setObject(1, condition);
            }

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int numColumns = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= numColumns; ++i) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }
                    returnTable.add(row);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data:" + e.getMessage(), e);
        }
        return returnTable;
    }
}
