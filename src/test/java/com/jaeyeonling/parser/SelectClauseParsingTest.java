package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.clause.SelectClause;
import com.jaeyeonling.ast.expression.AllColumns;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.ExpressionSelectItem;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.expression.SelectItem;
import com.jaeyeonling.ast.expression.StringLiteral;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SELECT 절 파싱 단위 테스트
 */
class SelectClauseParsingTest extends BaseSqlParserTest {

    @Test
    @DisplayName("SELECT * 를 파싱한다")
    void testParseSelectAll() {
        // given
        final String sql = "SELECT * FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        assertThat(selectClause).isNotNull();
        assertThat(selectClause.isDistinct()).isFalse();
        assertThat(selectClause.selectItems()).hasSize(1);

        final SelectItem item = selectClause.selectItems().getFirst();
        assertThat(item).isInstanceOf(AllColumns.class);
        assertThat(item.alias()).isEmpty();
    }

    @Test
    @DisplayName("단일 컬럼을 파싱한다")
    void testParseSingleColumn() {
        // given
        final String sql = "SELECT name FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        assertThat(selectClause.selectItems()).hasSize(1);

        final SelectItem item = selectClause.selectItems().getFirst();
        assertThat(item).isInstanceOf(ColumnReference.class);
        final ColumnReference colRef = (ColumnReference) item;
        assertThat(colRef.columnName()).isEqualTo("name");
        assertThat(colRef.tableName()).isEmpty();
        assertThat(colRef.alias()).isEmpty();
    }

    @Test
    @DisplayName("여러 컬럼을 파싱한다")
    void testParseMultipleColumns() {
        // given
        final String sql = "SELECT id, name, email FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        final List<SelectItem> items = selectClause.selectItems();
        assertThat(items).hasSize(3);

        assertThat(items).allMatch(item -> item instanceof ColumnReference);

        final List<String> columnNames = items.stream()
                .map(item -> ((ColumnReference) item).columnName())
                .toList();
        assertThat(columnNames).containsExactly("id", "name", "email");
    }

    @Test
    @DisplayName("테이블.컬럼 형태를 파싱한다")
    void testParseTableQualifiedColumn() {
        // given
        final String sql = "SELECT users.id, users.name FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        final List<SelectItem> items = selectClause.selectItems();
        assertThat(items).hasSize(2);

        final ColumnReference col1 = (ColumnReference) items.getFirst();
        assertThat(col1.tableName()).hasValue("users");
        assertThat(col1.columnName()).isEqualTo("id");

        final ColumnReference col2 = (ColumnReference) items.get(1);
        assertThat(col2.tableName()).hasValue("users");
        assertThat(col2.columnName()).isEqualTo("name");
    }

    @Test
    @DisplayName("AS를 사용한 별칭을 파싱한다")
    void testParseColumnWithAsAlias() {
        // given
        final String sql = "SELECT name AS user_name, age AS user_age FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        final List<SelectItem> items = selectClause.selectItems();
        assertThat(items).hasSize(2);

        final ColumnReference col1 = (ColumnReference) items.getFirst();
        assertThat(col1.columnName()).isEqualTo("name");
        assertThat(col1.alias()).hasValue("user_name");

        final ColumnReference col2 = (ColumnReference) items.get(1);
        assertThat(col2.columnName()).isEqualTo("age");
        assertThat(col2.alias()).hasValue("user_age");
    }

    @Test
    @DisplayName("AS 없이 별칭을 파싱한다")
    void testParseColumnWithoutAsAlias() {
        // given
        final String sql = "SELECT name user_name, age user_age FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        final List<SelectItem> items = selectClause.selectItems();
        assertThat(items).hasSize(2);

        final ColumnReference col1 = (ColumnReference) items.getFirst();
        assertThat(col1.columnName()).isEqualTo("name");
        assertThat(col1.alias()).hasValue("user_name");

        final ColumnReference col2 = (ColumnReference) items.get(1);
        assertThat(col2.columnName()).isEqualTo("age");
        assertThat(col2.alias()).hasValue("user_age");
    }

    @Test
    @DisplayName("SELECT DISTINCT를 파싱한다")
    void testParseSelectDistinct() {
        // given
        final String sql = "SELECT DISTINCT city FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        assertThat(selectClause.isDistinct()).isTrue();
        assertThat(selectClause.selectItems()).hasSize(1);
    }

    @Test
    @DisplayName("SELECT DISTINCT 여러 컬럼을 파싱한다")
    void testParseSelectDistinctMultipleColumns() {
        // given
        final String sql = "SELECT DISTINCT city, country FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        assertThat(selectClause.isDistinct()).isTrue();
        assertThat(selectClause.selectItems()).hasSize(2);

        final List<String> columnNames = selectClause.selectItems().stream()
                .map(item -> ((ColumnReference) item).columnName())
                .toList();
        assertThat(columnNames).containsExactly("city", "country");
    }

