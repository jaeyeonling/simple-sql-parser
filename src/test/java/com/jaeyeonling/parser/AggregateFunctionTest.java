package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.expression.ExpressionSelectItem;
import com.jaeyeonling.ast.expression.FunctionCall;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 집계 함수 파싱 테스트
 */
class AggregateFunctionTest extends BaseSqlParserTest {

    @Test
    @DisplayName("COUNT(*) 함수를 파싱한다")
    void testParseCountStar() {
        // given
        final String sql = "SELECT COUNT(*) FROM users";
        
        // when
        final SelectStatement stmt = parseAndValidate(sql);
        
        // then
        assertThat(stmt.selectClause().selectItems()).hasSize(1);
        final var item = stmt.selectClause().selectItems().getFirst();
        assertThat(item).isInstanceOf(ExpressionSelectItem.class);
        
        final ExpressionSelectItem exprItem = (ExpressionSelectItem) item;
        assertThat(exprItem.expression()).isInstanceOf(FunctionCall.class);
        
        final FunctionCall func = (FunctionCall) exprItem.expression();
        assertThat(func.functionName()).isEqualTo("COUNT");
        assertThat(func.arguments()).hasSize(1);
        assertThat(func.isAggregateFunction()).isTrue();
    }
    
    @Test
    @DisplayName("COUNT(column) 함수를 파싱한다")
    void testParseCountColumn() {
        // given
        final String sql = "SELECT COUNT(id) FROM users";
        
        // when
        final SelectStatement stmt = parseAndValidate(sql);
        
        // then
        final var item = stmt.selectClause().selectItems().getFirst();
        assertThat(item).isInstanceOf(ExpressionSelectItem.class);
        
        final ExpressionSelectItem exprItem = (ExpressionSelectItem) item;
        assertThat(exprItem.expression()).isInstanceOf(FunctionCall.class);
        
        final FunctionCall func = (FunctionCall) exprItem.expression();
        assertThat(func.functionName()).isEqualTo("COUNT");
        assertThat(func.arguments()).hasSize(1);
    }
    
    @Test
    @DisplayName("여러 집계 함수를 파싱한다")
    void testParseMultipleAggregateFunctions() {
        // given
        final String sql = "SELECT COUNT(*), SUM(amount), AVG(price), MIN(age), MAX(salary) FROM transactions";
        
        // when
        final SelectStatement stmt = parseAndValidate(sql);
        
        // then
        assertThat(stmt.selectClause().selectItems()).hasSize(5);
        
        final var functions = stmt.selectClause().selectItems().stream()
                .map(item -> (ExpressionSelectItem) item)
                .map(ExpressionSelectItem::expression)
                .map(expr -> (FunctionCall) expr)
                .map(FunctionCall::functionName)
                .toList();
        
        assertThat(functions).containsExactly("COUNT", "SUM", "AVG", "MIN", "MAX");
    }
    
    @Test
    @DisplayName("집계 함수의 재구성이 올바르게 동작한다")
    void testAggregateFunctionReconstruction() {
        // given
        final String sql = "SELECT COUNT(*), SUM(amount) FROM orders";
        
        // when & then
        parseAndAssertReconstructed(sql);
    }
    
    @Test
    @DisplayName("WHERE 절과 함께 집계 함수를 사용한다")
    void testAggregateFunctionWithWhere() {
        // given
        final String sql = "SELECT COUNT(*) FROM users WHERE age > 18";
        
        // when & then
        parseAndAssertReconstructed(sql);
    }
    
    @Test
    @DisplayName("HAVING 절에서 집계 함수를 사용한다")
    void testAggregateFunctionInHaving() {
        // given
        final String sql = "SELECT city, COUNT(*) FROM users GROUP BY city HAVING COUNT(*) > 10";
        
        // when & then
        parseAndAssertReconstructed(sql);
    }
    
    @Test
    @DisplayName("집계 함수를 별칭과 함께 사용한다")
    void testAggregateFunctionWithAlias() {
        // given
        final String sql = "SELECT COUNT(*) AS total_count, SUM(amount) AS total_amount FROM sales";
        final String expected = "SELECT COUNT(*) AS total_count, SUM(amount) AS total_amount FROM sales";
        
        // when & then
        parseAndAssertReconstructed(sql, expected);
    }
}