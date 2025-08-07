package com.jaeyeonling.parser;

import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.exception.SqlParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * SqlParser 테스트
 */
class SqlParserTest {

    @Test
    void testParseSimpleSelect() {
        // given
        final String sql = "SELECT id, name FROM users";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause()).isNotNull();
        assertThat(stmt.fromClause()).isPresent();
        assertThat(stmt.whereClause()).isEmpty();
    }

    @Test
    void testParseSelectWithWhere() {
        // given
        final String sql = "SELECT * FROM users WHERE age > 18";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.whereClause()).isPresent();
    }

    @Test
    void testParseSelectWithAlias() {
        // given
        final String sql = "SELECT u.id, u.name AS username FROM users u";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause().selectItems()).hasSize(2);
    }

    @Test
    void testParseInvalidSql() {
        // given
        final String sql = "SELECT FROM";  // 잘못된 SQL

        // then
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("표현식을 파싱할 수 없습니다");
    }

    @Test
    void testParseSelectDistinct() {
        // given
        final String sql = "SELECT DISTINCT city FROM users";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.selectClause().isDistinct()).isTrue();
    }

    @Test
    void testParseSelectWithOrderBy() {
        // given
        final String sql = "SELECT * FROM users ORDER BY age DESC";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.orderByClause()).isPresent();
    }

    @Test
    void testParseSelectWithLimit() {
        // given
        final String sql = "SELECT * FROM users LIMIT 10";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.limitClause()).isPresent();
        assertThat(stmt.limitClause().get().limit()).isEqualTo(10);
    }
}
