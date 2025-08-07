package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.expression.BetweenExpression;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.InExpression;
import com.jaeyeonling.ast.expression.IsNotNullExpression;
import com.jaeyeonling.ast.expression.IsNullExpression;
import com.jaeyeonling.ast.expression.LikeExpression;
import com.jaeyeonling.ast.expression.NotBetweenExpression;
import com.jaeyeonling.ast.expression.NotInExpression;
import com.jaeyeonling.ast.expression.NotLikeExpression;
import com.jaeyeonling.ast.expression.Operator;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("특수 연산자 테스트 (LIKE, IN, BETWEEN, IS NULL)")
class SpecialOperatorsTest extends BaseSqlParserTest {

    @Test
    @DisplayName("LIKE 연산자를 파싱한다")
    void testLikeOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE name LIKE 'John%'";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(LikeExpression.class);
    }

    @Test
    @DisplayName("NOT LIKE 연산자를 파싱한다")
    void testNotLikeOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE name NOT LIKE 'John%'";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(NotLikeExpression.class);
    }

    @Test
    @DisplayName("IN 연산자를 파싱한다")
    void testInOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE status IN ('active', 'pending', 'inactive')";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(InExpression.class);

        final InExpression in = (InExpression) condition;
        // 타입이 InExpression이면 NOT이 아님
        assertThat(in.values()).hasSize(3);
    }

    @Test
    @DisplayName("NOT IN 연산자를 파싱한다")
    void testNotInOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE status NOT IN ('deleted', 'banned')";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(NotInExpression.class);

        final NotInExpression notIn = (NotInExpression) condition;
        // 타입이 NotInExpression이면 NOT IN
        assertThat(notIn.values()).hasSize(2);
    }

    @Test
    @DisplayName("BETWEEN 연산자를 파싱한다")
    void testBetweenOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE age BETWEEN 18 AND 65";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(BetweenExpression.class);
    }

    @Test
    @DisplayName("NOT BETWEEN 연산자를 파싱한다")
    void testNotBetweenOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE age NOT BETWEEN 1 AND 17";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(NotBetweenExpression.class);
    }

    @Test
    @DisplayName("IS NULL 연산자를 파싱한다")
    void testIsNullOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE email IS NULL";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(IsNullExpression.class);
    }

    @Test
    @DisplayName("IS NOT NULL 연산자를 파싱한다")
    void testIsNotNullOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE email IS NOT NULL";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(IsNotNullExpression.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM users WHERE name LIKE '%test%'",
            "SELECT * FROM products WHERE category IN ('electronics', 'books')",
            "SELECT * FROM orders WHERE price BETWEEN 100 AND 500",
            "SELECT * FROM customers WHERE phone IS NULL",
            "SELECT * FROM employees WHERE department NOT IN ('HR', 'IT')",
            "SELECT * FROM sales WHERE amount NOT BETWEEN 0 AND 100",
            "SELECT * FROM users WHERE email IS NOT NULL"
    })
    @DisplayName("다양한 특수 연산자를 파싱한다")
    void testVariousSpecialOperators(final String sql) {
        // when & then
        assertDoesNotThrow(() -> new SqlParser(sql).parse());
    }

    @Test
    @DisplayName("복합 조건을 파싱한다")
    void testComplexConditions() {
        // given
        final String sql = "SELECT * FROM users WHERE " +
                "name LIKE 'J%' AND " +
                "status IN ('active', 'pending') AND " +
                "age BETWEEN 18 AND 65 AND " +
                "email IS NOT NULL";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
    }

    @Test
    @DisplayName("LIKE 연산자를 포함한 SQL을 문자열로 변환한다")
    void testLikeToString() {
        // given
        final String sql = "SELECT * FROM users WHERE name LIKE 'John%'";
        parseAndAssertReconstructed(sql);
    }

    @Test
    @DisplayName("IN 연산자를 포함한 SQL을 문자열로 변환한다")
    void testInToString() {
        // given
        final String sql = "SELECT * FROM users WHERE status IN ('active', 'pending')";
        parseAndAssertReconstructed(sql);
    }

    @Test
    @DisplayName("BETWEEN 연산자를 포함한 SQL을 문자열로 변환한다")
    void testBetweenToString() {
        // given
        final String sql = "SELECT * FROM users WHERE age BETWEEN 18 AND 65";
        parseAndAssertReconstructed(sql);
    }

    @Test
    @DisplayName("IS NULL 연산자를 포함한 SQL을 문자열로 변환한다")
    void testIsNullToString() {
        // given
        final String sql = "SELECT * FROM users WHERE email IS NULL";
        parseAndAssertReconstructed(sql);
    }

    @Test
    @DisplayName("IS NOT NULL 연산자를 포함한 SQL을 문자열로 변환한다")
    void testIsNotNullToString() {
        // given
        final String sql = "SELECT * FROM users WHERE email IS NOT NULL";
        parseAndAssertReconstructed(sql);
    }

    @Test
    @DisplayName("산술 연산자와 BETWEEN을 함께 사용할 수 있다")
    void testBetweenWithArithmeticExpression() {
        // given
        final String sql = "SELECT * FROM products WHERE price * 1.1 BETWEEN 50 AND 100";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();
        assertThat(condition).isInstanceOf(BetweenExpression.class);

        final BetweenExpression between = (BetweenExpression) condition;
        assertThat(between.expression()).isInstanceOf(BinaryOperatorExpression.class);

        // SQL 재생성 확인
        final String regenerated = stmt.accept(visitor);
        assertThat(regenerated).isEqualTo(sql);
    }

    @Test
    @DisplayName("여러 특수 연산자를 복합적으로 사용할 수 있다")
    void testComplexSpecialOperators() {
        // given
        final String sql = "SELECT id, name, email FROM users " +
                "WHERE name LIKE 'J%' " +
                "AND age BETWEEN 25 AND 65 " +
                "AND status IN ('active', 'premium') " +
                "AND email IS NOT NULL";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression condition = stmt.whereClause().get().condition();

        // 최상위는 AND 연산자여야 함
        assertThat(condition).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression andExpr = (BinaryOperatorExpression) condition;
        assertThat(andExpr.operator()).isEqualTo(Operator.AND);

        // SQL 재생성 확인
        final String regenerated = stmt.accept(visitor);
        assertThat(regenerated).isEqualTo(sql);
    }

    @ParameterizedTest
    @DisplayName("다양한 특수 연산자 시나리오를 파싱하고 재생성한다")
    @ValueSource(strings = {
            "SELECT * FROM products WHERE description NOT LIKE '%discontinued%'",
            "SELECT * FROM employees WHERE department NOT IN ('HR', 'Finance')",
            "SELECT * FROM products WHERE price BETWEEN 10.99 AND 99.99",
            "SELECT * FROM students WHERE age NOT BETWEEN 1 AND 17",
            "SELECT * FROM customers WHERE email IS NULL",
            "SELECT * FROM users WHERE phone IS NOT NULL"
    })
    void testVariousSpecialOperatorScenarios(final String sql) {
        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.whereClause()).isPresent();

        // SQL 재생성이 원본과 일치하는지 확인
        final String regenerated = stmt.accept(visitor);
        assertThat(regenerated).isEqualTo(sql);
    }

    @Test
    @DisplayName("IN 절에 빈 목록이 오면 에러가 발생한다")
    void testInWithEmptyList() {
        // given
        final String sql = "SELECT * FROM users WHERE status IN ()";

        // when & then
        assertParseException(sql);
    }

    @Test
    @DisplayName("BETWEEN에 AND가 없으면 에러가 발생한다")
    void testBetweenWithoutAnd() {
        // given
        final String sql = "SELECT * FROM users WHERE age BETWEEN 18 65";

        // when & then
        assertParseException(sql, "BETWEEN");
    }

    @Test
    @DisplayName("IS 다음에 NULL이 없으면 에러가 발생한다")
    void testIsWithoutNull() {
        // given
        final String sql = "SELECT * FROM users WHERE email IS";

        // when & then
        assertParseException(sql, "NULL");
    }
}
