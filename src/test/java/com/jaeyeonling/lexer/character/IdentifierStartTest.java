package com.jaeyeonling.lexer.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("IdentifierStart 테스트")
class IdentifierStartTest {

    @ParameterizedTest
    @ValueSource(chars = {'a', 'Z', '_'})
    @DisplayName("알파벳과 언더스코어는 유효한 식별자 시작 문자이다")
    void testValidIdentifierStart(final char c) {
        // when
        final IdentifierStart is = new IdentifierStart(c);

        // then
        assertThat(is.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'0', '9', '!', '@'})
    @DisplayName("유효하지 않은 식별자 시작 문자는 예외를 발생시킨다")
    void testInvalidIdentifierStart(final char c) {
        assertThatThrownBy(() -> new IdentifierStart(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("식별자의 시작 문자로 사용할 수 없습니다");
    }


    @Test
    @DisplayName("isIdentifierStart 정적 메서드를 테스트한다")
    void testIsIdentifierStartMethod() {
        assertThat(IdentifierStart.isIdentifierStart('a')).isTrue();
        assertThat(IdentifierStart.isIdentifierStart('Z')).isTrue();
        assertThat(IdentifierStart.isIdentifierStart('_')).isTrue();
        assertThat(IdentifierStart.isIdentifierStart('5')).isFalse();
    }

    @Test
    @DisplayName("isNotIdentifierStart 정적 메서드를 테스트한다")
    void testIsNotIdentifierStartMethod() {
        assertThat(IdentifierStart.isNotIdentifierStart('5')).isTrue();
        assertThat(IdentifierStart.isNotIdentifierStart('a')).isFalse();
    }
}
