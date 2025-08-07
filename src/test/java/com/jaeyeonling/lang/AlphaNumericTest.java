package com.jaeyeonling.lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AlphaNumeric 테스트")
class AlphaNumericTest {

    @ParameterizedTest
    @ValueSource(chars = {'a', 'Z', '0', '9'})
    @DisplayName("알파벳과 숫자는 유효하다")
    void testValidAlphaNumeric(final char c) {
        // when
        AlphaNumeric an = new AlphaNumeric(c);

        // then
        assertThat(an.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'!', '@', ' ', '_'})
    @DisplayName("알파벳이나 숫자가 아닌 문자는 예외를 발생시킨다")
    void testInvalidAlphaNumeric(final char c) {
        assertThatThrownBy(() -> new AlphaNumeric(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("알파벳 또는 숫자가 아닙니다");
    }

    @Test
    @DisplayName("알파벳 여부를 확인한다")
    void testIsAlpha() {
        assertThat(new AlphaNumeric('A').isAlpha()).isTrue();
        assertThat(new AlphaNumeric('5').isAlpha()).isFalse();
    }

    @Test
    @DisplayName("숫자 여부를 확인한다")
    void testIsDigit() {
        assertThat(new AlphaNumeric('5').isDigit()).isTrue();
        assertThat(new AlphaNumeric('A').isDigit()).isFalse();
    }


    @Test
    @DisplayName("isAlphaNumeric 정적 메서드를 테스트한다")
    void testIsAlphaNumericMethod() {
        assertThat(AlphaNumeric.isAlphaNumeric('a')).isTrue();
        assertThat(AlphaNumeric.isAlphaNumeric('5')).isTrue();
        assertThat(AlphaNumeric.isAlphaNumeric('!')).isFalse();
    }
}
