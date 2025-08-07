# Null 처리 가이드라인

이 프로젝트에서는 null 처리에 대해 다음과 같은 일관된 규칙을 따릅니다.

## 기본 원칙

1. **인스턴스 변수**: `@Nullable` 애노테이션 사용
2. **반환값**: `Optional` 사용
3. **파라미터**: `@Nullable` 또는 명시적 null 체크

## 상세 규칙

### 1. 인스턴스 변수

null일 수 있는 인스턴스 변수는 `@Nullable` 애노테이션을 사용합니다.

```java
public class ColumnReference {
    @Nullable
    private final String tableName;  // null 가능
    private final String columnName;  // null 불가
}
```

### 2. 반환값

null을 반환할 수 있는 메서드는 `Optional`을 사용합니다.

```java
// 좋은 예
public Optional<String> alias() {
    return Optional.ofNullable(alias);
}

// 나쁜 예 (사용하지 말 것)
@Nullable
public String getAlias() {
    return alias;
}
```

### 3. 파라미터

null을 받을 수 있는 파라미터는 `@Nullable` 애노테이션을 사용합니다.

```java
public ColumnReference(
    @Nullable String tableName,  // null 가능
    String columnName             // null 불가
) {
    this.tableName = tableName;
    this.columnName = Objects.requireNonNull(columnName, "columnName은 null일 수 없습니다");
}
```

### 4. 컬렉션

컬렉션은 절대 null을 반환하지 않습니다. 빈 컬렉션을 반환합니다.

```java
// 좋은 예
public List<SelectItem> selectItems() {
    return Collections.unmodifiableList(selectItems);
}

// 나쁜 예
@Nullable
public List<SelectItem> getSelectItems() {
    return selectItems.isEmpty() ? null : selectItems;
}
```

### 5. Builder 패턴

Builder에서는 build() 시점에 null 체크를 수행합니다.

```java
public static class Builder {
    @Nullable
    private FromClause fromClause;  // 선택적
    private SelectClause selectClause;  // 필수
    
    public SelectStatement build() {
        Objects.requireNonNull(selectClause, "SELECT 절은 필수입니다");
        // fromClause는 null 가능
        return new SelectStatement(this);
    }
}
```

## 예외 사항

### Deprecated 클래스

`@Deprecated` 클래스에서는 이전 버전과의 호환성을 위해 규칙을 완화할 수 있습니다.

### 테스트 코드

테스트 코드에서는 가독성을 위해 `assertThat(value).isPresent()` 대신 직접 null 체크를 사용할 수 있습니다.

## 도구 지원

### IntelliJ IDEA 설정

1. Settings → Editor → Inspections
2. "Nullable and NotNull problems" 활성화
3. "@Nullable/@NotNull annotations" 설정

### Gradle 설정

```gradle
dependencies {
    implementation 'org.jetbrains:annotations:24.0.0'
}
```

## 마이그레이션 전략

기존 코드를 점진적으로 이 가이드라인에 맞게 수정합니다:

1. 새로운 코드는 이 규칙을 따름
2. 기존 코드 수정 시 해당 클래스 전체를 규칙에 맞게 수정
3. 주요 리팩토링 시 패키지 단위로 수정