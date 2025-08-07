package com.jaeyeonling.parser;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.clause.GroupByClause;
import com.jaeyeonling.ast.clause.HavingClause;
import com.jaeyeonling.ast.clause.LimitClause;
import com.jaeyeonling.ast.clause.OrderByClause;
import com.jaeyeonling.ast.clause.OrderByItem;
import com.jaeyeonling.ast.clause.OrderDirection;
import com.jaeyeonling.ast.clause.WhereClause;
import com.jaeyeonling.ast.expression.DecimalLiteral;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.exception.SyntaxException;
import com.jaeyeonling.lexer.SqlLexer;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL 파서.
 */
public class SqlParser {

    private final String sql;
    private final ParserErrorHandler errorHandler = new ParserErrorHandler();

    /**
     * SQL 문자열로 파서를 생성합니다.
     *
     * @param sql 파싱할 SQL 문자열
     */
    public SqlParser(final String sql) {
        this.sql = sql;
    }

    /**
     * SQL을 파싱하여 SelectStatement를 반환합니다.
     */
    public SelectStatement parse() throws SyntaxException {
        final SqlLexer lexer = new SqlLexer(sql);
        final List<Token> tokens = lexer.tokenize();

        return parseSelectStatement(tokens);
    }

    /**
     * SELECT 문 파싱
     */
    private SelectStatement parseSelectStatement(final List<Token> tokens) {
        final TokenStream tokenStream = new TokenStream(tokens);
        final ExpressionParser expressionParser = new ExpressionParser(tokenStream);

        final SelectStatement.Builder builder = SelectStatement.builder();

        // SELECT 절 (복잡하므로 별도 클래스 유지)
        builder.selectClause(new SelectClauseParser(tokenStream).parse());

        // FROM 절 (선택적 - SELECT TRUE 같은 경우 FROM이 없을 수 있음)
        if (tokenStream.check(TokenType.FROM)) {
            builder.fromClause(new FromClauseParser(tokenStream).parse());
        }

        // WHERE 절 (간단하므로 직접 처리)
        if (tokenStream.advanceIfMatch(TokenType.WHERE)) {
            builder.whereClause(parseWhereClause(tokenStream, expressionParser));
        }

        // GROUP BY 절 
        if (tokenStream.advanceIfMatch(TokenType.GROUP)) {
            tokenStream.consume(TokenType.BY,
                    "GROUP BY 구문을 완성해야 합니다.\n" +
                            "예시: SELECT category, COUNT(*) FROM products GROUP BY category");
            builder.groupByClause(parseGroupByClause(tokenStream, expressionParser));
        }

        // HAVING 절
        if (tokenStream.advanceIfMatch(TokenType.HAVING)) {
            builder.havingClause(parseHavingClause(tokenStream, expressionParser));
        }

        // ORDER BY 절
        if (tokenStream.advanceIfMatch(TokenType.ORDER)) {
            tokenStream.consume(TokenType.BY,
                    "ORDER BY 구문을 완성해야 합니다.\n" +
                            "예시: SELECT * FROM users ORDER BY name ASC");
            builder.orderByClause(parseOrderByClause(tokenStream, expressionParser));
        }

        // LIMIT 절
        if (tokenStream.advanceIfMatch(TokenType.LIMIT)) {
            builder.limitClause(parseLimitClause(tokenStream, expressionParser));
        }

        // 위치 정보 설정
        builder.location(new SourceLocation(1, 1, 0, tokenStream.current().column()));

        // 모든 토큰이 소비되었는지 확인
        errorHandler.enforceEndOfFile(tokenStream);

        return builder.build();
    }

    /**
     * WHERE 절 파싱 (간단한 로직)
     */
    private WhereClause parseWhereClause(
            final TokenStream tokenStream,
            final ExpressionParser expressionParser
    ) {
        final Token whereToken = tokenStream.previous();
        final Expression condition = expressionParser.parseExpression();

        return new WhereClause(condition, new SourceLocation(whereToken));
    }

    /**
     * GROUP BY 절 파싱
     */
    private GroupByClause parseGroupByClause(
            final TokenStream tokenStream,
            final ExpressionParser expressionParser
    ) {
        final Token groupToken = tokenStream.previous();
        final List<Expression> expressions = new ArrayList<>();

        do {
            expressions.add(expressionParser.parseExpression());
        } while (tokenStream.advanceIfMatch(TokenType.COMMA));

        return new GroupByClause(expressions, new SourceLocation(groupToken));
    }

