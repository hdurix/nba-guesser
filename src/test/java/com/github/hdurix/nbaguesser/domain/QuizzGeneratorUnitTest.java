package com.github.hdurix.nbaguesser.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.makePlayer;
import static com.github.hdurix.nbaguesser.domain.PlayerFixture.makePlayersWithIds;
import static com.github.hdurix.nbaguesser.domain.PlayerFixture.makeTwentyPlayers;
import static com.github.hdurix.nbaguesser.domain.RandomizerFixture.makeTenNumbers;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizzGeneratorUnitTest {
  @Mock
  private Players players;
  @Mock
  private Quizzes quizzes;
  @Mock
  private Randomizer randomizer;

  @InjectMocks
  private QuizzGenerator quizzGenerator;

  @Test
  void shouldGenerate() {
    int[] playerIndexes = makeTenNumbers();
    when(players.listAll()).thenReturn(makeTwentyPlayers());
    when(randomizer.tenOutOf(20)).thenReturn(playerIndexes);
    when(randomizer.oneOf(playerIndexes)).thenReturn(2);

    quizzGenerator.generate();

    verify(quizzes).send(makePlayersWithIds(playerIndexes), makePlayer());
  }
}
