package com.github.nagyesta.lowkeyvault.example.springcloudazurestarter;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MySqlConnectionCheck {

    private final DataSource dataSource;

    public MySqlConnectionCheck(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void verifyConnectivity() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            query(connection);
        }
    }

    private void query(final Connection connection) throws SQLException {
        final var sql = "SELECT CONCAT(@@version_comment, ' - ', VERSION()) FROM DUAL";
        try (var resultSet = connection.prepareStatement(sql).executeQuery()) {
            resultSet.next();
            final var value = resultSet.getString(1);
            //write something that will be visible on the Gradle output
            System.err.println(value);
        }
    }
}
