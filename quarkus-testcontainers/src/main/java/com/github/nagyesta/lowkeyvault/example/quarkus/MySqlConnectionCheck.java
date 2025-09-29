package com.github.nagyesta.lowkeyvault.example.quarkus;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class MySqlConnectionCheck {

    private final DataSource dataSource;

    @Inject
    public MySqlConnectionCheck(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void verifyConnectivity() throws SQLException {
        try (final var connection = dataSource.getConnection()) {
            query(connection);
        }
    }

    private void query(Connection connection) throws SQLException {
        final var sql = "SELECT CONCAT(@@version_comment, ' - ', VERSION()) FROM DUAL";
        try (final var resultSet = connection.prepareStatement(sql).executeQuery()) {
            resultSet.next();
            final var value = resultSet.getString(1);
            //write something that will be visible on the Gradle output
            System.err.println(value);
        }
    }
}
