package com.yas.sampledata.utils;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SqlScriptExecutor class.
 * Tests validate that methods can be invoked and handle errors gracefully.
 */
@DisplayName("SqlScriptExecutor Tests")
class SqlScriptExecutorTest {

    private SqlScriptExecutor sqlScriptExecutor = new SqlScriptExecutor();

    @Test
    @DisplayName("Should instantiate SqlScriptExecutor")
    void testInstantiation() {
        assertNotNull(sqlScriptExecutor, "SqlScriptExecutor should be created");
    }

    @Test
    @DisplayName("Should handle executeScriptsForSchema with null DataSource gracefully")
    void testHandleNullDataSource() {
        // Act & Assert - Method should not throw NPE for null datasource
        // (it catches exceptions internally)
        assertDoesNotThrow(() -> {
            sqlScriptExecutor.executeScriptsForSchema(null, "public", "classpath*:db/test/*.sql");
        }, "Should handle null DataSource gracefully");
    }

    @Test
    @DisplayName("Should handle executeScriptsForSchema with nonexistent resource pattern")
    void testHandleNonexistentResourcePattern() {
        // This will likely just log an error but not throw
        assertDoesNotThrow(() -> {
            sqlScriptExecutor.executeScriptsForSchema(null, "public", 
                "classpath*:db/absolutely/nonexistent/path/*.sql");
        }, "Should handle nonexistent resource patterns gracefully");
    }

    @Test
    @DisplayName("Should handle executeScriptsForSchema with empty schema name")
    void testHandleEmptySchema() {
        assertDoesNotThrow(() -> {
            sqlScriptExecutor.executeScriptsForSchema(null, "", "classpath*:db/test/*.sql");
        }, "Should handle empty schema");
    }

    @Test
    @DisplayName("Should accept different schema names")
    void testAcceptDifferentSchemaNames() {
        String[] schemas = {"public", "custom_schema", "test", "prod"};
        
        for (String schema : schemas) {
            assertDoesNotThrow(() -> {
                sqlScriptExecutor.executeScriptsForSchema(null, schema, 
                    "classpath*:db/nonexistent/*.sql");
            }, "Should accept schema: " + schema);
        }
    }

    @Test
    @DisplayName("Should accept different location patterns")
    void testAcceptDifferentLocationPatterns() {
        String[] patterns = {
            "classpath*:db/test/*.sql",
            "classpath:db/script.sql",
            "file:db/test/*.sql",
            ""
        };
        
        for (String pattern : patterns) {
            assertDoesNotThrow(() -> {
                sqlScriptExecutor.executeScriptsForSchema(null, "public", pattern);
            }, "Should accept pattern: " + pattern);
        }
    }

    @Test
    @DisplayName("Should handle multiple invocations")
    void testMultipleInvocations() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                sqlScriptExecutor.executeScriptsForSchema(null, "schema" + i, 
                    "classpath*:db/test_" + i + "/*.sql");
            }
        }, "Should handle multiple invocations");
    }
}

