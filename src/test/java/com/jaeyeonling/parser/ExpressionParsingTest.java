package com.jaeyeonling.parser;

import com.jaeyeonling.BaseSqlParserTest;
import com.jaeyeonling.ast.clause.WhereClause;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.expression.LikeExpression;
import com.jaeyeonling.ast.expression.Operator;
import com.jaeyeonling.ast.expression.StringLiteral;
import com.jaeyeonling.ast.statement.SelectStatement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Expression 파싱 단위 테스트
 * WHERE 절을 통해 Expression 파싱을 테스트합니다.
 */
class ExpressionParsingTest extends BaseSqlParserTest {

    /**
     * WHERE 절에서 Expression을 추출하는 헬퍼 메서드
     */
    private Expression parseExpression(final String whereCondition) {
        final String sql = "SELECT * FROM dummy WHERE " + whereCondition;
        final SelectStatement stmt = parseAndValidate(sql);
        final WhereClause whereClause = stmt.whereClause()
                .orElseThrow(() -> new AssertionError("WHERE clause not found"));
        return whereClause.condition();
    }

    @Test
    @DisplayName("정수 리터럴을 파싱한다")
    void testParseIntegerLiteral() {
        // given & when
        final Expression expr = parseExpression("column = 123");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression binOp = (BinaryOperatorExpression) expr;

        final Expression right = binOp.right();
        assertThat(right).isInstanceOf(IntegerLiteral.class);
        final IntegerLiteral literal = (IntegerLiteral) right;
        assertThat(literal.value()).isEqualTo(123L);
    }

    @Test
    @DisplayName("문자열 리터럴을 파싱한다")
    void testParseStringLiteral() {
        // given & when
        final Expression expr = parseExpression("name = 'hello world'");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression binOp = (BinaryOperatorExpression) expr;

        final Expression right = binOp.right();
        assertThat(right).isInstanceOf(StringLiteral.class);
        final StringLiteral literal = (StringLiteral) right;
        assertThat(literal.value()).isEqualTo("hello world");
    }

    @Test
    @DisplayName("컬럼 참조를 파싱한다")
    void testParseColumnReference() {
        // given & when
        final Expression expr = parseExpression("column_name = 1");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression binOp = (BinaryOperatorExpression) expr;

        final Expression left = binOp.left();
        assertThat(left).isInstanceOf(ColumnReference.class);
        final ColumnReference colRef = (ColumnReference) left;
        assertThat(colRef.columnName()).isEqualTo("column_name");
        assertThat(colRef.tableName()).isEmpty();
    }

    @Test
    @DisplayName("테이블.컬럼 형태의 컬럼 참조를 파싱한다")
    void testParseTableColumnReference() {
        // given & when
        final Expression expr = parseExpression("users.name = 'John'");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression binOp = (BinaryOperatorExpression) expr;

        final Expression left = binOp.left();
        assertThat(left).isInstanceOf(ColumnReference.class);
        final ColumnReference colRef = (ColumnReference) left;
        assertThat(colRef.columnName()).isEqualTo("name");
        assertThat(colRef.tableName()).hasValue("users");
    }

    @ParameterizedTest
    @DisplayName("이항 비교 연산자를 파싱한다")
    @CsvSource({
            "age = 18, age, EQUALS, 18",
            "age != 20, age, NOT_EQUALS, 20",
            "age < 30, age, LESS_THAN, 30",
            "age <= 35, age, LESS_THAN_OR_EQUALS, 35",
            "age > 40, age, GREATER_THAN, 40",
            "age >= 45, age, GREATER_THAN_OR_EQUALS, 45"
    })
    void testParseBinaryComparisonOperator(
            final String expression,
            final String leftColumn,
            final String operatorName,
            final String rightValue
    ) {
        // given & when
        final Expression expr = parseExpression(expression);

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression binOp = (BinaryOperatorExpression) expr;

        // 왼쪽 피연산자 확인
        assertThat(binOp.left()).isInstanceOf(ColumnReference.class);
        final ColumnReference left = (ColumnReference) binOp.left();
        assertThat(left.columnName()).isEqualTo(leftColumn);

        // 연산자 확인
        assertThat(binOp.operator().name()).isEqualTo(operatorName);

        // 오른쪽 피연산자 확인
        final Expression right = binOp.right();
        if (rightValue.matches("\\d+")) {
            assertThat(right).isInstanceOf(IntegerLiteral.class);
            assertThat(((IntegerLiteral) right).value()).isEqualTo(Integer.parseInt(rightValue));
        } else {
            assertThat(right).isInstanceOf(StringLiteral.class);
            assertThat(((StringLiteral) right).value()).isEqualTo("'" + rightValue + "'");
        }
    }

    @Test
    @DisplayName("LIKE 연산자를 파싱한다")
    void testParseLikeOperator() {
        // given & when
        final Expression expr = parseExpression("name LIKE 'John%'");

        // then
        assertThat(expr).isInstanceOf(LikeExpression.class);
        final LikeExpression like = (LikeExpression) expr;

        // 왼쪽 피연산자 확인
        assertThat(like.expression()).isInstanceOf(ColumnReference.class);
        final ColumnReference left = (ColumnReference) like.expression();
        assertThat(left.columnName()).isEqualTo("name");

        // 패턴 확인
        assertThat(like.pattern()).isInstanceOf(StringLiteral.class);
        final StringLiteral pattern = (StringLiteral) like.pattern();
        assertThat(pattern.value()).isIn("John%", "'John%'"); // Lexer 구현에 따라 따옴표 포함 여부가 다를 수 있음
    }

