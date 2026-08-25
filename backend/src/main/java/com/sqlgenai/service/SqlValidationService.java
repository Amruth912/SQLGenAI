package com.sqlgenai.service;

import com.sqlgenai.exception.SqlValidationException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqlValidationService {
    private static final Logger log = LoggerFactory.getLogger(SqlValidationService.class);

    public void validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SqlValidationException("SQL query cannot be empty");
        }

        try {
            Statements statements = CCJSqlParserUtil.parseStatements(sql);
            
            if (statements.getStatements().isEmpty()) {
                throw new SqlValidationException("No SQL statement found");
            }
            
            if (statements.getStatements().size() > 1) {
                throw new SqlValidationException("Only a single SQL statement is allowed");
            }
            
            Statement statement = statements.getStatements().get(0);
            
            if (!(statement instanceof Select)) {
                throw new SqlValidationException("Only SELECT statements are allowed. Modifying statements are strictly prohibited.");
            }
            
            log.debug("SQL validation passed for: {}", sql);
        } catch (JSQLParserException e) {
            log.warn("SQL parsing failed: {}", e.getMessage());
            throw new SqlValidationException("Invalid SQL syntax: " + e.getMessage());
        }
    }
}
