package com.jaeyeonling.integration;

import com.jaeyeonling.ast.clause.LimitClause;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.ast.table.Table;
import com.jaeyeonling.ast.table.TableReference;
import com.jaeyeonling.parser.SqlParser;
import com.jaeyeonling.visitor.SqlToStringVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * 기본적인 SQL 쿼리 파싱 및 재구성 테스트
 */
@DisplayName("기본 SQL 쿼리 통합 테스트")
class BasicQueryIntegrationTest {

    private final SqlToStringVisitor sqlToStringVisitor = new SqlToStringVisitor();

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM users",
            "SELECT id, name FROM users WHERE age > 18",
            "SELECT DISTINCT city FROM users ORDER BY city ASC",
            "SELECT u.id, u.name, o.total FROM users u",
            "SELECT department FROM employees GROUP BY department",
            "SELECT name FROM users WHERE name LIKE 'John' AND age > 21",
            "SELECT * FROM products LIMIT 10 OFFSET 20"
    })
    @DisplayName("다양한 기본 쿼리가 성공적으로 파싱되고 재구성된다")
    void testBasicQueries(final String sql) {
        // when
        final SelectStatement stmt = assertDoesNotThrow(() -> new SqlParser(sql).parse(),
                "SQL 파싱에 실패했습니다: " + sql);

        // then
        assertThat(stmt).isNotNull();

        // SQL 재구성 테스트
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isNotNull();
        assertThat(reconstructed).isNotEmpty();

        // 재구성된 SQL도 파싱 가능한지 확인
        final SelectStatement reparsed = assertDoesNotThrow(() -> new SqlParser(reconstructed).parse(),
                "재구성된 SQL 파싱에 실패했습니다: " + reconstructed);
        assertThat(reparsed).isNotNull();
    }

    @Test
    @DisplayName("SELECT * FROM users - 가장 기본적인 쿼리")
    void testSimpleSelectAll() {
        // given
        final String sql = "SELECT * FROM users";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.selectClause().selectItems()).hasSize(1);
        assertThat(stmt.selectClause().selectItems().getFirst())
                .isInstanceOf(com.jaeyeonling.ast.expression.AllColumns.class);
        assertThat(stmt.fromClause().orElseThrow().tableReferences()).hasSize(1);

        // 재구성
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isEqualTo(sql);
    }

    @Test
    @DisplayName("SELECT with WHERE - 조건절이 있는 쿼리")
    void testSelectWithWhere() {
        // given
        final String sql = "SELECT id, name FROM users WHERE age > 18";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.selectClause().selectItems()).hasSize(2);
        assertThat(stmt.whereClause()).isPresent();

        // 재구성
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isEqualTo(sql);
    }

    @Test
    @DisplayName("SELECT DISTINCT with ORDER BY - 중복 제거와 정렬")
    void testSelectDistinctWithOrderBy() {
        // given
        final String sql = "SELECT DISTINCT city FROM users ORDER BY city ASC";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.selectClause().isDistinct()).isTrue();
        assertThat(stmt.orderByClause()).isPresent();
        assertThat(stmt.orderByClause().get().orderByItems()).hasSize(1);

        // 재구성
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isEqualTo(sql);
    }

    @Test
    @DisplayName("SELECT with alias - 테이블 별칭 사용")
    void testSelectWithTableAlias() {
        // given
        final String sql = "SELECT u.id, u.name, o.total FROM users u";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.selectClause().selectItems()).hasSize(3);
        assertThat(stmt.fromClause().orElseThrow().tableReferences()).hasSize(1);

        final TableReference tableRef = stmt.fromClause().orElseThrow().tableReferences().getFirst();
        assertThat(tableRef).isInstanceOf(Table.class);
        final Table table = (Table) tableRef;
        assertThat(table.alias()).isPresent();
        assertThat(table.alias().get()).isEqualTo("u");

        // 재구성
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isEqualTo(sql);
    }

    @Test
    @DisplayName("SELECT with GROUP BY - 그룹화")
    void testSelectWithGroupBy() {
        // given
        final String sql = "SELECT department FROM employees GROUP BY department";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.groupByClause()).isPresent();
        assertThat(stmt.groupByClause().get().groupingExpressions()).hasSize(1);

        // 재구성
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isEqualTo(sql);
    }

    @Test
    @DisplayName("SELECT with complex WHERE - 복합 조건")
    void testSelectWithComplexWhere() {
        // given
        final String sql = "SELECT name FROM users WHERE name LIKE 'John' AND age > 21";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.whereClause()).isPresent();
        final Expression whereExpr = stmt.whereClause().get().condition();
        assertThat(whereExpr).isInstanceOf(BinaryOperatorExpression.class);

        // 재구성
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isEqualTo(sql);
    }

    @Test
    @DisplayName("SELECT with LIMIT and OFFSET - 페이징")
    void testSelectWithLimitOffset() {
        // given
        final String sql = "SELECT * FROM products LIMIT 10 OFFSET 20";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.limitClause()).isPresent();
        final LimitClause limitClause = stmt.limitClause().get();
        assertThat(limitClause.limit()).isEqualTo(10);
        assertThat(limitClause.offset()).isPresent();
        assertThat(limitClause.offset().get()).isEqualTo(20);

        // 재구성
        final String reconstructed = stmt.accept(sqlToStringVisitor);
        assertThat(reconstructed).isEqualTo(sql);
    }
}
