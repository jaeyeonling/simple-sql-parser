package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.expression.BooleanLiteral;
import com.jaeyeonling.ast.expression.DecimalLiteral;
import com.jaeyeonling.ast.expression.ExpressionSelectItem;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.expression.NullLiteral;
import com.jaeyeonling.ast.expression.SelectItem;
import com.jaeyeonling.ast.expression.StringLiteral;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LiteralParsingTest extends BaseSqlParserTest {

    @Test
    @DisplayName("TRUE 리터럴을 파싱한다")
    void testTrueLiteral() {
        // given
        final String sql = "SELECT TRUE";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql);

        // then
        final SelectItem item = stmt.selectClause().selectItems().getFirst();
        assertThat(item).isInstanceOf(ExpressionSelectItem.class);
        final ExpressionSelectItem exprItem = (ExpressionSelectItem) item;
        assertThat(exprItem.expression()).isInstanceOf(BooleanLiteral.class);
        final BooleanLiteral bool = (BooleanLiteral) exprItem.expression();
        assertThat(bool.value()).isTrue();
    }

    @Test
    @DisplayName("FALSE 리터럴을 파싱한다")
    void testFalseLiteral() {
        // given
        final String sql = "SELECT FALSE";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql);

        // then
        final SelectItem item = stmt.selectClause().selectItems().getFirst();
        assertThat(item).isInstanceOf(ExpressionSelectItem.class);
        final ExpressionSelectItem exprItem = (ExpressionSelectItem) item;
        assertThat(exprItem.expression()).isInstanceOf(BooleanLiteral.class);
        final BooleanLiteral bool = (BooleanLiteral) exprItem.expression();
        assertThat(bool.value()).isFalse();
    }

    @Test
    @DisplayName("NULL 리터럴을 파싱한다")
    void testNullLiteral() {
        // given
        final String sql = "SELECT NULL";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql);

        // then
        final SelectItem item = stmt.selectClause().selectItems().getFirst();
        assertThat(item).isInstanceOf(ExpressionSelectItem.class);
        final ExpressionSelectItem exprItem = (ExpressionSelectItem) item;
        assertThat(exprItem.expression()).isInstanceOf(NullLiteral.class);
    }

    @Test
    @DisplayName("WHERE 절에서 불리언 리터럴을 사용한다")
    void testBooleanInWhereClause() {
        // given
        final String sql = "SELECT * FROM users WHERE active = TRUE AND verified = FALSE";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql);

        // then
        final String reconstructed = stmt.accept(visitor);
        assertThat(reconstructed).isEqualTo(sql);
    }

    @Test
    @DisplayName("다양한 리터럴 타입을 함께 사용한다")
    void testMixedLiterals() {
        // given
        final String sql = "SELECT 42, 3.14, 'hello', TRUE, FALSE, NULL FROM users";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql);

        // then
        final List<SelectItem> items = stmt.selectClause().selectItems();
        assertThat(items).hasSize(6);

        // 각 항목이 ExpressionSelectItem으로 감싸져 있고, 그 안에 리터럴이 있음
        assertThat(items.getFirst()).isInstanceOf(ExpressionSelectItem.class);
        assertThat(((ExpressionSelectItem) items.get(0)).expression()).isInstanceOf(IntegerLiteral.class);
        assertThat(((IntegerLiteral) ((ExpressionSelectItem) items.get(0)).expression()).value()).isEqualTo(42);

        assertThat(items.get(1)).isInstanceOf(ExpressionSelectItem.class);
        assertThat(((ExpressionSelectItem) items.get(1)).expression()).isInstanceOf(DecimalLiteral.class);
        assertThat(((DecimalLiteral) ((ExpressionSelectItem) items.get(1)).expression()).value()).isEqualTo(3.14);

        assertThat(items.get(2)).isInstanceOf(ExpressionSelectItem.class);
        assertThat(((ExpressionSelectItem) items.get(2)).expression()).isInstanceOf(StringLiteral.class);
        assertThat(((StringLiteral) ((ExpressionSelectItem) items.get(2)).expression()).value()).isEqualTo("hello");

        assertThat(items.get(3)).isInstanceOf(ExpressionSelectItem.class);
        assertThat(((ExpressionSelectItem) items.get(3)).expression()).isInstanceOf(BooleanLiteral.class);
        assertThat(((BooleanLiteral) ((ExpressionSelectItem) items.get(3)).expression()).value()).isTrue();

        assertThat(items.get(4)).isInstanceOf(ExpressionSelectItem.class);
        assertThat(((ExpressionSelectItem) items.get(4)).expression()).isInstanceOf(BooleanLiteral.class);
        assertThat(((BooleanLiteral) ((ExpressionSelectItem) items.get(4)).expression()).value()).isFalse();

        assertThat(items.get(5)).isInstanceOf(ExpressionSelectItem.class);
        assertThat(((ExpressionSelectItem) items.get(5)).expression()).isInstanceOf(NullLiteral.class);


    }

    @Test
    @DisplayName("IS NULL과 리터럴 NULL을 구분한다")
    void testIsNullVsNullLiteral() {
        // given
        final String sql = "SELECT NULL, name FROM users WHERE status IS NULL";

        // when
        final SelectStatement stmt = parseAndAssertReconstructed(sql);

        // then
        final List<SelectItem> items = stmt.selectClause().selectItems();
        assertThat(items).hasSize(2);

        // SELECT 절의 NULL은 ExpressionSelectItem으로 감싸진 리터럴
        assertThat(items.getFirst()).isInstanceOf(ExpressionSelectItem.class);
        assertThat(((ExpressionSelectItem) items.getFirst()).expression()).isInstanceOf(NullLiteral.class);


    }
}