    @Test
    @Disabled("파서가 아직 수식 표현식을 지원하지 않음")
    @DisplayName("표현식을 SELECT 항목으로 파싱한다")
    void testParseExpressionAsSelectItem() {
        // given
        final String sql = "SELECT age + 1 AS next_age FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        assertThat(selectClause.selectItems()).hasSize(1);

        final SelectItem item = selectClause.selectItems().getFirst();
        assertThat(item).isInstanceOf(ExpressionSelectItem.class);

        final ExpressionSelectItem exprItem = (ExpressionSelectItem) item;
        assertThat(exprItem.alias()).hasValue("next_age");
        assertThat(exprItem.expression()).isInstanceOf(BinaryOperatorExpression.class);
    }

    @Test
    @DisplayName("리터럴을 SELECT 항목으로 파싱한다")
    void testParseLiteralAsSelectItem() {
        // given
        final String sql = "SELECT 'hello' AS greeting, 123 AS number FROM users";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        assertThat(selectClause.selectItems()).hasSize(2);

        // 첫 번째 항목: 문자열 리터럴
        final SelectItem item1 = selectClause.selectItems().getFirst();
        assertThat(item1).isInstanceOf(ExpressionSelectItem.class);
        final ExpressionSelectItem strItem = (ExpressionSelectItem) item1;
        assertThat(strItem.alias()).hasValue("greeting");
        assertThat(strItem.expression()).isInstanceOf(StringLiteral.class);
        assertThat(((StringLiteral) strItem.expression()).value()).isEqualTo("hello");

        // 두 번째 항목: 정수 리터럴
        final SelectItem item2 = selectClause.selectItems().get(1);
        assertThat(item2).isInstanceOf(ExpressionSelectItem.class);
        final ExpressionSelectItem numItem = (ExpressionSelectItem) item2;
        assertThat(numItem.alias()).hasValue("number");
        assertThat(numItem.expression()).isInstanceOf(IntegerLiteral.class);
        assertThat(((IntegerLiteral) numItem.expression()).value()).isEqualTo(123L);
    }

    @Test
    @Disabled("파서가 아직 수식 표현식을 지원하지 않음")
    @DisplayName("복합 SELECT 절을 파싱한다")
    void testParseComplexSelectClause() {
        // given
        final String sql = "SELECT u.id, u.name AS username, u.age + 1 next_year_age, 'active' status, * FROM users u";

        // when
        final SelectStatement stmt = parseAndValidate(sql);
        final SelectClause selectClause = stmt.selectClause();

        // then
        assertThat(selectClause.selectItems()).hasSize(5);

        // 1. u.id
        assertThat(selectClause.selectItems().get(0))
                .isInstanceOf(ColumnReference.class)
                .extracting("tableName", "columnName", "alias")
                .containsExactly(Optional.of("u"), "id", Optional.empty());

        // 2. u.name AS username
        assertThat(selectClause.selectItems().get(1))
                .isInstanceOf(ColumnReference.class)
                .extracting("tableName", "columnName", "alias")
                .containsExactly(Optional.of("u"), "name", Optional.of("username"));

        // 3. u.age + 1 next_year_age
        final SelectItem item3 = selectClause.selectItems().get(2);
        assertThat(item3).isInstanceOf(ExpressionSelectItem.class);
        assertThat(item3.alias()).hasValue("next_year_age");

        // 4. 'active' status
        final SelectItem item4 = selectClause.selectItems().get(3);
        assertThat(item4).isInstanceOf(ExpressionSelectItem.class);
        assertThat(item4.alias()).hasValue("status");

        // 5. *
        assertThat(selectClause.selectItems().get(4)).isInstanceOf(AllColumns.class);
    }

    @ParameterizedTest
    @DisplayName("대소문자를 구분하지 않고 SELECT를 파싱한다")
    @ValueSource(strings = {"select", "SELECT", "Select", "SeLeCt"})
    void testParseSelectCaseInsensitive(final String selectKeyword) {
        // given
        final String sql = selectKeyword + " id FROM users";

        // when & then
        assertParseSuccess(sql);

        final SelectStatement stmt = parseAndValidate(sql);
        assertThat(stmt.selectClause()).isNotNull();
        assertThat(stmt.selectClause().selectItems()).hasSize(1);
    }

    @Test
    @DisplayName("SELECT 절이 없으면 예외가 발생한다")
    void testMissingSelectClause() {
        // given
        final String sql = "FROM users";

        // when & then
        assertParseException(sql, "SQL 쿼리는 SELECT로 시작해야 합니다");
    }

    @Test
    @DisplayName("SELECT 다음에 항목이 없으면 예외가 발생한다")
    void testEmptySelectList() {
        // given
        final String sql = "SELECT FROM users";

        // when & then
        assertParseException(sql);
    }
}
