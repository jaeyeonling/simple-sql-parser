package com.jaeyeonling.lexer.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("IdentifierChar 테스트")
class IdentifierCharTest {

    @ParameterizedTest
    @ValueSource(chars = {'a', 'Z', '0', '9', '_'})
    @DisplayName("알파벳, 숫자, 언더스코어는 유효한 식별자 문자이다")
    void testValidIdentifierChar(final char c) {
        // when
        IdentifierChar ic = new IdentifierChar(c);

        // then
        assertThat(ic.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'!', '@', ' ', '-'})
    @DisplayName("유효하지 않은 식별자 문자는 예외를 발생시킨다")
    void testInvalidIdentifierChar(final char c) {
        assertThatThrownBy(() -> new IdentifierChar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("식별자에 사용할 수 없습니다");
    }


    @Test
    @DisplayName("isIdentifierChar 정적 메서드를 테스트한다")
    void testIsIdentifierCharMethod() {
        assertThat(IdentifierChar.isIdentifierChar('a')).isTrue();
        assertThat(IdentifierChar.isIdentifierChar('5')).isTrue();
        assertThat(IdentifierChar.isIdentifierChar('_')).isTrue();
        assertThat(IdentifierChar.isIdentifierChar('!')).isFalse();
    }
}
