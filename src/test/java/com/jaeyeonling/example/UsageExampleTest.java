package com.jaeyeonling.example;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.parser.SqlParser;
import com.jaeyeonling.visitor.SqlToStringVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple SQL Parser 사용 예제 테스트
 */
class UsageExampleTest extends BaseSqlParserTest {

    @Test
    @DisplayName("기본 SQL 파싱 예제")
    void basicUsageExample() {
        // given
        final String sql = "SELECT id, name FROM users WHERE age > 18 ORDER BY name";

        // when
        final String expected = "SELECT id, name FROM users WHERE age > 18 ORDER BY name ASC";
        final SelectStatement stmt = parseAndAssertReconstructed(sql, expected);

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause().selectItems()).hasSize(2);
        assertThat(stmt.whereClause()).isPresent();
        assertThat(stmt.orderByClause()).isPresent();
    }

    @Test
    @DisplayName("다양한 SQL 문법 예제")
    void variousQueryExamples() {
        // 간단한 SELECT
        assertValidQuery("SELECT * FROM users");

        // WHERE 조건
        assertValidQuery("SELECT name, age FROM users WHERE age >= 18");

        // ORDER BY
        assertValidQuery("SELECT * FROM products ORDER BY price DESC");

        // GROUP BY와 HAVING (집계 함수 없이)
        assertValidQuery("SELECT category FROM products GROUP BY category HAVING category > 5");

        // LIMIT과 OFFSET
        assertValidQuery("SELECT * FROM users LIMIT 10 OFFSET 20");

        // 복잡한 조건
        assertValidQuery("SELECT * FROM users WHERE age > 18 AND status = 'active' OR role = 'admin'");

        // LIKE 연산자
        assertValidQuery("SELECT * FROM users WHERE name LIKE 'John%'");

        // IN 연산자
        assertValidQuery("SELECT * FROM products WHERE category IN ('electronics', 'books')");

        // BETWEEN 연산자
        assertValidQuery("SELECT * FROM orders WHERE price BETWEEN 100 AND 500");

        // IS NULL
        assertValidQuery("SELECT * FROM users WHERE deleted_at IS NULL");

        // 산술 연산
        assertValidQuery("SELECT price * quantity AS total FROM orders");

        // 리터럴 값
        assertValidQuery("SELECT TRUE, FALSE, NULL, 42, 3.14, 'hello'");
    }

    private void assertValidQuery(final String sql) {
        final SelectStatement stmt = new SqlParser(sql).parse();
        assertThat(stmt).isNotNull();

        // 파싱 후 재구성한 SQL이 원본과 유사한지 확인
        // (ORDER BY의 경우 ASC가 추가될 수 있음)
        final String reconstructed = stmt.accept(new SqlToStringVisitor());

        // ORDER BY가 있는 경우 ASC가 추가될 수 있음
        if (sql.contains("ORDER BY") && !sql.contains("DESC")) {
            final String expectedWithAsc = sql.replaceAll("ORDER BY (\\w+)(?!\\s+(ASC|DESC))", "ORDER BY $1 ASC");
            assertThat(reconstructed).isIn(sql, expectedWithAsc);
        } else {
            assertThat(reconstructed).isEqualTo(sql);
        }
    }
}
