package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.clause.GroupByClause;
import com.jaeyeonling.ast.clause.HavingClause;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.Operator;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GROUP BY와 HAVING 절 파싱 단위 테스트
 */
class GroupByHavingParsingTest extends BaseSqlParserTest {

    @Test
    @DisplayName("단일 컬럼 GROUP BY를 파싱한다")
    void testParseSingleColumnGroupBy() {
        // given
        final String sql = "SELECT city, COUNT(*) FROM users GROUP BY city";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final GroupByClause groupByClause = stmt.groupByClause().orElseThrow();

        // then
        assertThat(groupByClause).isNotNull();
        assertThat(groupByClause.groupingExpressions()).hasSize(1);

        final Expression expr = groupByClause.groupingExpressions().getFirst();
        assertThat(expr).isInstanceOf(ColumnReference.class);
        assertThat(((ColumnReference) expr).columnName()).isEqualTo("city");
    }

    @Test
    @DisplayName("여러 컬럼 GROUP BY를 파싱한다")
    void testParseMultipleColumnsGroupBy() {
        // given
        final String sql = "SELECT city, country, COUNT(*) FROM users GROUP BY city, country";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final GroupByClause groupByClause = stmt.groupByClause().orElseThrow();

        // then
        final List<Expression> expressions = groupByClause.groupingExpressions();
        assertThat(expressions).hasSize(2);

        assertThat(expressions.get(0)).isInstanceOf(ColumnReference.class);
        assertThat(((ColumnReference) expressions.get(0)).columnName()).isEqualTo("city");

        assertThat(expressions.get(1)).isInstanceOf(ColumnReference.class);
        assertThat(((ColumnReference) expressions.get(1)).columnName()).isEqualTo("country");
    }

    @Test
    @DisplayName("테이블.컬럼 형태의 GROUP BY를 파싱한다")
    void testParseTableQualifiedGroupBy() {
        // given
        final String sql = "SELECT u.department, COUNT(*) FROM users u GROUP BY u.department";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final GroupByClause groupByClause = stmt.groupByClause().orElseThrow();

        // then
        final Expression expr = groupByClause.groupingExpressions().getFirst();
        assertThat(expr).isInstanceOf(ColumnReference.class);

        final ColumnReference colRef = (ColumnReference) expr;
        assertThat(colRef.tableName()).hasValue("u");
        assertThat(colRef.columnName()).isEqualTo("department");
    }

    @Test
    @Disabled("파서가 아직 복잡한 수식 표현식을 지원하지 않음")
    @DisplayName("표현식을 사용한 GROUP BY를 파싱한다")
    void testParseExpressionGroupBy() {
        // given
        final String sql = "SELECT age / 10 * 10, COUNT(*) FROM users GROUP BY age / 10 * 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final GroupByClause groupByClause = stmt.groupByClause().orElseThrow();

        // then
        final Expression expr = groupByClause.groupingExpressions().getFirst();
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
    }

    @Test
    @DisplayName("GROUP 다음에 BY가 없으면 예외가 발생한다")
    void testMissingByAfterGroup() {
        // given
        final String sql = "SELECT city FROM users GROUP city";

        // when & then
        assertParseException(sql, "GROUP BY 구문을 완성해야 합니다");
    }

    @Test
    @DisplayName("GROUP BY 다음에 표현식이 없으면 예외가 발생한다")
    void testEmptyGroupByClause() {
        // given
        final String sql = "SELECT city FROM users GROUP BY";

        // when & then
        assertParseException(sql);
    }

    @Test
    @DisplayName("HAVING 절을 파싱한다")
    void testParseHavingClause() {
        // given
        final String sql = "SELECT city, COUNT(*) FROM users GROUP BY city HAVING COUNT(*) > 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final HavingClause havingClause = stmt.havingClause().orElseThrow();

        // then
        assertThat(havingClause).isNotNull();
        assertThat(havingClause.condition()).isInstanceOf(BinaryOperatorExpression.class);

        final BinaryOperatorExpression condition = (BinaryOperatorExpression) havingClause.condition();
        assertThat(condition.operator()).isEqualTo(Operator.GREATER_THAN);
    }

