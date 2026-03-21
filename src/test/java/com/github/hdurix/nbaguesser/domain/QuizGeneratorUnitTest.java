package com.github.hdurix.nbaguesser.domain;

import static com.github.hdurix.nbaguesser.domain.PlayerFixture.makePlayer;
import static com.github.hdurix.nbaguesser.domain.PlayerFixture.makePlayersWithIds;
import static com.github.hdurix.nbaguesser.domain.PlayerFixture.makeTwentyPlayers;
import static com.github.hdurix.nbaguesser.domain.QuizResultFixture.makeQuizResult;
import static com.github.hdurix.nbaguesser.domain.RandomizerFixture.makeTenNumbers;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.hdurix.nbaguesser.UnitTest;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class QuizGeneratorUnitTest {

  @Mock
  private Players players;

  @Mock
  private Quizzes quizzes;

  @Mock
  private QuizResults quizResults;

  @Mock
  private Randomizer randomizer;

  @InjectMocks
  private QuizGenerator quizGenerator;

  @Test
  void shouldGenerate() {
    int[] playerIndexes = makeTenNumbers();
    Collection<Player> twentyPlayers = makeTwentyPlayers();
    when(players.listAll()).thenReturn(twentyPlayers);
    when(randomizer.tenOutOf(20)).thenReturn(playerIndexes);
    when(randomizer.oneOf(makePlayersWithIds(playerIndexes))).thenReturn(makePlayer());

    QuizResult expectedResult = makeQuizResult();
    when(quizzes.send(makePlayersWithIds(playerIndexes), makePlayer())).thenReturn(expectedResult);

    quizGenerator.generate();

    verify(quizResults).save(expectedResult);
  }
}
