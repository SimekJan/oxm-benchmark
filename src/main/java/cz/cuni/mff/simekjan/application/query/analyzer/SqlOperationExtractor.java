package cz.cuni.mff.simekjan.application.query.analyzer;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.operation.OperationChildSlot;
import cz.cuni.mff.simekjan.application.operation.OperationEntity;
import cz.cuni.mff.simekjan.application.operation.OperationService;
import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BooleanValue;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
final class SqlOperationExtractor {

    private final OperationService operationService;

    /**
     * Parses one SELECT statement and adds its extracted operation tree to the query.
     *
     * @param dataset     schema used to resolve SQL tables and columns
     * @param queryEntity query that will own the extracted operations
     * @param sql         SQL text containing exactly one statement
     */
    void extract(DatasetEntity dataset, QueryEntity queryEntity, String sql) {
        if (dataset == null || queryEntity == null || sql == null || sql.isBlank()) {
            throw new IllegalArgumentException("Dataset, query entity, and SQL text are required for SQL analysis");
        }

        Statement statement;
        try {
            var statements = CCJSqlParserUtil.parseStatements(sql);
            if (statements.size() != 1) {
                throw new IllegalArgumentException("A SQL query must contain exactly one statement");
            }
            statement = statements.getFirst();
        } catch (JSQLParserException exception) {
            throw new IllegalArgumentException("Invalid SQL query", exception);
        }

        if (!(statement instanceof Select select)) {
            throw new IllegalArgumentException("Only a single SELECT statement can be analyzed");
        }
        PlainSelect plainSelect = select.getPlainSelect();
        if (plainSelect == null) {
            throw new IllegalArgumentException("Only a single SELECT statement can be analyzed");
        }
        validateSupportedSelect(select, plainSelect);

        ColumnResolver columnResolver = new ColumnResolver(dataset);
        columnResolver.register(requireTable(plainSelect.getFromItem(), "FROM"));
        List<OperationEntity> operations = new ArrayList<>();

        for (Join join : nullToEmpty(plainSelect.getJoins())) {
            if (!join.isInnerJoin() || join.isOuter() || join.isStraight() || join.isSemi()
                || join.isApply() || join.isAsOf() || join.isLeft() || join.isRight() || join.isFull()
                || join.isCross() || join.isNatural() || join.isSimple()) {
                throw new IllegalArgumentException("Only inner equi-joins are supported");
            }
            Table joinedTable = requireTable(join.getFromItem(), "JOIN");
            columnResolver.register(joinedTable);
            Collection<Expression> onExpressions = join.getOnExpressions();
            if (onExpressions == null || onExpressions.isEmpty()) {
                throw new IllegalArgumentException("JOIN requires an equality condition between columns");
            }
            for (Expression onExpression : onExpressions) {
                operations.addAll(extractJoinOperations(onExpression, queryEntity, columnResolver));
            }
        }

        for (SelectItem<?> selectItem : plainSelect.getSelectItems()) {
            Expression expression = selectItem.getExpression();
            if (expression instanceof Function function) {
                operations.add(extractAggregate(function, queryEntity, columnResolver));
            } else if (expression instanceof Column column) {
                columnResolver.resolve(column);
            } else if (expression instanceof net.sf.jsqlparser.statement.select.AllTableColumns allTableColumns) {
                columnResolver.resolveTable(allTableColumns.getTable().getName());
            } else if (!(expression instanceof net.sf.jsqlparser.statement.select.AllColumns) && !isLiteral(expression)) {
                throw unsupported("SELECT expression", expression);
            }
        }

        if (plainSelect.getWhere() != null) {
            operations.add(extractPredicate(plainSelect.getWhere(), queryEntity, columnResolver));
        }

        for (OrderByElement orderByElement : nullToEmpty(select.getOrderByElements())) {
            if (!(orderByElement.getExpression() instanceof Column column)) {
                throw unsupported("ORDER BY expression", orderByElement.getExpression());
            }
            OperationEntity sort = createOperation(queryEntity, "SORT");
            sort.setColumn(columnResolver.resolve(column));
            operations.add(sort);
        }

        combineAtRoot(queryEntity, operations);
    }

