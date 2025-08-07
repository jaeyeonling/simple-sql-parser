# Simple SQL Parser

SELECT 문 파싱을 통해 객체지향 프로그래밍을 학습하기 위한 교육용 프로젝트입니다.

파서 생성기를 사용하지 않고 순수 Java로 직접 구현하여, 파싱 과정의 내부 동작을 이해할 수 있습니다.

## 핵심 학습 목표

- **파서 구현**: Lexer → Parser → AST → Visitor 아키텍처
- **객체지향 설계**: SOLID 원칙, 디자인 패턴 실제 적용
- **클린 코드**: 작은 객체와 명확한 책임

## 사용법

```java
// SQL 파싱
String sql = "SELECT name, age FROM users WHERE age > 18";
SelectStatement stmt = new SqlParser(sql).parse();

// AST 활용
SqlToStringVisitor visitor = new SqlToStringVisitor();
String reconstructed = stmt.accept(visitor);
```

## 지원 기능

### ✅ 구현 완료

- **SQL 절**: SELECT, FROM, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT (OFFSET 포함)
- **논리 연산자**: AND, OR, NOT
- **비교 연산자**: =, !=, <, <=, >, >=
- **특수 연산자**: LIKE, NOT LIKE, IN, NOT IN, BETWEEN, NOT BETWEEN, IS NULL, IS NOT NULL
- **산술 연산자**: +, -, *, / (단일 연산만, 복합 수식 미지원)
- **리터럴**: 정수, 실수, 문자열, TRUE, FALSE, NULL
- **집계 함수**: COUNT(*), COUNT(column), SUM, AVG, MIN, MAX
- **기타**: 컬럼 별칭(AS), 테이블 별칭, DISTINCT, *, 복수 테이블 FROM

### ❌ 미구현

- **표현식**: 복잡한 수식 `(col1 + col2) * 2`
- **함수**: 날짜/시간 함수(YEAR, MONTH 등), 문자열 함수(UPPER, LOWER 등)
- **조인**: INNER/LEFT/RIGHT JOIN
- **서브쿼리**: IN (SELECT ...), EXISTS
- **스키마**: `schema.table`, `table.column` 점(.) 문법
- **기타**: UNION, WITH, CASE WHEN, 윈도우 함수

## 프로젝트 구조

```
src/main/java/com/jaeyeonling/
├── lexer/      # 토큰화 (문자열 → 토큰)
├── parser/     # 구문 분석 (토큰 → AST)
├── ast/        # Abstract Syntax Tree 노드
├── visitor/    # AST 순회 및 처리
└── exception/  # 예외 계층 구조
```

## 관련 문서

- [아키텍처](ARCHITECTURE.md) - 설계 원칙과 구조
- [OOP 학습](OOP_LEARNING.md) - 객체지향 개념 적용 사례
