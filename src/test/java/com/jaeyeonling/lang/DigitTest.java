package com.jaeyeonling.lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Digit 테스트")
class DigitTest {

    @ParameterizedTest
    @ValueSource(chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'})
    @DisplayName("0-9는 유효한 숫자이다")
    void testValidDigit(final char c) {
        // when
        Digit digit = new Digit(c);

        // then
        assertThat(digit.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'A', 'a', '!', ' ', '@'})
    @DisplayName("숫자가 아닌 문자는 예외를 발생시킨다")
    void testInvalidDigit(final char c) {
        assertThatThrownBy(() -> new Digit(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효한 숫자가 아닙니다");
    }

    @Test
    @DisplayName("isDigit 정적 메서드를 테스트한다")
    void testIsDigitMethod() {
        assertThat(Digit.isDigit('5')).isTrue();
        assertThat(Digit.isDigit('A')).isFalse();
    }

    @Test
    @DisplayName("isNotDigit 정적 메서드를 테스트한다")
    void testIsNotDigitMethod() {
        assertThat(Digit.isNotDigit('A')).isTrue();
        assertThat(Digit.isNotDigit('5')).isFalse();
    }
}
