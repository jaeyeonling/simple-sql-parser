package com.jaeyeonling.lexer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TokenType 테스트")
class TokenTypeTest {

    @Test
    @DisplayName("SQL 키워드를 올바르게 식별한다")
    void testKeywordIdentification() {
        // 키워드인 경우
        assertThat(TokenType.SELECT.isKeyword()).isTrue();
        assertThat(TokenType.FROM.isKeyword()).isTrue();
        assertThat(TokenType.WHERE.isKeyword()).isTrue();
        assertThat(TokenType.AND.isKeyword()).isTrue();
        assertThat(TokenType.OR.isKeyword()).isTrue();
        assertThat(TokenType.NULL.isKeyword()).isTrue();

        // 키워드가 아닌 경우
        assertThat(TokenType.IDENTIFIER.isKeyword()).isFalse();
        assertThat(TokenType.INTEGER.isKeyword()).isFalse();
        assertThat(TokenType.DECIMAL.isKeyword()).isFalse();
        assertThat(TokenType.STRING.isKeyword()).isFalse();
        assertThat(TokenType.STAR.isKeyword()).isFalse();
        assertThat(TokenType.COMMA.isKeyword()).isFalse();
        assertThat(TokenType.EOF.isKeyword()).isFalse();
    }

    @Test
    @DisplayName("절 시작 키워드를 올바르게 식별한다")
    void testClauseStarterIdentification() {
        assertThat(TokenType.SELECT.isClauseStarter()).isTrue();
        assertThat(TokenType.FROM.isClauseStarter()).isTrue();
        assertThat(TokenType.WHERE.isClauseStarter()).isTrue();
        assertThat(TokenType.GROUP.isClauseStarter()).isTrue();
        assertThat(TokenType.ORDER.isClauseStarter()).isTrue();
        assertThat(TokenType.HAVING.isClauseStarter()).isTrue();
        assertThat(TokenType.LIMIT.isClauseStarter()).isTrue();

        assertThat(TokenType.AND.isClauseStarter()).isFalse();
        assertThat(TokenType.OR.isClauseStarter()).isFalse();
        assertThat(TokenType.AS.isClauseStarter()).isFalse();
    }

    @Test
    @DisplayName("논리 연산자를 올바르게 식별한다")
    void testLogicalOperatorIdentification() {
        assertThat(TokenType.AND.isLogicalOperator()).isTrue();
        assertThat(TokenType.OR.isLogicalOperator()).isTrue();
        assertThat(TokenType.NOT.isLogicalOperator()).isTrue();

        assertThat(TokenType.EQUALS.isLogicalOperator()).isFalse();
        assertThat(TokenType.SELECT.isLogicalOperator()).isFalse();
    }

    @Test
    @DisplayName("비교 연산자를 올바르게 식별한다")
    void testComparisonOperatorIdentification() {
        assertThat(TokenType.EQUALS.isComparisonOperator()).isTrue();
        assertThat(TokenType.NOT_EQUALS.isComparisonOperator()).isTrue();
        assertThat(TokenType.LESS_THAN.isComparisonOperator()).isTrue();
        assertThat(TokenType.GREATER_THAN.isComparisonOperator()).isTrue();
        assertThat(TokenType.IN.isComparisonOperator()).isTrue();
        assertThat(TokenType.BETWEEN.isComparisonOperator()).isTrue();
        assertThat(TokenType.LIKE.isComparisonOperator()).isTrue();
        assertThat(TokenType.IS.isComparisonOperator()).isTrue();

        assertThat(TokenType.AND.isComparisonOperator()).isFalse();
        assertThat(TokenType.PLUS.isComparisonOperator()).isFalse();
    }

    @Test
    @DisplayName("리터럴 값을 올바르게 식별한다")
    void testLiteralIdentification() {
        assertThat(TokenType.NULL.isLiteral()).isTrue();
        assertThat(TokenType.TRUE.isLiteral()).isTrue();
        assertThat(TokenType.FALSE.isLiteral()).isTrue();
        assertThat(TokenType.INTEGER.isLiteral()).isTrue();
        assertThat(TokenType.DECIMAL.isLiteral()).isTrue();
        assertThat(TokenType.STRING.isLiteral()).isTrue();

        assertThat(TokenType.SELECT.isLiteral()).isFalse();
        assertThat(TokenType.IDENTIFIER.isLiteral()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(TokenType.class)
    @DisplayName("모든 TokenType은 심볼을 가진다")
    void testAllTokenTypesHaveSymbol(final TokenType type) {
        assertThat(type.symbol()).isNotNull();
        assertThat(type.symbol()).isNotEmpty();
    }

    @Test
    @DisplayName("키워드 개수가 올바른지 확인한다")
    void testKeywordCount() {
        long keywordCount = Arrays.stream(TokenType.values())
                .filter(TokenType::isKeyword)
                .count();

        // 현재 정의된 키워드 개수: 23개
        assertThat(keywordCount).isEqualTo(23);
    }

    @Test
    @DisplayName("단일 문자 심볼을 올바르게 식별한다")
    void testSingleCharSymbolIdentification() {
        // 단일 문자 심볼인 경우
        assertThat(TokenType.STAR.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.COMMA.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.DOT.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.LPAREN.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.RPAREN.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.PLUS.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.MINUS.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.EQUALS.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.LESS_THAN.isSingleCharSymbol()).isTrue();
        assertThat(TokenType.GREATER_THAN.isSingleCharSymbol()).isTrue();

        // 단일 문자 심볼이 아닌 경우
        assertThat(TokenType.NOT_EQUALS.isSingleCharSymbol()).isFalse();
        assertThat(TokenType.LESS_THAN_OR_EQUALS.isSingleCharSymbol()).isFalse();
        assertThat(TokenType.GREATER_THAN_OR_EQUALS.isSingleCharSymbol()).isFalse();
        assertThat(TokenType.SELECT.isSingleCharSymbol()).isFalse();
        assertThat(TokenType.IDENTIFIER.isSingleCharSymbol()).isFalse();
    }

    @Test
    @DisplayName("단일 문자 심볼을 문자로 변환한다")
    void testAsChar() {
        assertThat(TokenType.STAR.asChar()).isEqualTo('*');
        assertThat(TokenType.COMMA.asChar()).isEqualTo(',');
        assertThat(TokenType.DOT.asChar()).isEqualTo('.');
        assertThat(TokenType.LPAREN.asChar()).isEqualTo('(');
        assertThat(TokenType.RPAREN.asChar()).isEqualTo(')');
        assertThat(TokenType.PLUS.asChar()).isEqualTo('+');
        assertThat(TokenType.MINUS.asChar()).isEqualTo('-');
    }

    @Test
    @DisplayName("단일 문자가 아닌 심볼에서 asChar 호출 시 예외가 발생한다")
    void testAsCharThrowsForNonSingleChar() {
        assertThatThrownBy(TokenType.SELECT::asChar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("단일 문자 심볼이 아닙니다");
    }
}
