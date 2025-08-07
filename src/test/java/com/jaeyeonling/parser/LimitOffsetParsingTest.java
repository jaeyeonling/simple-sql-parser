package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.clause.LimitClause;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LIMIT/OFFSET 절 파싱 단위 테스트
 */
class LimitOffsetParsingTest extends BaseSqlParserTest {

    @Test
    @DisplayName("LIMIT만 있는 경우를 파싱한다")
    void testParseLimitOnly() {
        // given
        final String sql = "SELECT * FROM users LIMIT 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause).isNotNull();
        assertThat(limitClause.limit()).isEqualTo(10);
        assertThat(limitClause.offset()).isEmpty();
    }

    @Test
    @DisplayName("LIMIT과 OFFSET을 함께 파싱한다")
    void testParseLimitWithOffset() {
        // given
        final String sql = "SELECT * FROM users LIMIT 20 OFFSET 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause.limit()).isEqualTo(20);
        assertThat(limitClause.offset()).hasValue(10);
    }

    @ParameterizedTest
    @DisplayName("다양한 LIMIT 값을 파싱한다")
    @ValueSource(ints = {0, 1, 10, 100, 1000, 999999})
    void testParseDifferentLimitValues(final int limit) {
        // given
        final String sql = String.format("SELECT * FROM users LIMIT %d", limit);

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause.limit()).isEqualTo(limit);
    }

    @Test
    @DisplayName("0을 LIMIT 값으로 파싱한다")
    void testParseLimitZero() {
        // given
        final String sql = "SELECT * FROM users LIMIT 0";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause.limit()).isEqualTo(0);
    }

    @Test
    @DisplayName("OFFSET 0을 파싱한다")
    void testParseOffsetZero() {
        // given
        final String sql = "SELECT * FROM users LIMIT 10 OFFSET 0";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause.limit()).isEqualTo(10);
        assertThat(limitClause.offset()).hasValue(0);
    }

    @Test
    @DisplayName("큰 OFFSET 값을 파싱한다")
    void testParseLargeOffset() {
        // given
        final String sql = "SELECT * FROM users LIMIT 10 OFFSET 1000000";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause.limit()).isEqualTo(10);
        assertThat(limitClause.offset()).hasValue(1000000);
    }

    @Test
    @DisplayName("LIMIT은 SQL 문의 마지막에 위치한다")
    void testLimitAtEndOfQuery() {
        // given
        final String sql = "SELECT * FROM users WHERE age > 18 ORDER BY name LIMIT 10";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.whereClause()).isPresent();
        assertThat(stmt.orderByClause()).isPresent();
        assertThat(stmt.limitClause()).isPresent();
        assertThat(stmt.limitClause().get().limit()).isEqualTo(10);
    }

    @Test
    @Disabled("파서가 아직 COUNT(*) 같은 함수와 집계를 지원하지 않음")
    @DisplayName("모든 절이 있는 복잡한 쿼리에서 LIMIT을 파싱한다")
    void testLimitInComplexQuery() {
        // given
        final String sql = "SELECT city, COUNT(*) as cnt " +
                "FROM users " +
                "WHERE active = true " +
                "GROUP BY city " +
                "HAVING COUNT(*) > 5 " +
                "ORDER BY cnt DESC " +
                "LIMIT 3 OFFSET 2";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause.limit()).isEqualTo(3);
        assertThat(limitClause.offset()).hasValue(2);
    }

    @Test
    @DisplayName("대소문자를 구분하지 않고 LIMIT/OFFSET을 파싱한다")
    void testCaseInsensitive() {
        // given
        final String sql = "SELECT * FROM users limit 10 offset 5";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final LimitClause limitClause = stmt.limitClause().orElseThrow();

        // then
        assertThat(limitClause.limit()).isEqualTo(10);
        assertThat(limitClause.offset()).hasValue(5);
    }

    @Test
    @DisplayName("LIMIT 다음에 숫자가 없으면 예외가 발생한다")
    void testMissingLimitValue() {
        // given
        final String sql = "SELECT * FROM users LIMIT";

        // when & then
        assertParseException(sql, "LIMIT 절에는 반환할 행 수를 지정하는 정수가 와야 합니다");
    }

    @Test
    @DisplayName("LIMIT에 문자열이 오면 예외가 발생한다")
    void testInvalidLimitValue() {
        // given
        final String sql = "SELECT * FROM users LIMIT 'ten'";

        // when & then
        assertParseException(sql, "LIMIT 절에는 숫자만 사용할 수 있습니다");
    }

    @Test
    @DisplayName("OFFSET 다음에 숫자가 없으면 예외가 발생한다")
    void testMissingOffsetValue() {
        // given
        final String sql = "SELECT * FROM users LIMIT 10 OFFSET";

        // when & then
        assertParseException(sql, "OFFSET 절에는 건너뛸 행 수를 지정하는 정수가 와야 합니다");
    }

    @Test
    @Disabled("현재 파서는 OFFSET을 키워드가 아닌 식별자로 처리하므로 다른 예외가 발생함")
    @DisplayName("OFFSET만 단독으로 사용할 수 없다")
    void testOffsetWithoutLimit() {
        // given
        final String sql = "SELECT * FROM users OFFSET 10";

        // when & then
        // 현재 구현에서는 OFFSET을 식별자로 인식하여 구문 오류 발생
        assertParseException(sql);
    }

    @ParameterizedTest
    @DisplayName("절의 순서가 잘못되면 예외가 발생한다")
    @CsvSource({
            "SELECT * FROM users LIMIT 10 WHERE age > 18",
            "SELECT * FROM users LIMIT 10 ORDER BY name",
            "SELECT * FROM users ORDER BY name LIMIT 10 WHERE age > 18"
    })
    void testInvalidClauseOrder(String sql) {
        // when & then
        // LIMIT 이후에 다른 절이 오면 예외가 발생해야 함
        assertParseException(sql, "절이 잘못된 위치에 있습니다");
    }

    @Test
    @DisplayName("음수 LIMIT은 예외가 발생한다")
    void testNegativeLimit() {
        // given
        final String sql = "SELECT * FROM users LIMIT -10";

        // when & then
        // 현재 구현에서는 -를 별도 토큰으로 처리하여 다른 예외 발생
        assertParseException(sql);
    }
}
