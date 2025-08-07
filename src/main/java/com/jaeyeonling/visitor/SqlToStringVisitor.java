package com.jaeyeonling.visitor;

import com.jaeyeonling.ast.clause.FromClause;
import com.jaeyeonling.ast.clause.GroupByClause;
import com.jaeyeonling.ast.clause.HavingClause;
import com.jaeyeonling.ast.clause.LimitClause;
import com.jaeyeonling.ast.clause.OrderByClause;
import com.jaeyeonling.ast.clause.SelectClause;
import com.jaeyeonling.ast.clause.WhereClause;
import com.jaeyeonling.ast.expression.AllColumns;
import com.jaeyeonling.ast.expression.BetweenExpression;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.BooleanLiteral;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.DecimalLiteral;
import com.jaeyeonling.ast.expression.ExpressionSelectItem;
import com.jaeyeonling.ast.expression.FunctionCall;
import com.jaeyeonling.ast.expression.InExpression;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.expression.IsNotNullExpression;
import com.jaeyeonling.ast.expression.IsNullExpression;
import com.jaeyeonling.ast.expression.LikeExpression;
import com.jaeyeonling.ast.expression.NotBetweenExpression;
import com.jaeyeonling.ast.expression.NotInExpression;
import com.jaeyeonling.ast.expression.NotLikeExpression;
import com.jaeyeonling.ast.expression.NullLiteral;
import com.jaeyeonling.ast.expression.StringLiteral;
import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.ast.table.Table;

import java.util.stream.Collectors;

/**
 * AST를 SQL 문자열로 변환하는 Visitor 구현체.
 */
public final class SqlToStringVisitor extends AbstractAstVisitor<String> {

    @Override
    public String visitSelectStatement(final SelectStatement selectStatement) {
        final StringBuilder sql = new StringBuilder();

        // SELECT 절
        sql.append(selectStatement.selectClause().accept(this));

        // FROM 절 (선택적)
        selectStatement.fromClause()
                .ifPresent(from -> sql.append(" ").append(from.accept(this)));

        // WHERE 절
        selectStatement.whereClause().ifPresent(where ->
                sql.append(" ").append(where.accept(this))
        );

        // GROUP BY 절
        selectStatement.groupByClause().ifPresent(groupBy ->
                sql.append(" ").append(groupBy.accept(this))
        );

        // HAVING 절
        selectStatement.havingClause().ifPresent(having ->
                sql.append(" ").append(having.accept(this))
        );

        // ORDER BY 절
        selectStatement.orderByClause().ifPresent(orderBy ->
                sql.append(" ").append(orderBy.accept(this))
        );

        // LIMIT 절
        selectStatement.limitClause().ifPresent(limit ->
                sql.append(" ").append(limit.accept(this))
        );

        return sql.toString();
    }

    @Override
    public String visitSelectClause(final SelectClause selectClause) {
        final StringBuilder sql = new StringBuilder("SELECT");

        if (selectClause.isDistinct()) {
            sql.append(" DISTINCT");
        }

        final String items = selectClause.selectItems().stream()
                .map(item -> item.accept(this))
                .collect(Collectors.joining(", "));

        sql.append(" ").append(items);
        return sql.toString();
    }

    @Override
    public String visitFromClause(final FromClause fromClause) {
        final String tables = fromClause.tableReferences().stream()
                .map(table -> table.accept(this))
                .collect(Collectors.joining(", "));

        return "FROM " + tables;
    }

    @Override
    public String visitWhereClause(final WhereClause whereClause) {
        return "WHERE " + whereClause.condition().accept(this);
    }

