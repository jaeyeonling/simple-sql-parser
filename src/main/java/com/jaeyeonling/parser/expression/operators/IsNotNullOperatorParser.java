package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.IsNotNullExpression;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

/**
 * IS NOT NULL 연산자 파싱을 담당합니다.
 * 예: name IS NOT NULL
 */
public final class IsNotNullOperatorParser implements OperatorParser {

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        if (!tokenStream.check(TokenType.IS)) {
            return false;
        }

        final Token nextToken = tokenStream.peekAt(1);
        return nextToken != null && nextToken.type() == TokenType.NOT;
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        tokenStream.consume(TokenType.IS, "IS 키워드가 필요합니다");
        tokenStream.consume(TokenType.NOT, "IS NOT NULL에서 NOT 키워드가 필요합니다");
        tokenStream.consume(TokenType.NULL,
                "IS NOT 뒤에는 NULL이 와야 합니다.\n" +
                        "예시: name IS NOT NULL");

        final SourceLocation location = left.location();
        return new IsNotNullExpression(left, location);
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.IS_NULL;
    }
}
