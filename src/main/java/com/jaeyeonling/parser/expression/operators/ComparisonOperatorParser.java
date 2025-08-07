package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.Operator;
import com.jaeyeonling.exception.SyntaxException;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.ExpressionProvider;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

import java.util.Set;

/**
 * 일반 비교 연산자 파싱을 담당합니다.
 * 예: =, !=, <, >, <=, >=
 */
public final class ComparisonOperatorParser implements OperatorParser {

    private static final Set<TokenType> comparisonOperators = Set.of(
            TokenType.EQUALS, TokenType.NOT_EQUALS,
            TokenType.LESS_THAN, TokenType.GREATER_THAN,
            TokenType.LESS_THAN_OR_EQUALS, TokenType.GREATER_THAN_OR_EQUALS
    );

    private final ExpressionProvider expressionProvider;

    public ComparisonOperatorParser(final ExpressionProvider expressionProvider) {
        this.expressionProvider = expressionProvider;
    }

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        return comparisonOperators.stream()
                .anyMatch(tokenStream::check);
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        final Token operatorToken = consumeComparisonOperator(tokenStream);
        final Expression right = expressionProvider.parseAdditiveExpression();
        final Operator operator = mapTokenToOperator(operatorToken.type());
        final SourceLocation location = left.location().merge(right.location());

        return new BinaryOperatorExpression(left, operator, right, location);
    }

    /**
     * 비교 연산자 토큰을 소비하고 반환합니다.
     *
     * @param tokenStream 토큰 스트림
     * @return 소비된 비교 연산자 토큰
     * @throws SyntaxException 비교 연산자가 없는 경우
     */
    private Token consumeComparisonOperator(final TokenStream tokenStream) {
        for (final TokenType type : comparisonOperators) {
            if (tokenStream.advanceIfMatch(type)) {
                return tokenStream.previous();
            }
        }

        throw new SyntaxException(
                "비교 연산자가 필요합니다.\n" +
                        "예상: =, !=, <, <=, >, >=",
                tokenStream.peek()
        );
    }

    private Operator mapTokenToOperator(final TokenType tokenType) {
        return switch (tokenType) {
            case EQUALS -> Operator.EQUALS;
            case NOT_EQUALS -> Operator.NOT_EQUALS;
            case LESS_THAN -> Operator.LESS_THAN;
            case GREATER_THAN -> Operator.GREATER_THAN;
            case LESS_THAN_OR_EQUALS -> Operator.LESS_THAN_OR_EQUALS;
            case GREATER_THAN_OR_EQUALS -> Operator.GREATER_THAN_OR_EQUALS;
            default -> throw new SyntaxException(
                    "지원하지 않는 비교 연산자입니다: " + tokenType);
        };
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.COMPARISON;
    }
}