    /**
     * Rejects SELECT clauses that cannot currently be represented as operations.
     *
     * @param select      parsed SELECT statement
     * @param plainSelect SELECT body containing its clauses
     */
    private void validateSupportedSelect(Select select, PlainSelect plainSelect) {
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            throw new IllegalArgumentException("WITH queries are not supported");
        }
        if (plainSelect.getDistinct() != null || plainSelect.getGroupBy() != null
            || plainSelect.getHaving() != null || plainSelect.getQualify() != null
            || select.getLimit() != null || select.getOffset() != null || select.getFetch() != null) {
            throw new IllegalArgumentException("DISTINCT, grouping, HAVING, and row limits are not supported");
        }
    }

    /**
     * Converts an inner equi-join condition into JOIN operations.
     *
     * @param expression     parsed ON condition
     * @param queryEntity    query that will own the operations
     * @param columnResolver resolves both join columns against the dataset
     */
    private List<OperationEntity> extractJoinOperations(
        Expression expression,
        QueryEntity queryEntity,
        ColumnResolver columnResolver
    ) {
        if (expression instanceof ParenthesedExpressionList<?> expressionList && expressionList.size() == 1) {
            return extractJoinOperations(expressionList.getFirst(), queryEntity, columnResolver);
        }
        if (expression instanceof AndExpression andExpression) {
            List<OperationEntity> operations = new ArrayList<>();
            operations.addAll(extractJoinOperations(andExpression.getLeftExpression(), queryEntity, columnResolver));
            operations.addAll(extractJoinOperations(andExpression.getRightExpression(), queryEntity, columnResolver));
            return operations;
        }
        if (!(expression instanceof EqualsTo equalsTo)
            || !(equalsTo.getLeftExpression() instanceof Column left)
            || !(equalsTo.getRightExpression() instanceof Column right)) {
            throw new IllegalArgumentException("JOIN conditions must be equality comparisons between columns");
        }

        OperationEntity join = createOperation(queryEntity, "JOIN");
        join.setColumn1(columnResolver.resolve(left));
        join.setColumn2(columnResolver.resolve(right));
        return List.of(join);
    }

    /**
     * Converts a supported aggregate function into a column operation.
     *
     * @param function       parsed SQL function
     * @param queryEntity    query that will own the operation
     * @param columnResolver resolves the function's column against the dataset
     */
    private OperationEntity extractAggregate(
        Function function,
        QueryEntity queryEntity,
        ColumnResolver columnResolver
    ) {
        String functionName = function.getName().toUpperCase(Locale.ROOT);
        String operationType = switch (functionName) {
            case "COUNT" -> "COUNT";
            case "MIN", "MAX" -> "EXTREME";
            default -> throw new IllegalArgumentException("Unsupported SQL aggregate: " + function.getName());
        };
        if (function.isDistinct() || function.isAllColumns() || function.getParameters() == null
            || function.getParameters().size() != 1
            || !(function.getParameters().getFirst() instanceof Column column)) {
            throw new IllegalArgumentException(
                function.getName() + " must contain exactly one non-distinct column argument"
            );
        }

        OperationEntity operation = createOperation(queryEntity, operationType);
        operation.setColumn(columnResolver.resolve(column));
        return operation;
    }

    /**
     * Converts a WHERE expression into leaf operations and AND/OR/NOT parent nodes.
     *
     * @param expression     parsed WHERE expression or one of its nested expressions
     * @param queryEntity    query that will own the created operations
     * @param columnResolver resolves referenced columns against the dataset
     */
    private OperationEntity extractPredicate(
        Expression expression,
        QueryEntity queryEntity,
        ColumnResolver columnResolver
    ) {
        if (expression instanceof ParenthesedExpressionList<?> expressionList && expressionList.size() == 1) {
            return extractPredicate(expressionList.getFirst(), queryEntity, columnResolver);
        }
        if (expression instanceof AndExpression andExpression) {
            return createBinaryOperation(
                queryEntity,
                "AND",
                extractPredicate(andExpression.getLeftExpression(), queryEntity, columnResolver),
                extractPredicate(andExpression.getRightExpression(), queryEntity, columnResolver)
            );
        }
        if (expression instanceof OrExpression orExpression) {
            return createBinaryOperation(
                queryEntity,
                "OR",
                extractPredicate(orExpression.getLeftExpression(), queryEntity, columnResolver),
                extractPredicate(orExpression.getRightExpression(), queryEntity, columnResolver)
            );
        }
        if (expression instanceof NotExpression notExpression) {
            OperationEntity not = createOperation(queryEntity, "NOT");
            operationService.addChild(
                not,
                extractPredicate(notExpression.getExpression(), queryEntity, columnResolver),
                OperationChildSlot.OPERATION
            );
            return not;
        }
        if (expression instanceof EqualsTo equalsTo) {
            return extractComparison(equalsTo, "MATCH", queryEntity, columnResolver);
        }
        if (expression instanceof GreaterThan greaterThan) {
            return extractComparison(greaterThan, "MORE_THAN", queryEntity, columnResolver);
        }
        if (expression instanceof MinorThan minorThan) {
            return extractComparison(minorThan, "LESS_THAN", queryEntity, columnResolver);
        }
        if (expression instanceof Between between) {
            if (between.isNot() || !(between.getLeftExpression() instanceof Column column)) {
                throw new IllegalArgumentException("Only positive BETWEEN predicates on a column are supported");
            }
            OperationEntity range = createOperation(queryEntity, "RANGE");
            range.setColumn(columnResolver.resolve(column));
            range.setValue1(literalValue(between.getBetweenExpressionStart()));
            range.setValue2(literalValue(between.getBetweenExpressionEnd()));
            return range;
        }
        throw unsupported("WHERE predicate", expression);
    }

    /**
     * Creates a comparison operation, reversing directional comparisons when needed.
     *
     * @param comparison     parsed comparison expression
     * @param operationType  operation type when the column is on the left
     * @param queryEntity    query that will own the operation
     * @param columnResolver resolves the column against the dataset
     */
    private OperationEntity extractComparison(
        net.sf.jsqlparser.expression.BinaryExpression comparison,
        String operationType,
        QueryEntity queryEntity,
        ColumnResolver columnResolver
    ) {
        Expression left = comparison.getLeftExpression();
        Expression right = comparison.getRightExpression();
        if (left instanceof Column column) {
            OperationEntity operation = createOperation(queryEntity, operationType);
            operation.setColumn(columnResolver.resolve(column));
            operation.setValue(literalValue(right));
            return operation;
        }
        if (right instanceof Column column) {
            String reversedType = switch (operationType) {
                case "LESS_THAN" -> "MORE_THAN";
                case "MORE_THAN" -> "LESS_THAN";
                default -> operationType;
            };
            OperationEntity operation = createOperation(queryEntity, reversedType);
            operation.setColumn(columnResolver.resolve(column));
            operation.setValue(literalValue(left));
            return operation;
        }
        throw new IllegalArgumentException("Comparisons must have one column and one literal value");
    }

    /**
     * Converts a supported SQL literal to the string stored by an operation.
     *
     * @param expression parsed literal expression
     */
    private String literalValue(Expression expression) {
        if (expression instanceof StringValue stringValue) {
            return stringValue.getValue();
        }
        if (expression instanceof LongValue || expression instanceof DoubleValue
            || expression instanceof DateValue || expression instanceof TimeValue
            || expression instanceof TimestampValue || expression instanceof BooleanValue) {
            return expression.toString();
        }
        if (expression instanceof SignedExpression signedExpression) {
            return signedExpression.getSign() + literalValue(signedExpression.getExpression());
        }
        throw new IllegalArgumentException("Expected a literal value but found " + expression);
    }

    /**
     * Creates a binary parent operation and attaches its left and right children.
     *
     * @param queryEntity   query that owns the operation tree
     * @param operationType parent type, either AND or OR
     * @param first         left child
     * @param second        right child
     */
    private OperationEntity createBinaryOperation(
        QueryEntity queryEntity,
        String operationType,
        OperationEntity first,
        OperationEntity second
    ) {
        OperationEntity parent = createOperation(queryEntity, operationType);
        operationService.addChild(parent, first, OperationChildSlot.OPERATION1);
        operationService.addChild(parent, second, OperationChildSlot.OPERATION2);
        return parent;
    }

    /**
     * Creates an operation of the given type for the query.
     *
     * @param queryEntity   query that owns the operation
     * @param operationType operation type to create
     */
    private OperationEntity createOperation(QueryEntity queryEntity, String operationType) {
        return operationService.createOperation(queryEntity, operationType);
    }

    /**
     * Combines independent query operations under AND nodes.
     *
     * @param queryEntity query that owns the operations
     * @param operations  operations extracted from separate SELECT clauses
     */
    private void combineAtRoot(QueryEntity queryEntity, List<OperationEntity> operations) {
        if (operations.size() < 2) {
            return;
        }
        OperationEntity combined = operations.getFirst();
        for (int index = 1; index < operations.size(); index++) {
            combined = createBinaryOperation(queryEntity, "AND", combined, operations.get(index));
        }
    }

    /**
     * Requires a direct table reference in a FROM or JOIN clause.
     *
     * @param fromItem parsed table source
     * @param clause   clause name used in the error message
     */
    private Table requireTable(FromItem fromItem, String clause) {
        if (fromItem instanceof Table table) {
            return table;
        }
        throw new IllegalArgumentException(clause + " must reference a dataset table directly");
    }

    /**
     * Builds a consistent error for an unsupported expression.
     *
     * @param context    SQL clause containing the expression
     * @param expression expression that could not be mapped
     */
    private IllegalArgumentException unsupported(String context, Expression expression) {
        return new IllegalArgumentException(
            "Unsupported " + context + ": " + expression.getClass().getSimpleName()
        );
    }

    /**
     * Checks whether a SELECT item is a literal rather than a column or operation.
     *
     * @param expression SELECT item expression to check
     */
    private boolean isLiteral(Expression expression) {
        return expression instanceof StringValue || expression instanceof LongValue
            || expression instanceof DoubleValue || expression instanceof DateValue
            || expression instanceof TimeValue || expression instanceof TimestampValue
            || expression instanceof BooleanValue || expression instanceof SignedExpression;
    }

    /**
     * Replaces an optional parser list with an empty list.
     *
     * @param values parser-produced list that may be null
     */
    private <T> List<T> nullToEmpty(List<T> values) {
        return values == null ? List.of() : values;
    }

    /**
     * Resolves SQL table and column references to entities in the dataset schema.
     */
    @RequiredArgsConstructor
    private static final class ColumnResolver {

        private final DatasetEntity dataset;
        private final Map<String, TableEntity> tablesByQualifier = new LinkedHashMap<>();

        /**
         * Adds a FROM/JOIN table and its SQL name or alias to the lookup map.
         *
         * @param sqlTable table reference from the parsed query
         */
        private void register(Table sqlTable) {
            List<TableEntity> matches = dataset.getTables().stream()
                .filter(candidate -> matchesIdentifier(candidate.getName(), sqlTable.getName()))
                .toList();
            if (matches.size() != 1) {
                throw new IllegalArgumentException(
                    matches.isEmpty()
                        ? "Unknown table in SQL query: " + sqlTable.getName()
                        : "Ambiguous table in SQL query: " + sqlTable.getName()
                );
            }
            TableEntity table = matches.getFirst();
            String qualifier = sqlTable.getAlias() == null
                ? sqlTable.getName()
                : sqlTable.getAlias().getName();
            String qualifierName = normalizeSqlIdentifier(qualifier);
            if (tablesByQualifier.containsKey(qualifierName)) {
                throw new IllegalArgumentException("Duplicate table alias in SQL query: " + qualifier);
            }
            tablesByQualifier.put(qualifierName, table);
        }

        /**
         * Finds a dataset column using a qualified or unqualified SQL column reference.
         *
         * @param sqlColumn parsed SQL column reference
         */
        private ColumnEntity resolve(Column sqlColumn) {
            String qualifier = sqlColumn.getTable() == null ? null : sqlColumn.getTable().getName();
            if (qualifier != null && !qualifier.isBlank()) {
                TableEntity table = resolveTable(qualifier);
                return findColumn(table, sqlColumn.getColumnName());
            }

            List<ColumnEntity> matches = tablesByQualifier.values().stream()
                .flatMap(table -> table.getColumns().stream())
                .filter(column -> matchesIdentifier(column.getName(), sqlColumn.getColumnName()))
                .toList();
            if (matches.size() != 1) {
                throw new IllegalArgumentException(
                    "Column " + sqlColumn.getColumnName()
                        + (matches.isEmpty() ? " does not exist in the query tables" : " is ambiguous")
                );
            }
            return matches.getFirst();
        }

        /**
         * Finds a table by its SQL name or alias.
         *
         * @param sqlQualifier table name or alias from the query
         */
        private TableEntity resolveTable(String sqlQualifier) {
            TableEntity table = tablesByQualifier.get(normalizeSqlIdentifier(sqlQualifier));
            if (table == null) {
                throw new IllegalArgumentException("Unknown table or alias: " + sqlQualifier);
            }
            return table;
        }

        /**
         * Finds a named column on one resolved table.
         *
         * @param table      dataset table to search
         * @param columnName SQL column identifier
         */
        private ColumnEntity findColumn(TableEntity table, String columnName) {
            List<ColumnEntity> matches = table.getColumns().stream()
                .filter(column -> matchesIdentifier(column.getName(), columnName))
                .toList();
            if (matches.size() != 1) {
                throw new IllegalArgumentException(
                    matches.isEmpty()
                        ? "Unknown column " + columnName + " in table " + table.getName()
                        : "Ambiguous column " + columnName + " in table " + table.getName()
                );
            }
            return matches.getFirst();
        }

        /**
         * Compares a schema identifier with an SQL identifier, respecting quoted names.
         *
         * @param schemaIdentifier identifier stored in the dataset
         * @param sqlIdentifier    identifier parsed from SQL
         */
        private static boolean matchesIdentifier(String schemaIdentifier, String sqlIdentifier) {
            return schemaIdentifier.equals(normalizeSqlIdentifier(sqlIdentifier));
        }

        /**
         * Unquotes SQL identifiers or lowercases unquoted identifiers.
         *
         * @param identifier SQL table, alias, or column identifier
         */
        private static String normalizeSqlIdentifier(String identifier) {
            if (isQuoted(identifier)) {
                return identifier.substring(1, identifier.length() - 1).replace("\"\"", "\"");
            }
            return identifier.toLowerCase(Locale.ROOT);
        }

        /**
         * Checks whether an SQL identifier is double-quoted.
         *
         * @param identifier SQL identifier
         */
        private static boolean isQuoted(String identifier) {
            return identifier.length() >= 2 && identifier.startsWith("\"") && identifier.endsWith("\"");
        }
    }
}
