package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.clause.OrderByClause;
import com.jaeyeonling.ast.clause.OrderByItem;
import com.jaeyeonling.ast.clause.OrderDirection;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ORDER BY 절 파싱 단위 테스트
 */
class OrderByClauseParsingTest extends BaseSqlParserTest {

    @Test
    @DisplayName("단일 컬럼으로 정렬을 파싱한다")
    void testParseSingleColumnOrderBy() {
        // given
        final String sql = "SELECT * FROM users ORDER BY name";
        final String expected = "SELECT * FROM users ORDER BY name ASC";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql, expected);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        assertThat(orderByClause).isNotNull();
        assertThat(orderByClause.orderByItems()).hasSize(1);

        final OrderByItem item = orderByClause.orderByItems().getFirst();
        assertThat(item.expression()).isInstanceOf(ColumnReference.class);
        assertThat(((ColumnReference) item.expression()).columnName()).isEqualTo("name");
        assertThat(item.direction()).isEqualTo(OrderDirection.ASC);
    }

    @Test
    @DisplayName("ASC를 명시적으로 지정한 정렬을 파싱한다")
    void testParseOrderByAsc() {
        // given
        final String sql = "SELECT * FROM users ORDER BY name ASC";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        final OrderByItem item = orderByClause.orderByItems().getFirst();
        assertThat(item.direction()).isEqualTo(OrderDirection.ASC);
    }

    @Test
    @DisplayName("DESC 정렬을 파싱한다")
    void testParseOrderByDesc() {
        // given
        final String sql = "SELECT * FROM users ORDER BY age DESC";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        final OrderByItem item = orderByClause.orderByItems().getFirst();
        assertThat(item.direction()).isEqualTo(OrderDirection.DESC);
    }

    @Test
    @DisplayName("여러 컬럼으로 정렬을 파싱한다")
    void testParseMultipleColumnsOrderBy() {
        // given
        final String sql = "SELECT * FROM users ORDER BY age DESC, name ASC, created_at";
        final String expected = "SELECT * FROM users ORDER BY age DESC, name ASC, created_at ASC";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql, expected);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        final List<OrderByItem> items = orderByClause.orderByItems();
        assertThat(items).hasSize(3);

        // age DESC
        assertThat(items.getFirst().expression()).isInstanceOf(ColumnReference.class);
        assertThat(((ColumnReference) items.get(0).expression()).columnName()).isEqualTo("age");
        assertThat(items.getFirst().direction()).isEqualTo(OrderDirection.DESC);

        // name ASC
        assertThat(items.get(1).expression()).isInstanceOf(ColumnReference.class);
        assertThat(((ColumnReference) items.get(1).expression()).columnName()).isEqualTo("name");
        assertThat(items.get(1).direction()).isEqualTo(OrderDirection.ASC);

        // created_at (기본값 ASC)
        assertThat(items.get(2).expression()).isInstanceOf(ColumnReference.class);
        assertThat(((ColumnReference) items.get(2).expression()).columnName()).isEqualTo("created_at");
        assertThat(items.get(2).direction()).isEqualTo(OrderDirection.ASC);
    }

    @Test
    @DisplayName("테이블.컬럼 형태의 정렬을 파싱한다")
    void testParseTableQualifiedOrderBy() {
        // given
        final String sql = "SELECT * FROM users u ORDER BY u.name DESC";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        final OrderByItem item = orderByClause.orderByItems().getFirst();
        assertThat(item.expression()).isInstanceOf(ColumnReference.class);

        final ColumnReference colRef = (ColumnReference) item.expression();
        assertThat(colRef.tableName()).hasValue("u");
        assertThat(colRef.columnName()).isEqualTo("name");
        assertThat(item.direction()).isEqualTo(OrderDirection.DESC);
    }

    @Test
    @Disabled("파서가 아직 수식 표현식을 지원하지 않음")
    @DisplayName("표현식으로 정렬을 파싱한다")
    void testParseExpressionOrderBy() {
        // given
        final String sql = "SELECT * FROM users ORDER BY age + 1 DESC";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        final OrderByItem item = orderByClause.orderByItems().getFirst();
        assertThat(item.expression()).isInstanceOf(BinaryOperatorExpression.class);
        assertThat(item.direction()).isEqualTo(OrderDirection.DESC);
    }

    @Test
    @DisplayName("리터럴로 정렬을 파싱한다")
    void testParseLiteralOrderBy() {
        // given
        final String sql = "SELECT * FROM users ORDER BY 1";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        final OrderByItem item = orderByClause.orderByItems().getFirst();
        assertThat(item.expression()).isInstanceOf(IntegerLiteral.class);
        assertThat(((IntegerLiteral) item.expression()).value()).isEqualTo(1L);
    }

    @ParameterizedTest
    @DisplayName("대소문자를 구분하지 않고 ORDER BY를 파싱한다")
    @CsvSource({
            "'order by', 'asc'",
            "'ORDER BY', 'ASC'",
            "'Order By', 'Asc'",
            "'OrDeR bY', 'AsC'"
    })
    void testParseOrderByCaseInsensitive(
            final String orderByKeyword,
            final String ascKeyword
    ) {
        // given
        final String sql = String.format("SELECT * FROM users %s name %s", orderByKeyword, ascKeyword);
        // 모든 키워드는 대문자로 정규화됨
        final String expected = "SELECT * FROM users ORDER BY name ASC";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql, expected);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        assertThat(orderByClause).isNotNull();
        assertThat(orderByClause.orderByItems()).hasSize(1);
        assertThat(orderByClause.orderByItems().getFirst().direction())
                .isEqualTo(OrderDirection.ASC);
    }

    @Test
    @DisplayName("ORDER 다음에 BY가 없으면 예외가 발생한다")
    void testMissingByAfterOrder() {
        // given
        final String sql = "SELECT * FROM users ORDER name";

        // when & then
        assertParseException(sql, "ORDER BY 구문을 완성해야 합니다");
    }

    @Test
    @DisplayName("ORDER BY 다음에 표현식이 없으면 예외가 발생한다")
    void testEmptyOrderByClause() {
        // given
        final String sql = "SELECT * FROM users ORDER BY";

        // when & then
        assertParseException(sql);
    }

    @Test
    @Disabled("파서가 아직 수식 표현식을 지원하지 않음")
    @DisplayName("복잡한 ORDER BY 절을 파싱한다")
    void testParseComplexOrderBy() {
        // given
        final String sql = "SELECT * FROM users u " +
                "ORDER BY u.department ASC, u.salary DESC, u.name, 1 DESC, age + 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();

        // then
        final List<OrderByItem> items = orderByClause.orderByItems();
        assertThat(items).hasSize(5);

        // u.department ASC
        assertThat(items.getFirst().expression()).isInstanceOf(ColumnReference.class);
        assertThat(items.getFirst().direction()).isEqualTo(OrderDirection.ASC);

        // u.salary DESC
        assertThat(items.get(1).expression()).isInstanceOf(ColumnReference.class);
        assertThat(items.get(1).direction()).isEqualTo(OrderDirection.DESC);

        // u.name (기본값 ASC)
        assertThat(items.get(2).expression()).isInstanceOf(ColumnReference.class);
        assertThat(items.get(2).direction()).isEqualTo(OrderDirection.ASC);

        // 1 DESC
        assertThat(items.get(3).expression()).isInstanceOf(IntegerLiteral.class);
        assertThat(items.get(3).direction()).isEqualTo(OrderDirection.DESC);

        // age + 10 (기본값 ASC)
        assertThat(items.get(4).expression()).isInstanceOf(BinaryOperatorExpression.class);
        assertThat(items.get(4).direction()).isEqualTo(OrderDirection.ASC);
    }
}
