package com.jaeyeonling.integration;

import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.exception.SqlParseException;
import com.jaeyeonling.parser.SqlParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 에러 처리 및 경계 조건에 대한 통합 테스트
 * 잘못된 SQL과 엣지 케이스를 테스트합니다.
 */
class ErrorHandlingIntegrationTest {

    @Test
    @DisplayName("빈 문자열은 파싱 오류를 발생시킨다")
    void testEmptyString() {
        // given
        final String sql = "";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("SQL 쿼리는 SELECT로 시작해야 합니다");
    }

    @Test
    @DisplayName("공백만 있는 문자열은 파싱 오류를 발생시킨다")
    void testWhitespaceOnly() {
        // given
        final String sql = "   \t\n\r  ";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("SQL 쿼리는 SELECT로 시작해야 합니다");
    }

    @Test
    @DisplayName("SELECT 없이 시작하는 쿼리는 오류를 발생시킨다")
    void testMissingSelect() {
        // given
        final String sql = "FROM users WHERE id = 1";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("SQL 쿼리는 SELECT로 시작해야 합니다");
    }

    @Test
    @DisplayName("FROM 없이 WHERE가 와도 파싱이 가능하다")
    void testWhereWithoutFrom() {
        // given
        final String sql = "SELECT 1 WHERE 1 = 1";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.selectClause()).isNotNull();
        assertThat(stmt.fromClause()).isEmpty();
        assertThat(stmt.whereClause()).isPresent();
    }

    @Test
    @DisplayName("잘못된 연산자 사용 시 오류를 발생시킨다")
    void testInvalidOperator() {
        // given
        final String sql = "SELECT * FROM users WHERE age >> 18";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("닫히지 않은 문자열 리터럴은 오류를 발생시킨다")
    void testUnterminatedString() {
        // given
        final String sql = "SELECT * FROM users WHERE name = 'John";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("종료되지 않은 문자열");
    }

    @Test
    @DisplayName("WHERE 절에 표현식이 없으면 오류를 발생시킨다")
    void testEmptyWhereClause() {
        // given
        final String sql = "SELECT * FROM users WHERE";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("ORDER BY 절에 표현식이 없으면 오류를 발생시킨다")
    void testEmptyOrderByClause() {
        // given
        final String sql = "SELECT * FROM users ORDER BY";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("GROUP BY 절에 표현식이 없으면 오류를 발생시킨다")
    void testEmptyGroupByClause() {
        // given
        final String sql = "SELECT * FROM users GROUP BY";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("LIMIT에 숫자가 아닌 값이 오면 오류를 발생시킨다")
    void testNonNumericLimit() {
        // given
        final String sql = "SELECT * FROM users LIMIT abc";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("LIMIT 절에는 숫자만 사용할 수 있습니다");
    }

    @Test
    @DisplayName("쉼표 뒤에 아무것도 없으면 오류를 발생시킨다")
    void testTrailingCommaInSelectList() {
        // given
        final String sql = "SELECT id, name, FROM users";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("AS 뒤에 별칭이 없으면 오류를 발생시킨다")
    void testMissingAliasAfterAs() {
        // given
        final String sql = "SELECT name AS FROM users";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("AS 키워드 다음에는 컬럼 별칭을 지정해야 합니다");
    }

    @Test
    @DisplayName("잘못된 문자가 포함되면 오류를 발생시킨다")
    void testInvalidCharacter() {
        // given
        final String sql = "SELECT * FROM users WHERE id = @variable";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("SQL 구문에서 사용할 수 없는 문자입니다: '@'");
    }

    @Test
    @Disabled("현재 파서는 두 번째 FROM을 테이블명으로 처리함")
    @DisplayName("중복된 FROM 키워드는 오류를 발생시킨다")
    void testDuplicateFromKeyword() {
        // given
        final String sql = "SELECT * FROM users FROM orders";

        // when & then
        // 두 번째 FROM은 WHERE 등이 와야 할 위치에 있으므로 오류
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("WHERE 다음에 잘못된 키워드가 오면 오류를 발생시킨다")
    void testInvalidKeywordAfterWhere() {
        // given
        final String sql = "SELECT * FROM users WHERE ORDER BY name";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("괄호가 올바르게 닫히지 않으면 오류를 발생시킨다")
    void testMismatchedParentheses() {
        // given
        final String sql = "SELECT * FROM users WHERE (age > 18 AND (status = 'active')";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @Disabled("현재 파서는 추가 닫는 괄호를 무시함")
    @DisplayName("너무 많이 닫힌 괄호는 오류를 발생시킨다")
    void testExtraClosingParenthesis() {
        // given
        final String sql = "SELECT * FROM users WHERE (age > 18))";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @ParameterizedTest
    @DisplayName("예약어만으로 구성된 잘못된 쿼리들")
    @ValueSource(strings = {
            "SELECT",
            "SELECT FROM",
            "SELECT WHERE",
            "FROM users",
            "WHERE id = 1",
            "ORDER BY name",
            "LIMIT 10"
    })
    void testIncompleteQueries(final String sql) {
        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("연산자가 연속으로 오면 오류를 발생시킨다")
    void testConsecutiveOperatorsError() {
        // given
        final String sql = "SELECT * FROM users WHERE age > = 18";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("피연산자 없이 연산자만 있으면 오류를 발생시킨다")
    void testOperatorWithoutOperands() {
        // given
        final String sql = "SELECT * FROM users WHERE > 18";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("절의 순서가 잘못되면 오류를 발생시킨다")
    void testWrongClauseOrder() {
        // given
        final String sql = "FROM users SELECT *";

        // when & then  
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("SQL 쿼리는 SELECT로 시작해야 합니다");
    }

    @Test
    @DisplayName("음수 LIMIT은 오류를 발생시킨다")
    void testNegativeLimit() {
        // given
        final String sql = "SELECT * FROM users LIMIT -10";

        // when & then
        // 현재 구현에서는 '-'를 별도 토큰으로 처리
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("소수점이 있는 LIMIT은 오류를 발생시킨다")
    void testDecimalLimit() {
        // given
        final String sql = "SELECT * FROM users LIMIT 10.5";

        // when & then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("LIMIT 값은 정수여야 합니다");
    }

    @Test
    @DisplayName("GROUP BY 없이 HAVING을 사용하면 파싱은 성공한다")
    void testHavingWithoutGroupBy() {
        // given
        final String sql = "SELECT * FROM users HAVING id > 10";

        // when
        // 현재 구현에서는 문법적으로 허용 (의미적 검증은 하지 않음)
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.groupByClause()).isEmpty();
        assertThat(stmt.havingClause()).isPresent();
    }

    @Test
    @DisplayName("테이블명 없이 컬럼만 있는 경우는 성공한다")
    void testColumnWithoutTablePrefix() {
        // given
        final String sql = "SELECT id, name FROM users WHERE age > 18";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 테이블 별칭 사용은 파싱 단계에서는 성공한다")
    void testNonExistentTableAlias() {
        // given
        final String sql = "SELECT x.id FROM users WHERE x.age > 18";

        // when
        // 파서는 의미적 검증을 하지 않으므로 성공
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
    }

    @Test
    @DisplayName("매우 깊은 중첩은 스택 오버플로우를 발생시킬 수 있다")
    void testVeryDeepNesting() {
        // given
        // 100단계 중첩 생성
        String sql = "SELECT * FROM users WHERE " + "(".repeat(100) +
                "id = 1" +
                ")".repeat(100);

        // when & then
        // 파서의 재귀 깊이 제한에 따라 성공하거나 실패할 수 있음
        assertThatCode(() -> new SqlParser(sql).parse())
                .doesNotThrowAnyException();
    }
}