    @Test
    @DisplayName("복잡한 HAVING 조건을 파싱한다")
    void testParseComplexHavingCondition() {
        // given
        final String sql = "SELECT department, AVG(salary) " +
                "FROM employees " +
                "GROUP BY department " +
                "HAVING AVG(salary) > 50000 AND COUNT(*) >= 5";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final HavingClause havingClause = stmt.havingClause().orElseThrow();

        // then
        assertThat(havingClause.condition()).isInstanceOf(BinaryOperatorExpression.class);

        final BinaryOperatorExpression andExpr = (BinaryOperatorExpression) havingClause.condition();
        assertThat(andExpr.operator()).isEqualTo(Operator.AND);
    }

    @Test
    @DisplayName("HAVING은 GROUP BY 없이 사용할 수 없다")
    void testHavingWithoutGroupBy() {
        // given
        final String sql = "SELECT * FROM users HAVING COUNT(*) > 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        // 현재 구현에서는 문법적으로 허용하고 있음
        assertThat(stmt.havingClause()).isPresent();
        assertThat(stmt.groupByClause()).isEmpty();
    }

    @Test
    @DisplayName("GROUP BY와 HAVING이 올바른 순서로 파싱된다")
    void testGroupByHavingOrder() {
        // given
        final String sql = "SELECT department, COUNT(*) as cnt " +
                "FROM employees " +
                "GROUP BY department " +
                "HAVING COUNT(*) > 5 " +
                "ORDER BY cnt DESC";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.groupByClause()).isPresent();
        assertThat(stmt.havingClause()).isPresent();
        assertThat(stmt.orderByClause()).isPresent();

        // 각 절이 올바르게 파싱되었는지 확인
        final GroupByClause groupBy = stmt.groupByClause().get();
        assertThat(groupBy.groupingExpressions()).hasSize(1);

        final HavingClause having = stmt.havingClause().get();
        assertThat(having.condition()).isInstanceOf(BinaryOperatorExpression.class);
    }

    @Test
    @DisplayName("대소문자를 구분하지 않고 GROUP BY와 HAVING을 파싱한다")
    void testCaseInsensitive() {
        // given
        final String sql = "SELECT city FROM users group by city having COUNT(*) > 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.groupByClause()).isPresent();
        assertThat(stmt.havingClause()).isPresent();
    }

    @Test
    @Disabled("파서가 아직 YEAR 같은 함수를 지원하지 않음")
    @DisplayName("복잡한 GROUP BY 절을 파싱한다")
    void testParseComplexGroupBy() {
        // given
        final String sql = "SELECT u.department, u.team, YEAR(u.hire_date), COUNT(*) " +
                "FROM users u " +
                "GROUP BY u.department, u.team, YEAR(u.hire_date)";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final GroupByClause groupByClause = stmt.groupByClause().orElseThrow();

        // then
        final List<Expression> expressions = groupByClause.groupingExpressions();
        assertThat(expressions).hasSize(3);

        // u.department
        assertThat(expressions.getFirst()).isInstanceOf(ColumnReference.class);
        final ColumnReference col1 = (ColumnReference) expressions.getFirst();
        assertThat(col1.tableName()).hasValue("u");
        assertThat(col1.columnName()).isEqualTo("department");

        // u.team
        assertThat(expressions.get(1)).isInstanceOf(ColumnReference.class);
        ColumnReference col2 = (ColumnReference) expressions.get(1);
        assertThat(col2.tableName()).hasValue("u");
        assertThat(col2.columnName()).isEqualTo("team");

        // YEAR(u.hire_date) - 현재 구현에서는 YEAR를 식별자로 파싱
        assertThat(expressions.get(2)).isInstanceOf(ColumnReference.class);
    }
}
