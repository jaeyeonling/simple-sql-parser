package com.jaeyeonling.parser.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.BooleanLiteral;
import com.jaeyeonling.ast.expression.DecimalLiteral;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.expression.NullLiteral;
import com.jaeyeonling.ast.expression.StringLiteral;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;

import java.util.Optional;

/**
 * 리터럴 파싱을 담당하는 클래스
 * - 숫자, 문자열, 불리언, NULL 리터럴 처리
 * - 간결한 구조
 */
public final class LiteralParser {

    private final TokenStream tokenStream;

    public LiteralParser(final TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    /**
     * 리터럴을 파싱합니다.
     *
     * @return 파싱된 리터럴 Expression, 리터럴이 아닌 경우 Optional.empty()
     */
    public Optional<Expression> parseLiteral() {
        final Token token = tokenStream.peek();
        final TokenType type = token.type();

        // 리터럴이 아니면 즉시 empty 반환
        if (!type.isLiteral()) {
            return Optional.empty();
        }

        tokenStream.advance();
        final SourceLocation location = new SourceLocation(token);

        final Expression literal = switch (type) {
            case INTEGER -> new IntegerLiteral(Integer.parseInt(token.value()), location);
            case DECIMAL -> new DecimalLiteral(Double.parseDouble(token.value()), location);
            case STRING -> new StringLiteral(token.value(), location);
            case TRUE -> new BooleanLiteral(true, location);
            case FALSE -> new BooleanLiteral(false, location);
            case NULL -> new NullLiteral(location);
            default -> throw new IllegalStateException(
                    String.format("예상치 못한 리터럴 타입: %s (위치: %d행 %d열)",
                            type, token.line(), token.column())
            );
        };

        return Optional.of(literal);
    }
}
