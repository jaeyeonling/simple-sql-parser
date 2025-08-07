package com.jaeyeonling.parser;

import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.exception.SqlParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * SqlParser의 새로운 사용법 테스트
 */
@DisplayName("SqlParser 새로운 사용법 테스트")
class SqlParserNewUsageTest {

    @Test
    @DisplayName("new SqlParser(sql).parse() 형태로 SQL을 파싱한다")
    void testNewInstanceBasedParsing() {
        // given
        final String sql = "SELECT id, name FROM users WHERE age > 18";

        // when
        final SqlParser parser = new SqlParser(sql);
        final SelectStatement stmt = parser.parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause()).isNotNull();
        assertThat(stmt.fromClause()).isPresent();
        assertThat(stmt.whereClause()).isPresent();
    }

    @Test
    @DisplayName("정적 메서드도 여전히 동작한다 (기존 호환성)")
    void testStaticMethodStillWorks() {
        // given
        final String sql = "SELECT * FROM products";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause()).isNotNull();
        assertThat(stmt.fromClause()).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM users",
            "SELECT id FROM products WHERE price > 100",
            "SELECT status FROM orders GROUP BY status",
            "SELECT DISTINCT city FROM users ORDER BY city",
            "SELECT * FROM items LIMIT 10 OFFSET 20"
    })
    @DisplayName("다양한 SQL을 인스턴스 방식으로 파싱한다")
    void testVariousQueriesWithNewUsage(final String sql) {
        // when
        final SqlParser parser = new SqlParser(sql);
        final SelectStatement stmt = parser.parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause()).isNotNull();
        assertThat(stmt.fromClause()).isPresent();
    }

    @Test
    @DisplayName("같은 파서 인스턴스를 재사용할 수 있다")
    void testReuseParserInstance() {
        // given
        final String sql = "SELECT * FROM users";
        final SqlParser parser = new SqlParser(sql);

        // when & then
        final SelectStatement stmt1 = parser.parse();
        final SelectStatement stmt2 = parser.parse();

        assertThat(stmt1).isNotNull();
        assertThat(stmt2).isNotNull();
        // 매번 새로운 파싱 결과를 반환
        assertThat(stmt1).isNotSameAs(stmt2);
    }

    @Test
    @DisplayName("잘못된 SQL은 SqlParseException을 발생시킨다")
    void testInvalidSqlThrowsException() {
        // given
        final String invalidSql = "SELECT FROM";  // SELECT 다음에 컬럼이 없음
        final SqlParser parser = new SqlParser(invalidSql);

        // when & then
        assertThatThrownBy(parser::parse)
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("표현식");  // 표현식 파싱 오류
    }

    @Test
    @DisplayName("빈 SQL은 SqlParseException을 발생시킨다")
    void testEmptySqlThrowsException() {
        // given
        final String emptySql = "";
        final SqlParser parser = new SqlParser(emptySql);

        // when & then
        assertThatThrownBy(parser::parse)
                .isInstanceOf(SqlParseException.class);
    }

    @Test
    @DisplayName("여러 SQL을 순차적으로 파싱할 수 있다")
    void testMultipleSqlParsing() {
        // given
        final List<String> queries = List.of(
                "SELECT * FROM users",
                "SELECT id, name FROM products WHERE price > 100",
                "SELECT status FROM orders GROUP BY status HAVING status > 5"
        );

        // when & then
        for (final String sql : queries) {
            SqlParser parser = new SqlParser(sql);
            SelectStatement stmt = assertDoesNotThrow(parser::parse);
            assertThat(stmt).isNotNull();
            assertThat(stmt.selectClause()).isNotNull();
        }
    }

    @Test
    @DisplayName("복잡한 쿼리도 파싱할 수 있다")
    void testComplexQuery() {
        // given
        final String complexSql = """
                SELECT u.id, u.name, u.email AS user_email
                FROM users u
                WHERE u.age > 18 AND u.status = 'active'
                GROUP BY u.id, u.name
                HAVING u.id > 5
                ORDER BY u.name DESC
                LIMIT 10
                """.trim();

        // when
        final SqlParser parser = new SqlParser(complexSql);
        final SelectStatement stmt = parser.parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause().selectItems()).hasSize(3);
        assertThat(stmt.whereClause()).isPresent();
        assertThat(stmt.groupByClause()).isPresent();
        assertThat(stmt.havingClause()).isPresent();
        assertThat(stmt.orderByClause()).isPresent();
        assertThat(stmt.limitClause()).isPresent();
    }
}
