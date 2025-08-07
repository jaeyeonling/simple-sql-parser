package com.jaeyeonling.parser;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.Operator;
import com.jaeyeonling.exception.SyntaxException;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.expression.ExpressionProvider;
import com.jaeyeonling.parser.expression.IdentifierParser;
import com.jaeyeonling.parser.expression.LiteralParser;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorParserRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * SQL 표현식 파싱을 담당하는 클래스.
 *
 * <h3>파싱 우선순위 (낮음 → 높음)</h3>
 * <ol>
 *   <li>OR - 논리 OR 연산</li>
 *   <li>AND - 논리 AND 연산</li>
 *   <li>비교 연산자 - LIKE, IN, BETWEEN, =, !=, <, > 등 (레지스트리로 관리)</li>
 *   <li>덧셈/뺄셈 - +, -</li>
 *   <li>곱셈/나눗셈 - *</li>
 *   <li>단항 연산자 - NOT</li>
 *   <li>Primary - 리터럴, 식별자, 괄호 표현식</li>
 * </ol>
 *
 * <h3>설계 원칙</h3>
 * <ul>
 *   <li>복잡한 연산자(LIKE, IN, BETWEEN 등)는 레지스트리로 관리하여 확장성 확보</li>
 *   <li>기본 이항 연산자(OR, AND, +, -, *)는 성능과 가독성을 위해 직접 처리</li>
 *   <li>리터럴과 식별자 파싱은 전문 파서에게 위임</li>
 * </ul>
 */
public final class ExpressionParser implements ExpressionProvider {

    private static final Map<TokenType, Operator> binaryOperators = Map.of(
            TokenType.PLUS, Operator.ADD,
            TokenType.MINUS, Operator.SUBTRACT,
            TokenType.STAR, Operator.MULTIPLY,
            TokenType.SLASH, Operator.DIVIDE,
            TokenType.OR, Operator.OR,
            TokenType.AND, Operator.AND
    );

    private final TokenStream tokenStream;
    private final LiteralParser literalParser;
    private final IdentifierParser identifierParser;
    private final OperatorParserRegistry operatorRegistry;

    public ExpressionParser(final TokenStream tokenStream) {
        this.tokenStream = tokenStream;
        this.literalParser = new LiteralParser(tokenStream);
        this.identifierParser = new IdentifierParser(tokenStream);
        this.operatorRegistry = new OperatorParserRegistry(this);
    }

    /**
     * 표현식을 파싱합니다.
     * 가장 낮은 우선순위(OR)부터 시작하여 재귀적으로 파싱합니다.
     */
    public Expression parseExpression() {
        return parseOrExpression();
    }

    /**
     * 이항 연산자를 일반적으로 파싱하는 헬퍼 메서드.
     */
    private Expression parseBinaryExpression(
            final Supplier<Expression> nextLevel,
            final TokenType... operatorTypes
    ) {
        Expression left = nextLevel.get();

        while (tokenStream.advanceIfMatch(operatorTypes)) {
            final Token operatorToken = tokenStream.previous();
            final Expression right = nextLevel.get();

            final Operator operator = binaryOperators.get(operatorToken.type());
            if (operator == null) {
                throw new SyntaxException("매핑되지 않은 연산자", operatorToken);
            }

            final SourceLocation location = left.location().merge(right.location());
            left = new BinaryOperatorExpression(left, operator, right, location);
        }

        return left;
    }

    /**
     * OR 표현식을 파싱합니다. (우선순위: 최하위)
     * 예: condition1 OR condition2
     */
    private Expression parseOrExpression() {
        return parseBinaryExpression(
                this::parseAndExpression,
                TokenType.OR
        );
    }

    /**
     * AND 표현식을 파싱합니다. (우선순위: OR보다 높음)
     * 예: condition1 AND condition2
     */
    private Expression parseAndExpression() {
        return parseBinaryExpression(
                this::parseComparisonExpression,
                TokenType.AND
        );
    }

    /**
     * 비교 연산자 표현식을 파싱합니다.
     * 복잡한 연산자(LIKE, IN, BETWEEN, NOT 버전들)는 레지스트리를 통해 처리합니다.
     * 레지스트리는 우선순위에 따라 파서를 정렬하므로, NOT LIKE가 LIKE보다 먼저 매칭됩니다.
     */
    private Expression parseComparisonExpression() {
        Expression left = parseAdditiveExpression();

        // 레지스트리가 모든 비교 연산자를 처리 (NOT 버전 포함)
        Optional<OperatorParser> parser = operatorRegistry.findParser(tokenStream);
        if (parser.isPresent()) {
            return parser.get().parse(tokenStream, left);
        }

        // 파서를 찾지 못한 경우 = 연산자가 없는 단순 표현식
        return left;
    }

    /**
     * 덧셈/뺄셈 표현식을 파싱합니다.
     * 예: value1 + value2 - value3
     * ExpressionProvider 인터페이스 구현. 레지스트리의 파서들이 하위 레벨 파싱에 사용합니다.
     */
    @Override
    public Expression parseAdditiveExpression() {
        return parseBinaryExpression(
                this::parseMultiplicativeExpression,
                TokenType.PLUS, TokenType.MINUS
        );
    }

    /**
     * 곱셈/나눗셈 표현식을 파싱합니다.
     * 예: value1 * value2 / value3
     */
    private Expression parseMultiplicativeExpression() {
        return parseBinaryExpression(
                this::parsePrimaryExpression,
                TokenType.STAR, TokenType.SLASH
        );
    }

    /**
     * 기본 표현식을 파싱합니다. (우선순위: 최상위)
     *
     * <h4>처리 순서:</h4>
     * <ol>
     *   <li>단항 NOT 연산자</li>
     *   <li>리터럴 (숫자, 문자열, 불리언, NULL - LiteralParser에 위임)</li>
     *   <li>괄호로 묶인 표현식</li>
     *   <li>식별자/컬럼 참조 (IdentifierParser에 위임)</li>
     * </ol>
     */
    private Expression parsePrimaryExpression() {
        // 1. 단항 NOT 연산자 (예: NOT EXISTS)
        if (tokenStream.advanceIfMatch(TokenType.NOT)) {
            final Expression expr = parsePrimaryExpression();
            return new BinaryOperatorExpression(
                    expr,
                    Operator.NOT,
                    null,  // 단항 연산자이므로 right는 null
                    expr.location()
            );
        }

        // 2. 리터럴 파싱 위임 (정수, 소수, 문자열, 불리언, NULL)
        final Optional<Expression> literal = literalParser.parseLiteral();
        if (literal.isPresent()) {
            return literal.get();
        }

        // 3. 괄호로 묶인 표현식
        if (tokenStream.advanceIfMatch(TokenType.LPAREN)) {
            final Expression expr = parseExpression();
            tokenStream.consume(TokenType.RPAREN,
                    "괄호로 시작한 표현식은 ')'로 닫아야 합니다.\n" +
                            "예시: (age > 18), (price * 0.9)");
            return expr;
        }

        // 4. 식별자 파싱 위임 (컬럼명 또는 테이블.컬럼)
        final Expression identifier = identifierParser.parseIdentifier();
        if (identifier != null) {
            return identifier;
        }

        // 파싱 실패 - 상세한 에러 메시지 제공
        throw new SyntaxException(
                "표현식을 파싱할 수 없습니다.\n" +
                        "표현식에는 컬럼명, 숫자, 문자열, 함수 호출 등이 올 수 있습니다.",
                tokenStream.peek());
    }
}
