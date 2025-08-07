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

/**
 * AstVisitor의 기본 구현을 제공하는 추상 클래스.
 * 모든 메서드는 null을 반환하는 기본 구현을 가지고 있으며,
 * 필요한 메서드만 오버라이드하여 사용할 수 있습니다.
 *
 * @param <T> 방문 결과 타입
 */
public abstract class AbstractAstVisitor<T> implements AstVisitor<T> {

    // Statement
    @Override
    public T visitSelectStatement(SelectStatement selectStatement) {
        return null;
    }

    // Clauses
    @Override
    public T visitSelectClause(SelectClause selectClause) {
        return null;
    }

    @Override
    public T visitFromClause(FromClause fromClause) {
        return null;
    }

    @Override
    public T visitWhereClause(WhereClause whereClause) {
        return null;
    }

    @Override
    public T visitGroupByClause(GroupByClause groupByClause) {
        return null;
    }

    @Override
    public T visitHavingClause(HavingClause havingClause) {
        return null;
    }

    @Override
    public T visitOrderByClause(OrderByClause orderByClause) {
        return null;
    }

    @Override
    public T visitLimitClause(LimitClause limitClause) {
        return null;
    }

    // Table
    @Override
    public T visitTable(Table table) {
        return null;
    }

    // SelectItems
    @Override
    public T visitExpressionSelectItem(ExpressionSelectItem expressionSelectItem) {
        return null;
    }

    @Override
    public T visitAllColumns(AllColumns allColumns) {
        return null;
    }

    @Override
    public T visitColumnReference(ColumnReference columnReference) {
        return null;
    }

    // Expressions - Binary
    @Override
    public T visitBinaryOperatorExpression(BinaryOperatorExpression binaryOperatorExpression) {
        return null;
    }

    // Expressions - Literals
    @Override
    public T visitIntegerLiteral(IntegerLiteral integerLiteral) {
        return null;
    }

    @Override
    public T visitDecimalLiteral(DecimalLiteral decimalLiteral) {
        return null;
    }

    @Override
    public T visitStringLiteral(StringLiteral stringLiteral) {
        return null;
    }

    @Override
    public T visitBooleanLiteral(BooleanLiteral booleanLiteral) {
        return null;
    }

    @Override
    public T visitNullLiteral(NullLiteral nullLiteral) {
        return null;
    }

    // Expressions - Special Operators
    @Override
    public T visitLikeExpression(LikeExpression likeExpression) {
        return null;
    }

    @Override
    public T visitNotLikeExpression(NotLikeExpression notLikeExpression) {
        return null;
    }

    @Override
    public T visitInExpression(InExpression inExpression) {
        return null;
    }

    @Override
    public T visitNotInExpression(NotInExpression notInExpression) {
        return null;
    }

    @Override
    public T visitBetweenExpression(BetweenExpression betweenExpression) {
        return null;
    }

    @Override
    public T visitNotBetweenExpression(NotBetweenExpression notBetweenExpression) {
        return null;
    }

    @Override
    public T visitIsNullExpression(IsNullExpression isNullExpression) {
        return null;
    }

    @Override
    public T visitIsNotNullExpression(IsNotNullExpression isNotNullExpression) {
        return null;
    }
}
