# 아키텍처

## 설계 원칙

### 1. 책임 분리

각 컴포넌트는 단일 책임만 가집니다.

- **Lexer**: 문자열을 토큰으로 변환
- **Parser**: 토큰을 AST로 변환
- **AST**: 파싱된 구조를 표현
- **Visitor**: AST 순회 및 처리

### 2. 확장 가능한 구조

새로운 기능 추가 시 기존 코드 수정을 최소화합니다.

```java
// 새로운 연산자 추가 예시
public class ModuloOperatorParser implements OperatorParser {
    @Override
    public boolean canParse(TokenStream stream) {
        return stream.check(TokenType.PERCENT);
    }
}

// Registry에 등록만 하면 동작
registry.register(new ModuloOperatorParser());
```

### 3. 불변성

모든 AST 노드는 불변 객체로 설계하여 안전성을 보장합니다.

```java
public record SelectStatement(
    SelectClause selectClause,
    Optional<FromClause> fromClause,
    Optional<WhereClause> whereClause,
    // ...
) implements Statement { }
```

## 핵심 컴포넌트

### Lexer (토큰화)

```
"SELECT name FROM users"
         ↓
[SELECT] [name] [FROM] [users]
```

**TokenReader 전략 패턴**으로 다양한 토큰 타입을 처리합니다.

### Parser (구문 분석)

**Recursive Descent Parser**로 구현되어 문법 규칙과 코드가 1:1 대응됩니다.

```java
// 문법: expression → term ('+' term)*
private Expression parseAdditive() {
    Expression left = parseMultiplicative();
    while (match(PLUS, MINUS)) {
        Token op = previous();
        Expression right = parseMultiplicative();
        left = new BinaryOperatorExpression(left, op, right);
    }
    return left;
}
```

### AST (Abstract Syntax Tree)

```
SELECT name FROM users WHERE age > 18
                ↓
         SelectStatement
         ├── SelectClause
         │   └── ColumnReference("name")
         ├── FromClause
         │   └── Table("users")
         └── WhereClause
             └── BinaryOperatorExpression(">")
                 ├── ColumnReference("age")
                 └── IntegerLiteral(18)
```

### Visitor Pattern

데이터 구조(AST)와 알고리즘(처리 로직)을 분리합니다.

```java
public interface AstVisitor<T> {
    T visitSelectStatement(SelectStatement stmt);
    T visitColumnReference(ColumnReference column);
    // ...
}

// 사용 예: SQL 재구성
public class SqlToStringVisitor implements AstVisitor<String> {
    @Override
    public String visitSelectStatement(SelectStatement stmt) {
        return stmt.selectClause().accept(this) + 
               stmt.fromClause().map(f -> " " + f.accept(this)).orElse("");
    }
}
```

## 패키지 구조

```
com.jaeyeonling/
├── lexer/          # 토큰화
│   ├── Token, TokenType
│   └── reader/     # TokenReader 구현체들
├── parser/         # 구문 분석
│   ├── SqlParser   # 메인 파서
│   └── expression/ # 표현식 파서들
├── ast/            # AST 노드
│   ├── statement/  # SELECT 문
│   ├── clause/     # SQL 절
│   └── expression/ # 표현식
└── visitor/        # Visitor 패턴
```

## 확장 방법

### 새로운 연산자 추가

1. AST 노드 생성: `ModuloExpression implements Expression`
2. Parser 구현: `ModuloOperatorParser implements OperatorParser`
3. Registry 등록: `registry.register(new ModuloOperatorParser())`
4. Visitor 메서드 추가: `visitModuloExpression(ModuloExpression expr)`

### 새로운 SQL 기능 추가

1. TokenType에 키워드 추가
2. AST 노드 클래스 생성
3. Parser에 파싱 로직 추가
4. Visitor 메서드 구현
