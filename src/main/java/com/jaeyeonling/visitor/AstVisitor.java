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
 * AST를 순회하기 위한 Visitor 인터페이스.
 * 각 AST 노드 타입에 대한 visit 메서드를 정의합니다.
 *
 * @param <T> visitor가 반환하는 타입
 */
public interface AstVisitor<T> {

    // Statement visitors
    T visitSelectStatement(SelectStatement selectStatement);

    // Clause visitors
    T visitSelectClause(SelectClause selectClause);

    T visitFromClause(FromClause fromClause);

    T visitWhereClause(WhereClause whereClause);

    T visitGroupByClause(GroupByClause groupByClause);

    T visitHavingClause(HavingClause havingClause);

    T visitOrderByClause(OrderByClause orderByClause);

    T visitLimitClause(LimitClause limitClause);

    // Table visitors
    T visitTable(Table table);

    // Expression visitors
    T visitColumnReference(ColumnReference columnReference);

    T visitAllColumns(AllColumns allColumns);

    T visitExpressionSelectItem(ExpressionSelectItem expressionSelectItem);

    T visitBinaryOperatorExpression(BinaryOperatorExpression binaryOperatorExpression);

    T visitIntegerLiteral(IntegerLiteral integerLiteral);

    T visitDecimalLiteral(DecimalLiteral decimalLiteral);

    T visitStringLiteral(StringLiteral stringLiteral);

    T visitBooleanLiteral(BooleanLiteral booleanLiteral);

    T visitNullLiteral(NullLiteral nullLiteral);

    T visitLikeExpression(LikeExpression likeExpression);

    T visitNotLikeExpression(NotLikeExpression notLikeExpression);

    T visitInExpression(InExpression inExpression);

    T visitNotInExpression(NotInExpression notInExpression);

    T visitBetweenExpression(BetweenExpression betweenExpression);

    T visitNotBetweenExpression(NotBetweenExpression notBetweenExpression);

    T visitIsNullExpression(IsNullExpression isNullExpression);

    T visitIsNotNullExpression(IsNotNullExpression isNotNullExpression);
}
