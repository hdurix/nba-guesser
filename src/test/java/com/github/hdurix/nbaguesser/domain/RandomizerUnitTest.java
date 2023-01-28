package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.MissingMandatoryValueException;
import com.github.hdurix.nbaguesser.error.domain.NumberValueTooLowException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.github.hdurix.nbaguesser.domain.RandomizerFixture.makeTenNumbers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomizerUnitTest {
  @Test
  void shouldNotRandomizeLessThanTen() {
    assertThatThrownBy(() -> new Randomizer().tenOutOf(9))
      .isInstanceOf(NumberValueTooLowException.class)
      .hasMessageContaining("max")
      .hasMessageContaining("at least 10")
      .hasMessageContaining("was 9");
  }

  @Test
  void shouldRandomize() {
    int[] randomNumbers = new Randomizer().tenOutOf( 20);

    assertThat(randomNumbers)
      .hasSize(10)
      .isNotEqualTo(new Randomizer().tenOutOf( 20));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldNotGetOneOfEmptyArray(int[] numbers) {
    assertThatThrownBy(() -> new Randomizer().oneOf(numbers))
        .isInstanceOf(MissingMandatoryValueException.class)
        .hasMessageContaining("numbers");
  }

  @Test
  void shouldGetRandomNumber() {
    int[] numbers = makeTenNumbers();

    int randomNumber = new Randomizer().oneOf(numbers);

    assertThat(numbers).contains(randomNumber);
  }
}