    @Override
    public String visitGroupByClause(final GroupByClause groupByClause) {
        final String expressions = groupByClause.groupingExpressions().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", "));

        return "GROUP BY " + expressions;
    }

    @Override
    public String visitHavingClause(final HavingClause havingClause) {
        return "HAVING " + havingClause.condition().accept(this);
    }

    @Override
    public String visitOrderByClause(final OrderByClause orderByClause) {
        final String items = orderByClause.orderByItems().stream()
                .map(item -> item.expression().accept(this) + " " + item.direction())
                .collect(Collectors.joining(", "));

        return "ORDER BY " + items;
    }

    @Override
    public String visitLimitClause(final LimitClause limitClause) {
        final StringBuilder sql = new StringBuilder("LIMIT ").append(limitClause.limit());

        limitClause.offset().ifPresent(offset ->
                sql.append(" OFFSET ").append(offset)
        );

        return sql.toString();
    }

    @Override
    public String visitTable(final Table table) {
        StringBuilder sql = new StringBuilder(table.name());

        table.alias().ifPresent(alias ->
                sql.append(" ").append(alias)
        );

        return sql.toString();
    }

    @Override
    public String visitColumnReference(final ColumnReference columnReference) {
        final StringBuilder sql = new StringBuilder();

        columnReference.tableName().ifPresent(table ->
                sql.append(table).append(".")
        );

        sql.append(columnReference.columnName());

        columnReference.alias().ifPresent(alias ->
                sql.append(" AS ").append(alias)
        );

        return sql.toString();
    }

    @Override
    public String visitAllColumns(final AllColumns allColumns) {
        return "*";
    }

    @Override
    public String visitExpressionSelectItem(final ExpressionSelectItem expressionSelectItem) {
        final String expr = expressionSelectItem.expression().accept(this);

        return expressionSelectItem.alias()
                .map(alias -> expr + " AS " + alias)
                .orElse(expr);
    }

    @Override
    public String visitBinaryOperatorExpression(final BinaryOperatorExpression binaryOperatorExpression) {
        final String left = binaryOperatorExpression.left().accept(this);
        final String right = binaryOperatorExpression.right().accept(this);
        final String operator = binaryOperatorExpression.operator().symbol();

        return left + " " + operator + " " + right;
    }

    @Override
    public String visitIntegerLiteral(final IntegerLiteral integerLiteral) {
        return String.valueOf(integerLiteral.value());
    }

    @Override
    public String visitDecimalLiteral(final DecimalLiteral decimalLiteral) {
        return String.valueOf(decimalLiteral.value());
    }

    @Override
    public String visitStringLiteral(final StringLiteral stringLiteral) {
        return "'" + stringLiteral.value().replace("'", "''") + "'";
    }

    @Override
    public String visitBooleanLiteral(final BooleanLiteral booleanLiteral) {
        return booleanLiteral.value() ? "TRUE" : "FALSE";
    }

    @Override
    public String visitNullLiteral(final NullLiteral nullLiteral) {
        return "NULL";
    }

    @Override
    public String visitLikeExpression(final LikeExpression likeExpression) {
        return likeExpression.expression().accept(this) +
                " LIKE " +
                likeExpression.pattern().accept(this);
    }

    @Override
    public String visitNotLikeExpression(final NotLikeExpression notLikeExpression) {
        return notLikeExpression.expression().accept(this) +
                " NOT LIKE " +
                notLikeExpression.pattern().accept(this);
    }

    @Override
    public String visitInExpression(final InExpression inExpression) {
        return inExpression.expression().accept(this) +
                " IN (" +
                inExpression.values().stream()
                        .map(v -> v.accept(this))
                        .collect(Collectors.joining(", ")) +
                ")";
    }

    @Override
    public String visitNotInExpression(final NotInExpression notInExpression) {
        return notInExpression.expression().accept(this) +
                " NOT IN (" +
                notInExpression.values().stream()
                        .map(v -> v.accept(this))
                        .collect(Collectors.joining(", ")) +
                ")";
    }

    @Override
    public String visitBetweenExpression(final BetweenExpression betweenExpression) {
        return betweenExpression.expression().accept(this) +
                " BETWEEN " +
                betweenExpression.lowerBound().accept(this) +
                " AND " +
                betweenExpression.upperBound().accept(this);
    }

    @Override
    public String visitNotBetweenExpression(final NotBetweenExpression notBetweenExpression) {
        return notBetweenExpression.expression().accept(this) +
                " NOT BETWEEN " +
                notBetweenExpression.lowerBound().accept(this) +
                " AND " +
                notBetweenExpression.upperBound().accept(this);
    }

    @Override
    public String visitIsNullExpression(final IsNullExpression isNullExpression) {
        return isNullExpression.expression().accept(this) + " IS NULL";
    }

    @Override
    public String visitIsNotNullExpression(final IsNotNullExpression isNotNullExpression) {
        return isNotNullExpression.expression().accept(this) + " IS NOT NULL";
    }

    @Override
    public String visitFunctionCall(final FunctionCall functionCall) {
        final String args = functionCall.arguments().stream()
                .map(arg -> arg.accept(this))
                .collect(Collectors.joining(", "));
        
        return functionCall.functionName() + "(" + args + ")";
    }
}
