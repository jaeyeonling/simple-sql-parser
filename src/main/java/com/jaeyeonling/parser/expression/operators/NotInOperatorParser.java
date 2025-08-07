package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.NotInExpression;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.ExpressionProvider;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

import java.util.ArrayList;
import java.util.List;

/**
 * NOT IN 연산자 파싱을 담당합니다.
 * 예: id NOT IN (1, 2, 3)
 */
public final class NotInOperatorParser implements OperatorParser {

    private final ExpressionProvider expressionProvider;

    public NotInOperatorParser(final ExpressionProvider expressionProvider) {
        this.expressionProvider = expressionProvider;
    }

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        if (!tokenStream.check(TokenType.NOT)) {
            return false;
        }

        final Token nextToken = tokenStream.peekAt(1);
        return nextToken != null && nextToken.type() == TokenType.IN;
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        tokenStream.consume(TokenType.NOT, "NOT 키워드가 필요합니다");
        tokenStream.consume(TokenType.IN, "NOT IN에서 IN 키워드가 필요합니다");
        tokenStream.consume(TokenType.LPAREN,
                "NOT IN 연산자 뒤에는 '('가 와야 합니다.\n" +
                        "예시: id NOT IN (1, 2, 3)");

        // 값 목록 파싱
        final List<Expression> values = parseValueList(tokenStream);

        tokenStream.consume(TokenType.RPAREN,
                "NOT IN 절의 값 목록은 ')'로 닫아야 합니다.");

        final SourceLocation location = left.location();

        return new NotInExpression(left, values, location);
    }

    private List<Expression> parseValueList(TokenStream tokenStream) {
        final List<Expression> values = new ArrayList<>();

        do {
            // 쉼표로 구분된 추가 값들
            values.add(expressionProvider.parseAdditiveExpression());
        } while (tokenStream.advanceIfMatch(TokenType.COMMA));

        return values;
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.SPECIAL;
    }
}
