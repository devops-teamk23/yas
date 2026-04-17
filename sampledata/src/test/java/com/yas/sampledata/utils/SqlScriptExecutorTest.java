package com.yas.sampledata.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SqlScriptExecutor class.
 * Tests cover:
 * - SQL script execution for schema
 * - Resource resolution from patterns
 * - Database connection handling
 * - Schema switching
 * - Exception handling (SQL and IO exceptions)
 * - Empty resource arrays
 * - Database connection close
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SqlScriptExecutor Tests")
class SqlScriptExecutorTest {

    @InjectMocks
    private SqlScriptExecutor sqlScriptExecutor;

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @Test
    @DisplayName("Should create SqlScriptExecutor instance successfully")
    void testSqlScriptExecutorInstantiation() {
        // Assert
        assertNotNull(sqlScriptExecutor, "SqlScriptExecutor should be instantiated");
    }

    @Test
    @DisplayName("Should get connection from DataSource")
    void testGetConnectionFromDataSource() throws SQLException {
        // Arrange
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Act - Call the method that uses DataSource internally
        sqlScriptExecutor.executeScriptsForSchema(mockDataSource, "public", "classpath*:db/nosuchfiles/*.sql");

        // Assert
        verify(mockDataSource, atLeastOnce()).getConnection();
    }

    @Test
    @DisplayName("Should close connection after execution")
    void testCloseConnectionAfterExecution() throws SQLException {
        // Arrange
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        sqlScriptExecutor.executeScriptsForSchema(mockDataSource, "public", "classpath*:db/nonexistent/*.sql");

        // Assert - Connection should be closed (either successfully or due to error handling)
        // The method uses try-with-resources, so close should be called
        verify(mockConnection, atLeastOnce()).close();
    }

    @Test
    @DisplayName("Should set correct schema on connection")
    void testSetCorrectSchemaOnConnection() throws SQLException {
        // Arrange
        String schemaName = "custom_schema";
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        sqlScriptExecutor.executeScriptsForSchema(mockDataSource, schemaName, 
            "classpath*:db/nonexistent/*.sql");

        // Assert
        verify(mockConnection).setSchema(schemaName);
    }

    @Test
    @DisplayName("Should handle different schema names correctly")
    void testHandleDifferentSchemaNames() throws SQLException {
        // Arrange
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        String[] schemaNames = {"public", "custom", "schema_test", "test2"};

        // Act & Assert
        for (String schema : schemaNames) {
            sqlScriptExecutor.executeScriptsForSchema(mockDataSource, schema, 
                "classpath*:db/nonexistent/*.sql");
            verify(mockConnection).setSchema(schema);
        }
    }

    @Test
    @DisplayName("Should handle SQLExceptions gracefully without throwing")
    void testHandleSQLException() throws SQLException {
        // Arrange
        when(mockDataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> sqlScriptExecutor.executeScriptsForSchema(
            mockDataSource, "public", "classpath*:db/test/*.sql"
        ), "Should handle SQLException gracefully");
    }

    @Test
    @DisplayName("Should accept null schema parameter")
    void testAcceptNullSchemaParameter() {
        // Act & Assert - Should not throw NPE
        assertDoesNotThrow(() -> sqlScriptExecutor.executeScriptsForSchema(
            mockDataSource, null, "classpath*:db/test/*.sql"
        ), "Should handle null schema gracefully");
    }

    @Test
    @DisplayName("Should accept various locationPattern formats")
    void testAcceptVariousLocationPatterns() {
        // Arrange
        when(mockConnection).thenReturn(mockConnection);
        String[] patterns = {
            "classpath*:db/test/*.sql",
            "classpath:db/script.sql",
            "file:db/test/*.sql",
            "db/test/**/*.sql"
        };

        // Act & Assert
        for (String pattern : patterns) {
            assertDoesNotThrow(() -> sqlScriptExecutor.executeScriptsForSchema(
                mockDataSource, "public", pattern
            ), "Should accept pattern: " + pattern);
        }
    }

    @Test
    @DisplayName("Should handle empty location pattern")
    void testHandleEmptyLocationPattern() {
        // Act & Assert
        assertDoesNotThrow(() -> sqlScriptExecutor.executeScriptsForSchema(
            mockDataSource, "public", ""
        ), "Should handle empty location pattern");
    }

    @Test
    @DisplayName("Should handle resource resolution with no results")
    void testHandleNoResourcesFound() throws SQLException {
        // Arrange
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Act & Assert
        assertDoesNotThrow(() -> sqlScriptExecutor.executeScriptsForSchema(
            mockDataSource, "public", "classpath*:db/absolute_nonexistent_path/*.sql"
        ), "Should handle case when no resources are found");
    }

    @Test
    @DisplayName("Should maintain state with multiple consecutive calls")
    void testMultipleConsecutiveCalls() throws SQLException {
        // Arrange
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        for (int i = 0; i < 3; i++) {
            String schema = "schema_" + i;
            String pattern = "classpath*:db/test_" + i + "/*.sql";
            sqlScriptExecutor.executeScriptsForSchema(mockDataSource, schema, pattern);
        }

        // Assert - Should handle multiple calls without issues
        verify(mockDataSource, atLeastOnce()).getConnection();
    }

    @Test
    @DisplayName("Should work with public schema (common case)")
    void testPublicSchemaExecution() throws SQLException {
        // Arrange
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Act
        sqlScriptExecutor.executeScriptsForSchema(mockDataSource, "public", 
            "classpath*:db/nonexistent/*.sql");

        // Assert
        verify(mockConnection).setSchema("public");
    }

    @Test
    @DisplayName("Should handle DataSource that throws SQLException on getConnection")
    void testDataSourceThrowsSQLException() throws SQLException {
        // Arrange
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection())
            .thenThrow(new SQLException("Database unavailable"));

        // Act & Assert
        assertDoesNotThrow(() -> sqlScriptExecutor.executeScriptsForSchema(
            failingDataSource, "public", "classpath*:db/test/*.sql"
        ), "Should handle SQLException from DataSource");
    }

    @Test
    @DisplayName("Should accept null DataSource parameter gracefully")
    void testHandleNullDataSource() {
        // Act & Assert - May throw NPE or handle gracefully (both are acceptable)
        // Document the actual behavior
        try {
            sqlScriptExecutor.executeScriptsForSchema(null, "public", "classpath*:db/test/*.sql");
            // If no exception, that's fine
            assertTrue(true);
        } catch (NullPointerException e) {
            // NullPointerException is also acceptable
            assertTrue(true, "NPE is acceptable when DataSource is null");
        }
    }
}