    /**
     * HAVING 절 파싱
     */
    private HavingClause parseHavingClause(
            final TokenStream tokenStream,
            final ExpressionParser expressionParser
    ) {
        final Token havingToken = tokenStream.previous();
        final Expression condition = expressionParser.parseExpression();

        return new HavingClause(condition, new SourceLocation(havingToken));
    }

    /**
     * ORDER BY 절 파싱
     */
    private OrderByClause parseOrderByClause(
            final TokenStream tokenStream,
            final ExpressionParser expressionParser
    ) {
        final Token orderToken = tokenStream.previous();
        final List<OrderByItem> items = new ArrayList<>();

        do {
            items.add(parseOrderByItem(tokenStream, expressionParser));
        } while (tokenStream.advanceIfMatch(TokenType.COMMA));

        return new OrderByClause(items, new SourceLocation(orderToken));
    }

    /**
     * ORDER BY 아이템 파싱
     */
    private OrderByItem parseOrderByItem(
            final TokenStream tokenStream,
            final ExpressionParser expressionParser
    ) {
        final Expression expression = expressionParser.parseExpression();

        // DESC 또는 ASC 키워드 처리
        final OrderDirection direction;
        if (tokenStream.advanceIfMatch(TokenType.DESC)) {
            direction = OrderDirection.DESC;
        } else if (tokenStream.advanceIfMatch(TokenType.ASC)) {
            direction = OrderDirection.ASC;
        } else {
            // 방향이 명시되지 않으면 기본값 ASC
            direction = OrderDirection.ASC;
        }

        return new OrderByItem(expression, direction);
    }

    /**
     * LIMIT 절 파싱
     */
    private LimitClause parseLimitClause(
            final TokenStream tokenStream,
            final ExpressionParser expressionParser
    ) {
        final Token limitToken = tokenStream.previous();

        // LIMIT 값 검증 및 파싱
        validateHasValue(tokenStream, "LIMIT");
        final Expression limitExpr = expressionParser.parseExpression();
        final int limit = extractInteger(limitExpr, "LIMIT");

        if (!tokenStream.advanceIfMatch(TokenType.OFFSET)) {
            return new LimitClause(limit, null, new SourceLocation(limitToken));
        }

        validateHasValue(tokenStream, "OFFSET");
        final Expression offsetExpr = expressionParser.parseExpression();
        final int offset = extractInteger(offsetExpr, "OFFSET");

        return new LimitClause(limit, offset, new SourceLocation(limitToken));
    }

    /**
     * LIMIT/OFFSET 값 존재 검증
     */
    private void validateHasValue(
            final TokenStream tokenStream,
            final String clauseName
    ) {
        if (tokenStream.isAtEnd() || isClauseKeyword(tokenStream.peek())) {
            final String message = clauseName.equals("LIMIT")
                    ? "LIMIT 절에는 반환할 행 수를 지정하는 정수가 와야 합니다.\n예시: SELECT * FROM users LIMIT 10"
                    : "OFFSET 절에는 건너뛸 행 수를 지정하는 정수가 와야 합니다.\n예시: SELECT * FROM users LIMIT 10 OFFSET 20";
            throw new SyntaxException(message);
        }
    }

    /**
     * 표현식에서 정수 추출
     */
    private int extractInteger(
            final Expression expr,
            final String clauseName
    ) {
        if (expr instanceof IntegerLiteral intLiteral) {
            return intLiteral.value();
        }

        if (expr instanceof DecimalLiteral decimalLiteral) {
            throw new SyntaxException(
                    clauseName + " 값은 정수여야 합니다. 소수점이 있는 숫자(" +
                            decimalLiteral.value() + ")는 사용할 수 없습니다.\n" +
                            "예시: " + clauseName + " 10 (올바름), " + clauseName + " 10.5 (잘못됨)"
            );
        }

        throw new SyntaxException(
                clauseName + " 절에는 숫자만 사용할 수 있습니다.\n" +
                        "현재 입력값: " + expr + "\n" +
                        "예시: " + clauseName + " 10"
        );
    }

    /**
     * 토큰이 SQL 절 키워드인지 확인
     */
    private boolean isClauseKeyword(final Token token) {
        return switch (token.type()) {
            case WHERE, GROUP, HAVING, ORDER, LIMIT, OFFSET -> true;
            default -> false;
        };
    }
}
