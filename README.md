# Simple SQL Parser

SELECT 문을 파싱하는 Java 기반 SQL 파서입니다. 파서 생성기를 사용하지 않고 직접 구현했습니다.

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.java.net/)
[![Build](https://img.shields.io/badge/Build-Gradle-blue.svg)](https://gradle.org/)

## 개요

이 프로젝트는 SQL SELECT 문을 토큰으로 분해하고, Abstract Syntax Tree(AST)로 변환하는 파서입니다.
교육 목적으로 만들어졌지만, 실제 사용 가능한 수준의 기능을 구현했습니다.

```java
String sql = "SELECT name, COUNT(*) FROM users WHERE age > 18 GROUP BY name";
SelectStatement ast = new SqlParser(sql).parse();
```

## 주요 특징

### 구현 방식
- ANTLR, JavaCC 같은 파서 생성기 없이 수작업 구현
- Lexer → Parser → AST → Visitor 구조
- 재귀 하강 파서(Recursive Descent Parser) 방식

### 지원 기능

#### SQL 절
- `SELECT` (DISTINCT 포함)
- `FROM` (다중 테이블)
- `WHERE`
- `GROUP BY` / `HAVING`
- `ORDER BY` (ASC/DESC)
- `LIMIT` / `OFFSET`

#### 연산자
- 논리: `AND`, `OR`, `NOT`
- 비교: `=`, `!=`, `<`, `<=`, `>`, `>=`
- 패턴: `LIKE`, `NOT LIKE`
- 범위: `BETWEEN`, `NOT BETWEEN`
- 목록: `IN`, `NOT IN`
- NULL: `IS NULL`, `IS NOT NULL`
- 산술: `+`, `-`, `*`, `/`

#### 함수
- 집계: `COUNT(*)`, `COUNT(column)`, `SUM`, `AVG`, `MIN`, `MAX`

#### 데이터 타입
- 정수, 실수, 문자열
- `TRUE`, `FALSE`, `NULL`
- 컬럼 참조, 별칭(AS)

## 아키텍처

```
src/main/java/com/jaeyeonling/
├── lexer/      # 문자열을 토큰으로 변환
├── parser/     # 토큰을 AST로 변환
├── ast/        # AST 노드 정의
├── visitor/    # AST 순회 및 처리
└── exception/  # 예외 처리
```

### Lexer
문자 스트림을 읽어 토큰으로 변환합니다.
- 키워드 인식 (SELECT, FROM, WHERE 등)
- 리터럴 파싱 (숫자, 문자열)
- 연산자 토큰화

### Parser
토큰 스트림을 분석해 AST를 생성합니다.
- 연산자 우선순위 처리
- 플러그인 방식의 연산자 파서 등록
- 구문 검증 및 에러 리포팅

### AST
파싱 결과를 트리 구조로 표현합니다.
- 불변 객체 (Java Record 활용)
- Visitor 패턴 지원
- 소스 위치 정보 포함

## 사용 예시

### 기본 파싱
```java
// SQL 파싱
String sql = "SELECT * FROM users WHERE status = 'active'";
SelectStatement stmt = new SqlParser(sql).parse();

// AST를 다시 SQL로 변환
SqlToStringVisitor visitor = new SqlToStringVisitor();
String reconstructed = stmt.accept(visitor);
```

### 복잡한 쿼리
```sql
SELECT 
    department,
    COUNT(*) as emp_count,
    AVG(salary) as avg_salary
FROM employees
WHERE hire_date > '2020-01-01'
    AND status IN ('active', 'probation')
GROUP BY department
HAVING COUNT(*) > 5
ORDER BY avg_salary DESC
LIMIT 10
```

### 커스텀 Visitor 구현
```java
class ColumnExtractor extends AbstractAstVisitor<Set<String>> {
    private Set<String> columns = new HashSet<>();
    
    @Override
    public Set<String> visitColumnReference(ColumnReference ref) {
        columns.add(ref.columnName());
        return columns;
    }
}

// 사용
Set<String> usedColumns = stmt.accept(new ColumnExtractor());
```

## 에러 처리

파싱 실패 시 상세한 에러 메시지를 제공합니다.

```java
// 잘못된 SQL
"SELECT * FORM users"

// 에러 메시지
"예상치 못한 토큰 'FORM'이(가) 있습니다.
위치: 1행 10열
SQL 문법을 확인해주세요."
```

## 제한 사항

현재 지원하지 않는 기능:
- JOIN 구문
- 서브쿼리
- UNION, INTERSECT, EXCEPT
- 윈도우 함수
- CASE WHEN 표현식
- 복합 수식 표현식 `(col1 + col2) * 2`
- 날짜/시간 함수, 문자열 함수

## 빌드 및 테스트

```bash
# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 테스트 리포트
open build/reports/tests/test/index.html
```

## 프로젝트 구성

- **100+ 클래스**: 각 역할별로 세분화된 클래스 설계
- **테스트 클래스**: 단위 테스트 및 통합 테스트
- **외부 의존성 없음**: 순수 Java만 사용

## 문서

- [아키텍처 설명](ARCHITECTURE.md) - 내부 구조와 설계 결정
- [OOP 학습 노트](OOP_LEARNING.md) - 적용된 객체지향 패턴
- [NULL 처리 가이드](NULL_HANDLING_GUIDE.md) - NULL 값 처리 방식

