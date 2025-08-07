package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.NotBetweenExpression;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.ExpressionProvider;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

/**
 * NOT BETWEEN 연산자 파싱을 담당합니다.
 * 예: price NOT BETWEEN 0 AND 100
 */
public class NotBetweenOperatorParser implements OperatorParser {

    private final ExpressionProvider expressionProvider;

    public NotBetweenOperatorParser(final ExpressionProvider expressionProvider) {
        this.expressionProvider = expressionProvider;
    }

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        if (!tokenStream.check(TokenType.NOT)) {
            return false;
        }

        final Token nextToken = tokenStream.peekAt(1);
        return nextToken != null && nextToken.type() == TokenType.BETWEEN;
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        tokenStream.consume(TokenType.NOT,
                "NOT 키워드가 필요합니다");
        tokenStream.consume(TokenType.BETWEEN,
                "NOT BETWEEN에서 BETWEEN 키워드가 필요합니다");

        // 하한값 파싱
        final Expression lowerBound = expressionProvider.parseAdditiveExpression();

        // AND 키워드 확인
        tokenStream.consume(TokenType.AND,
                "NOT BETWEEN 연산자는 AND로 연결되어야 합니다.\n" +
                        "예시: price NOT BETWEEN 0 AND 100");

        // 상한값 파싱
        final Expression upperBound = expressionProvider.parseAdditiveExpression();

        final SourceLocation location = left.location().merge(upperBound.location());

        return new NotBetweenExpression(left, lowerBound, upperBound, location);
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.SPECIAL;
    }
}