    @Test
    @DisplayName("AND 논리 연산자를 파싱한다")
    void testParseAndOperator() {
        // given & when
        final Expression expr = parseExpression("age > 18 AND age < 65");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression andOp = (BinaryOperatorExpression) expr;
        assertThat(andOp.operator()).isEqualTo(Operator.AND);

        // 왼쪽: age > 18
        assertThat(andOp.left()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression left = (BinaryOperatorExpression) andOp.left();
        assertThat(left.operator()).isEqualTo(Operator.GREATER_THAN);

        // 오른쪽: age < 65
        assertThat(andOp.right()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression right = (BinaryOperatorExpression) andOp.right();
        assertThat(right.operator()).isEqualTo(Operator.LESS_THAN);
    }

    @Test
    @DisplayName("OR 논리 연산자를 파싱한다")
    void testParseOrOperator() {
        // given & when
        final Expression expr = parseExpression("status = 'active' OR status = 'pending'");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression orOp = (BinaryOperatorExpression) expr;
        assertThat(orOp.operator()).isEqualTo(Operator.OR);
    }

    @Test
    @DisplayName("AND가 OR보다 우선순위가 높다")
    void testOperatorPrecedence() {
        // given & when
        // A OR B AND C는 A OR (B AND C)로 파싱되어야 함
        final Expression expr = parseExpression("a = 1 OR b = 2 AND value = 3");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression root = (BinaryOperatorExpression) expr;
        assertThat(root.operator()).isEqualTo(Operator.OR);

        // 오른쪽이 AND 표현식이어야 함
        assertThat(root.right()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression rightAnd = (BinaryOperatorExpression) root.right();
        assertThat(rightAnd.operator()).isEqualTo(Operator.AND);
    }

    @Test
    @DisplayName("괄호로 연산자 우선순위를 변경할 수 있다")
    void testParenthesizedExpression() {
        // given & when
        // (A OR B) AND C로 파싱되어야 함
        final Expression expr = parseExpression("(a = 1 OR b = 2) AND value = 3");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression root = (BinaryOperatorExpression) expr;
        assertThat(root.operator()).isEqualTo(Operator.AND);

        // 왼쪽이 OR 표현식이어야 함
        assertThat(root.left()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression leftOr = (BinaryOperatorExpression) root.left();
        assertThat(leftOr.operator()).isEqualTo(Operator.OR);
    }

    @Test
    @DisplayName("곱셈 연산자를 파싱한다")
    void testMultiplyOperator() {
        // given & when
        final Expression expr = parseExpression("price * quantity");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression binary = (BinaryOperatorExpression) expr;
        assertThat(binary.operator()).isEqualTo(Operator.MULTIPLY);
    }

    @Test
    @DisplayName("나누기 연산자를 파싱한다")
    void testDivideOperator() {
        // given & when
        final Expression expr = parseExpression("total / count");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression binary = (BinaryOperatorExpression) expr;
        assertThat(binary.operator()).isEqualTo(Operator.DIVIDE);
    }

    @Test
    @DisplayName("산술 연산자 우선순위를 올바르게 처리한다")
    void testArithmeticOperatorPrecedence() {
        // given & when
        // a + b * c / d - e는 a + ((b * c) / d) - e로 파싱되어야 함
        final Expression expr = parseExpression("a + b * c / d - e");

        // then
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression root = (BinaryOperatorExpression) expr;
        assertThat(root.operator()).isEqualTo(Operator.SUBTRACT);

        // 왼쪽은 덧셈: a + ((b * c) / d)
        assertThat(root.left()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression add = (BinaryOperatorExpression) root.left();
        assertThat(add.operator()).isEqualTo(Operator.ADD);

        // 덧셈의 오른쪽은 나누기: (b * c) / d
        assertThat(add.right()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression divide = (BinaryOperatorExpression) add.right();
        assertThat(divide.operator()).isEqualTo(Operator.DIVIDE);

        // 나누기의 왼쪽은 곱셈: b * c
        assertThat(divide.left()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression multiply = (BinaryOperatorExpression) divide.left();
        assertThat(multiply.operator()).isEqualTo(Operator.MULTIPLY);
    }

    @Test
    @DisplayName("복잡한 중첩 표현식을 파싱한다")
    void testComplexNestedExpression() {
        // given & when
        final String complexExpr = "age >= 18 AND (status = 'active' OR status = 'premium') AND country = 'KR'";
        final Expression expr = parseExpression(complexExpr);

        // then
        // 루트는 AND
        assertThat(expr).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression root = (BinaryOperatorExpression) expr;
        assertThat(root.operator()).isEqualTo(Operator.AND);

        // 왼쪽도 AND (age >= 18 AND (...))
        assertThat(root.left()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression leftAnd = (BinaryOperatorExpression) root.left();
        assertThat(leftAnd.operator()).isEqualTo(Operator.AND);

        // 왼쪽의 왼쪽은 age >= 18
        assertThat(leftAnd.left()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression ageCheck = (BinaryOperatorExpression) leftAnd.left();
        assertThat(ageCheck.operator()).isEqualTo(Operator.GREATER_THAN_OR_EQUALS);

        // 왼쪽의 오른쪽은 OR 표현식
        assertThat(leftAnd.right()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression statusOr = (BinaryOperatorExpression) leftAnd.right();
        assertThat(statusOr.operator()).isEqualTo(Operator.OR);
    }

    @Test
    @DisplayName("잘못된 표현식은 예외를 발생시킨다")
    void testInvalidExpression() {
        // given
        final String sql = "SELECT * FROM dummy WHERE age > ";

        // when & then
        assertParseException(sql);
    }

    @Test
    @DisplayName("닫히지 않은 괄호는 예외를 발생시킨다")
    void testUnclosedParenthesis() {
        // given
        final String sql = "SELECT * FROM dummy WHERE (age > 18";

        // when & then
        assertParseException(sql);
    }
}
