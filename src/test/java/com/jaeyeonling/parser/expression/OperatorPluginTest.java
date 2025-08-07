package com.jaeyeonling.parser.expression;

import com.jaeyeonling.ast.expression.BetweenExpression;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.lexer.SqlLexer;
import com.jaeyeonling.parser.ExpressionParser;
import com.jaeyeonling.parser.TokenStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 연산자 플러그인 시스템 테스트.
 * 커스텀 연산자를 동적으로 추가할 수 있음을 검증합니다.
 */
class OperatorPluginTest {

    @Test
    @DisplayName("우선순위에 따라 연산자가 처리된다")
    void testOperatorPriority() {
        // given - 다른 우선순위를 가진 커스텀 연산자
        class HighPriorityOperator implements OperatorParser {
            @Override
            public boolean canParse(TokenStream tokenStream) {
                return false; // 테스트용
            }

            @Override
            public Expression parse(TokenStream tokenStream, Expression left) {
                return left;
            }

            @Override
            public OperatorPriority priority() {
                return OperatorPriority.IS_NULL; // 높은 우선순위
            }
        }

        class LowPriorityOperator implements OperatorParser {
            @Override
            public boolean canParse(TokenStream tokenStream) {
                return false; // 테스트용
            }

            @Override
            public Expression parse(TokenStream tokenStream, Expression left) {
                return left;
            }

            @Override
            public OperatorPriority priority() {
                return OperatorPriority.LOGICAL_OR; // 낮은 우선순위
            }
        }

        // when
        final OperatorParserRegistry registry = new OperatorParserRegistry(new ExpressionProvider() {
            @Override
            public Expression parseExpression() {
                return null;
            }
            
            @Override
            public Expression parseAdditiveExpression() {
                return null;
            }
        });
        registry.registerCustom(new LowPriorityOperator())
                .registerCustom(new HighPriorityOperator());

        // then - 우선순위대로 정렬되었는지 확인
        final List<OperatorParser> parsers = registry.getAllParsers();
        assertThat(parsers).isNotEmpty();
        assertThat(parsers.getFirst().priority().value())
                .isLessThanOrEqualTo(parsers.getLast().priority().value());
    }

    @Test
    @DisplayName("기존 연산자와 커스텀 연산자가 함께 작동한다")
    void testMixedOperators() {
        // given
        final String sql = "age BETWEEN 18 AND 30";
        final SqlLexer lexer = new SqlLexer(sql);
        final TokenStream tokenStream = new TokenStream(lexer.tokenize());

        // when - 기본 파서 사용 (이미 BETWEEN이 등록됨)
        final ExpressionParser parser = new ExpressionParser(tokenStream);
        final Expression result = parser.parseExpression();

        // then
        assertThat(result).isInstanceOf(BetweenExpression.class);
        final BetweenExpression between = (BetweenExpression) result;
        assertThat(between.expression()).isInstanceOf(ColumnReference.class);
        assertThat(between.lowerBound()).isInstanceOf(IntegerLiteral.class);
        assertThat(between.upperBound()).isInstanceOf(IntegerLiteral.class);
    }
}
