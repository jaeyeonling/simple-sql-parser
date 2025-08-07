package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.clause.FromClause;
import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.ast.table.Table;
import com.jaeyeonling.ast.table.TableReference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FROM 절 파싱 단위 테스트
 */
class FromClauseParsingTest extends BaseSqlParserTest {

    @Test
    @DisplayName("단일 테이블을 파싱한다")
    void testParseSingleTable() {
        // given
        final String sql = "SELECT * FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElse(null);

        // then
        assertThat(fromClause).isNotNull();
        assertThat(fromClause.tableReferences()).hasSize(1);

        final TableReference tableRef = fromClause.tableReferences().getFirst();
        assertThat(tableRef).isInstanceOf(Table.class);

        final Table table = (Table) tableRef;
        assertThat(table.name()).isEqualTo("users");
        assertThat(table.alias()).isEmpty();
    }

    @Test
    @DisplayName("AS를 사용한 테이블 별칭을 파싱한다")
    void testParseTableWithAsAlias() {
        // given
        final String sql = "SELECT * FROM users AS u";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final TableReference tableRef = fromClause.tableReferences().getFirst();
        assertThat(tableRef).isInstanceOf(Table.class);

        final Table table = (Table) tableRef;
        assertThat(table.name()).isEqualTo("users");
        assertThat(table.alias()).hasValue("u");
    }

    @Test
    @DisplayName("AS 없이 테이블 별칭을 파싱한다")
    void testParseTableWithoutAsAlias() {
        // given
        final String sql = "SELECT * FROM users u";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final TableReference tableRef = fromClause.tableReferences().getFirst();
        assertThat(tableRef).isInstanceOf(Table.class);

        final Table table = (Table) tableRef;
        assertThat(table.name()).isEqualTo("users");
        assertThat(table.alias()).hasValue("u");
    }

    @Test
    @DisplayName("여러 테이블을 쉼표로 구분하여 파싱한다")
    void testParseMultipleTables() {
        // given
        final String sql = "SELECT * FROM users, orders, products";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final List<TableReference> tables = fromClause.tableReferences();
        assertThat(tables).hasSize(3);

        assertThat(tables).allMatch(ref -> ref instanceof Table);

        final List<String> tableNames = tables.stream()
                .map(ref -> ((Table) ref).name())
                .toList();
        assertThat(tableNames).containsExactly("users", "orders", "products");
    }

    @Test
    @DisplayName("여러 테이블과 별칭을 함께 파싱한다")
    void testParseMultipleTablesWithAliases() {
        // given
        final String sql = "SELECT * FROM users u, orders AS o, products p";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final List<TableReference> tables = fromClause.tableReferences();
        assertThat(tables).hasSize(3);

        // users u
        final Table table1 = (Table) tables.getFirst();
        assertThat(table1.name()).isEqualTo("users");
        assertThat(table1.alias()).hasValue("u");

        // orders AS o
        final Table table2 = (Table) tables.get(1);
        assertThat(table2.name()).isEqualTo("orders");
        assertThat(table2.alias()).hasValue("o");

        // products p
        final Table table3 = (Table) tables.get(2);
        assertThat(table3.name()).isEqualTo("products");
        assertThat(table3.alias()).hasValue("p");
    }

    @Test
    @Disabled("스키마.테이블 문법(점 문법)은 아직 지원하지 않음")
    @DisplayName("스키마.테이블 형태를 파싱한다")
    void testParseSchemaQualifiedTable() {
        // given
        final String sql = "SELECT * FROM myschema.users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final TableReference tableRef = fromClause.tableReferences().getFirst();
        assertThat(tableRef).isInstanceOf(Table.class);

        final Table table = (Table) tableRef;
        // 현재 구현에서는 스키마.테이블을 하나의 이름으로 처리
        assertThat(table.name()).isEqualTo("myschema");
        assertThat(table.alias()).isEmpty();
    }

    @Test
    @DisplayName("대소문자를 구분하지 않고 FROM을 파싱한다")
    void testParseFromCaseInsensitive() {
        // given
        final String sql = "SELECT * from users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElse(null);

        // then
        assertThat(fromClause).isNotNull();
        assertThat(fromClause.tableReferences()).hasSize(1);
    }

    @Test
    @DisplayName("FROM 절이 없어도 쿼리가 파싱된다")
    void testMissingFromClause() {
        // given
        final String sql = "SELECT 1";

        // when
        final SelectStatement stmt = parseAndValidate(sql);

        // then
        assertThat(stmt.selectClause()).isNotNull();
        assertThat(stmt.fromClause()).isEmpty();
    }

    @Test
    @DisplayName("FROM 다음에 테이블이 없으면 예외가 발생한다")
    void testEmptyFromClause() {
        // given
        final String sql = "SELECT * FROM";

        // when & then
        assertParseException(sql, "FROM 절 다음에 테이블 이름을 지정해야 합니다");
    }

    @Test
    @DisplayName("잘못된 테이블 별칭 형식은 예외가 발생한다")
    void testInvalidTableAlias() {
        // given
        final String sql = "SELECT * FROM users AS";

        // when & then
        assertParseException(sql, "AS 키워드 다음에는 테이블 별칭을 지정해야 합니다");
    }

    @Test
    @DisplayName("특수 문자가 포함된 테이블명을 파싱한다")
    void testParseTableNameWithUnderscore() {
        // given
        final String sql = "SELECT * FROM user_accounts";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final Table table = (Table) fromClause.tableReferences().getFirst();
        assertThat(table.name()).isEqualTo("user_accounts");
    }

    @Test
    @DisplayName("숫자가 포함된 테이블명을 파싱한다")
    void testParseTableNameWithNumbers() {
        // given
        final String sql = "SELECT * FROM table123";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final Table table = (Table) fromClause.tableReferences().getFirst();
        assertThat(table.name()).isEqualTo("table123");
    }

    @Test
    @DisplayName("복잡한 FROM 절을 파싱한다")
    void testParseComplexFromClause() {
        // given
        final String sql = "SELECT * FROM users u, orders o, order_items oi, products AS p";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final FromClause fromClause = stmt.fromClause().orElseThrow();

        // then
        final List<TableReference> tables = fromClause.tableReferences();
        assertThat(tables).hasSize(4);

        // 각 테이블 검증
        assertThat(tables.get(0))
                .isInstanceOf(Table.class)
                .extracting("name", "alias")
                .containsExactly("users", "u");

        assertThat(tables.get(1))
                .isInstanceOf(Table.class)
                .extracting("name", "alias")
                .containsExactly("orders", "o");

        assertThat(tables.get(2))
                .isInstanceOf(Table.class)
                .extracting("name", "alias")
                .containsExactly("order_items", "oi");

        assertThat(tables.get(3))
                .isInstanceOf(Table.class)
                .extracting("name", "alias")
                .containsExactly("products", "p");
    }
}
