# 객체지향 프로그래밍 학습

이 프로젝트를 통해 학습할 수 있는 객체지향의 핵심 개념들입니다.

## 왜 파서 생성기를 사용하지 않았나?

ANTLR 같은 도구는 편리하지만 내부 동작을 숨깁니다.
직접 구현함으로써 복잡한 문제를 객체지향적으로 해결하는 과정을 학습할 수 있습니다.

## 핵심 OOP 개념

### 1. 단일 책임 원칙 (SRP)

각 클래스는 하나의 책임만 가집니다.

```java
// 나쁜 예: 여러 책임을 가진 클래스
class SqlProcessor {
    List<Token> tokenize(String sql) {
    }     // 토큰화

    SelectStatement parse(List<Token> tokens) {
    }  // 파싱

    String format(SelectStatement stmt) {
    }   // 포맷팅
}

// 좋은 예: 책임 분리
class SqlLexer {
    List<Token> tokenize(String sql) {
    }
}

class SqlParser {
    SelectStatement parse(List<Token> tokens) {
    }
}

class SqlToStringVisitor {
    String visit(SelectStatement stmt) {
    }
}
```

### 2. 개방-폐쇄 원칙 (OCP)

확장에는 열려있고 수정에는 닫혀있습니다.

```java
// 새로운 연산자 추가 시 기존 코드 수정 없음
public class OperatorParserRegistry {
    private final List<OperatorParser> parsers = new ArrayList<>();

    public void register(OperatorParser parser) {
        parsers.add(parser);  // 확장
    }
}

// 사용
registry.

register(new LikeOperatorParser());    // LIKE 추가
        registry.

register(new BetweenOperatorParser()); // BETWEEN 추가
```

### 3. 인터페이스 분리 원칙 (ISP)

클라이언트가 사용하지 않는 메서드에 의존하지 않습니다.

```java
// 역할별로 작은 인터페이스 분리
interface Expression {
    <T> T accept(AstVisitor<T> visitor);
}

interface SelectItem {
    Optional<String> alias();
}

interface TableReference {
    String name();
}
```

### 4. 의존성 역전 원칙 (DIP)

구체 클래스가 아닌 추상화에 의존합니다.

```java
public class ExpressionParser {
    // 인터페이스에 의존
    private final ExpressionProvider provider;

    // 구체 클래스가 아닌 인터페이스 타입 반환
    public Expression parse() {
        return parseOrExpression();
    }
}
```

## 주요 디자인 패턴

### Visitor Pattern

데이터 구조와 알고리즘을 분리합니다.

```java
// AST 노드는 데이터만 담당
public record IntegerLiteral(int value) implements Expression {
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitIntegerLiteral(this);
    }
}

// 처리 로직은 Visitor가 담당
public class SqlToStringVisitor implements AstVisitor<String> {
    public String visitIntegerLiteral(IntegerLiteral literal) {
        return String.valueOf(literal.value());
    }
}
```

**장점**:

- 새로운 연산 추가가 쉬움 (새 Visitor 구현)
- 관련 로직이 한 곳에 모임
- 타입 안정성 보장

### Strategy Pattern

런타임에 알고리즘을 선택합니다.

```java
public interface TokenReader {
    boolean canRead(CharStream stream);

    Token read(CharStream stream);
}

// 다양한 전략
class StringTokenReader implements TokenReader {
}

class IntegerTokenReader implements TokenReader {
}

class IdentifierTokenReader implements TokenReader {
}

// 런타임 선택
for(
TokenReader reader :readers){
        if(reader.

canRead(stream)){
        return reader.

read(stream);
    }
            }
```

### Builder Pattern

복잡한 객체를 단계적으로 생성합니다.

```java
SelectStatement stmt = SelectStatement.builder()
        .selectClause(selectClause)          // 필수
        .fromClause(fromClause)              // 선택
        .whereClause(whereClause)            // 선택
        .build();
```

## 클린 코드

### 작은 객체

```java
// 각 객체는 명확한 단일 책임
public final class WhitespaceSkipper {
    void skipAll(CharStream stream) {
        while (!stream.isAtEnd() && isWhitespace(stream.peek())) {
            stream.consume();
        }
    }
}
```

### Tell, Don't Ask

```java
// 나쁜 예: 상태를 묻고 직접 처리
if(token.getType() ==TokenType.IDENTIFIER){

processIdentifier(token.getValue());
        }

// 좋은 예: 객체에게 처리를 요청
        token.

accept(visitor);
```

### 명확한 이름

```java
// 의도가 명확한 이름
public class TokenCollector {
}         // 무엇을 하는지 명확

public Optional<Expression> parseLiteral() {
}  // 반환 타입과 역할 명확

public boolean isAtEnd() {
}           // 불린 메서드는 is/has/can으로 시작
```
