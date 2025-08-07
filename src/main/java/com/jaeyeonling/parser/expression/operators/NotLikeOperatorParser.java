package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.NotLikeExpression;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.ExpressionProvider;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

/**
 * NOT LIKE 연산자 파싱을 담당합니다.
 * 예: name NOT LIKE '%test%'
 */
public final class NotLikeOperatorParser implements OperatorParser {

    private final ExpressionProvider expressionProvider;

    public NotLikeOperatorParser(final ExpressionProvider expressionProvider) {
        this.expressionProvider = expressionProvider;
    }

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        if (!tokenStream.check(TokenType.NOT)) {
            return false;
        }

        final Token nextToken = tokenStream.peekAt(1);
        return nextToken != null && nextToken.type() == TokenType.LIKE;
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        tokenStream.consume(TokenType.NOT, "NOT 키워드가 필요합니다");
        tokenStream.consume(TokenType.LIKE, "NOT LIKE에서 LIKE 키워드가 필요합니다");

        // 패턴 파싱
        final Expression pattern = expressionProvider.parseAdditiveExpression();

        final SourceLocation location = left.location().merge(pattern.location());

        return new NotLikeExpression(left, pattern, location);
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.SPECIAL;
    }
}
