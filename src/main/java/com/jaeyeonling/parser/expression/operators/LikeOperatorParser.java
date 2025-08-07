package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.LikeExpression;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.ExpressionProvider;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

/**
 * LIKE/NOT LIKE 연산자 파싱을 담당합니다.
 * 예: name LIKE 'John%', name NOT LIKE '%test%'
 */
public final class LikeOperatorParser implements OperatorParser {

    private final ExpressionProvider expressionProvider;

    public LikeOperatorParser(final ExpressionProvider expressionProvider) {
        this.expressionProvider = expressionProvider;
    }

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        return tokenStream.check(TokenType.LIKE);
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        tokenStream.consume(TokenType.LIKE, "LIKE 키워드가 필요합니다");

        // 패턴 파싱 (문자열 리터럴이어야 함)
        final Expression pattern = expressionProvider.parseAdditiveExpression();


        final SourceLocation location = left.location().merge(pattern.location());

        return new LikeExpression(left, pattern, location);
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.SPECIAL;
    }
}
