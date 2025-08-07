package com.jaeyeonling.parser.expression;

import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.operators.BetweenOperatorParser;
import com.jaeyeonling.parser.expression.operators.ComparisonOperatorParser;
import com.jaeyeonling.parser.expression.operators.InOperatorParser;
import com.jaeyeonling.parser.expression.operators.IsNotNullOperatorParser;
import com.jaeyeonling.parser.expression.operators.IsNullOperatorParser;
import com.jaeyeonling.parser.expression.operators.LikeOperatorParser;
import com.jaeyeonling.parser.expression.operators.NotBetweenOperatorParser;
import com.jaeyeonling.parser.expression.operators.NotInOperatorParser;
import com.jaeyeonling.parser.expression.operators.NotLikeOperatorParser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 연산자 파서들을 관리하는 레지스트리.
 * 우선순위에 따라 파서를 정렬하고, 적절한 파서를 찾아주는 역할을 합니다.
 */
public class OperatorParserRegistry {

    private final List<OperatorParser> parsers = new CopyOnWriteArrayList<>();
    private final ExpressionProvider expressionProvider;

    /**
     * 기본 생성자. 표준 SQL 연산자 파서들을 등록합니다.
     */
    public OperatorParserRegistry(final ExpressionProvider expressionProvider) {
        this.expressionProvider = expressionProvider;
        registerDefaultParsers();
    }

    /**
     * 기본 파서들을 등록합니다.
     */
    private void registerDefaultParsers() {
        // NOT 파서들을 먼저 등록 (우선순위가 높음)
        register(new NotLikeOperatorParser(expressionProvider));
        register(new NotInOperatorParser(expressionProvider));
        register(new NotBetweenOperatorParser(expressionProvider));
        register(new IsNotNullOperatorParser());

        // 일반 특수 연산자들
        register(new IsNullOperatorParser());
        register(new LikeOperatorParser(expressionProvider));
        register(new InOperatorParser(expressionProvider));
        register(new BetweenOperatorParser(expressionProvider));

        // 일반 비교 연산자
        register(new ComparisonOperatorParser(expressionProvider));
    }

    /**
     * 새로운 파서를 등록합니다.
     *
     * @param parser 등록할 파서
     */
    public void register(final OperatorParser parser) {
        parsers.add(parser);
        // 우선순위로 정렬 (낮은 값이 먼저)
        parsers.sort(Comparator.comparingInt(p -> p.priority().value()));
    }

    /**
     * 커스텀 파서를 등록합니다. (플러그인 시스템)
     *
     * @param parser 커스텀 파서
     * @return 체이닝을 위한 this
     */
    public OperatorParserRegistry registerCustom(final OperatorParser parser) {
        register(parser);
        return this;
    }

    /**
     * 현재 토큰 스트림에서 처리 가능한 파서를 찾습니다.
     *
     * @param tokenStream 토큰 스트림
     * @return 처리 가능한 파서 (없으면 empty)
     */
    public Optional<OperatorParser> findParser(final TokenStream tokenStream) {
        return parsers.stream()
                .filter(parser -> parser.canParse(tokenStream))
                .findFirst();
    }

    /**
     * 등록된 모든 파서를 반환합니다.
     *
     * @return 불변 리스트로 래핑된 파서 목록
     */
    public List<OperatorParser> getAllParsers() {
        return Collections.unmodifiableList(parsers);
    }
}
