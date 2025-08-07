package com.jaeyeonling.parser;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.clause.FromClause;
import com.jaeyeonling.ast.table.Table;
import com.jaeyeonling.ast.table.TableReference;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * FROM 절 파싱을 담당하는 클래스.
 */
public class FromClauseParser {

    private final TokenStream tokenStream;

    public FromClauseParser(final TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    /**
     * FROM 절을 파싱합니다.
     */
    public FromClause parse() {
        final Token fromToken = tokenStream.consume(TokenType.FROM,
                "SELECT 절 다음에는 FROM 절이 와야 합니다.\n" +
                        "예시: SELECT 컬럼명 FROM 테이블명");

        final List<TableReference> tableReferences = new ArrayList<>();

        do {
            tableReferences.add(parseTableReference());
        } while (tokenStream.advanceIfMatch(TokenType.COMMA));

        return new FromClause(tableReferences, new SourceLocation(fromToken));
    }

    /**
     * 테이블 참조를 파싱합니다.
     * 테이블명과 별칭을 처리합니다.
     */
    private TableReference parseTableReference() {
        final Token tableToken = tokenStream.consume(TokenType.IDENTIFIER,
                "FROM 절 다음에 테이블 이름을 지정해야 합니다.\n" +
                        "예시: FROM users, FROM products AS p");
        final String tableName = tableToken.value();

        // AS alias 또는 그냥 alias
        if (tokenStream.advanceIfMatch(TokenType.AS)) {
            final Token aliasToken = tokenStream.consume(TokenType.IDENTIFIER,
                    "AS 키워드 다음에는 테이블 별칭을 지정해야 합니다.\n" +
                            "예시: FROM users AS u, FROM orders AS o");
            return new Table(tableName, aliasToken.value(), new SourceLocation(tableToken));
        }

        // AS 없이 별칭이 올 수 있음
        if (tokenStream.check(TokenType.IDENTIFIER) && !isReservedKeyword(tokenStream.peek())) {
            return new Table(tableName, tokenStream.advance().value(), new SourceLocation(tableToken));
        }

        return new Table(tableName, null, new SourceLocation(tableToken));
    }

    /**
     * 예약어인지 확인합니다.
     */
    private boolean isReservedKeyword(final Token token) {
        return tokenStream.isKeyword(token);
    }
}
