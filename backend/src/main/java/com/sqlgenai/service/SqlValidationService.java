package com.sqlgenai.service;

import com.sqlgenai.exception.SqlValidationException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqlValidationService {
    private static final Logger log = LoggerFactory.getLogger(SqlValidationService.class);

    public SqlValidationResult validateAndClassify(String sql) {
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
            
            String statementType;
            boolean readOnly;
            String riskLevel;

            if (statement instanceof Select) {
                statementType = "SELECT";
                readOnly = true;
                riskLevel = "READ_ONLY";
            } else if (statement instanceof Insert) {
                statementType = "INSERT";
                readOnly = false;
                riskLevel = "MUTATING";
            } else if (statement instanceof Update) {
                statementType = "UPDATE";
                readOnly = false;
                riskLevel = "MUTATING";
            } else if (statement instanceof Delete) {
                statementType = "DELETE";
                readOnly = false;
                riskLevel = "DESTRUCTIVE";
            } else if (statement instanceof CreateTable) {
                statementType = "CREATE_TABLE";
                readOnly = false;
                riskLevel = "DDL";
            } else if (statement instanceof Alter) {
                statementType = "ALTER";
                readOnly = false;
                riskLevel = "DDL";
            } else if (statement instanceof Drop) {
                statementType = "DROP";
                readOnly = false;
                riskLevel = "DESTRUCTIVE";
            } else if (statement instanceof Truncate) {
                statementType = "TRUNCATE";
                readOnly = false;
                riskLevel = "DESTRUCTIVE";
            } else if (statement instanceof CreateIndex || statement instanceof CreateView) {
                statementType = "CREATE";
                readOnly = false;
                riskLevel = "DDL";
            } else {
                statementType = statement.getClass().getSimpleName().toUpperCase();
                readOnly = false;
                riskLevel = "MUTATING";
            }
            
            log.debug("SQL validation passed for {} statement: {}", statementType, sql);
            return new SqlValidationResult(statementType, readOnly, !readOnly, riskLevel);
        } catch (JSQLParserException e) {
            log.warn("SQL parsing failed: {}", e.getMessage());
            throw new SqlValidationException("Invalid SQL syntax: " + e.getMessage());
        }
    }

    public void validateSql(String sql) {
        validateAndClassify(sql);
    }
}
