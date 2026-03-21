package com.github.hdurix.nbaguesser.infrastructure.primary.webhook;

import com.github.hdurix.nbaguesser.application.QuizAnswerApplicationService;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component("telegramWebhook")
class TelegramWebhookFunction implements Function<String, String> {

  private static final Logger log = LoggerFactory.getLogger(TelegramWebhookFunction.class);

  private final ObjectMapper objectMapper;
  private final QuizAnswerApplicationService quizAnswerApplicationService;

  TelegramWebhookFunction(ObjectMapper objectMapper, QuizAnswerApplicationService quizAnswerApplicationService) {
    this.objectMapper = objectMapper;
    this.quizAnswerApplicationService = quizAnswerApplicationService;
  }

  @Override
  public String apply(String input) {
    log.info("Received webhook event");
    log.debug("Webhook payload: {}", input);

    try {
      TelegramUpdate update = objectMapper.readValue(input, TelegramUpdate.class);
      handleUpdate(update);
    } catch (Exception e) {
      log.error("Failed to process webhook event", e);
    }

    return "OK";
  }

  private void handleUpdate(TelegramUpdate update) {
    if (update.isPollAnswer()) {
      handlePollAnswer(update);
    } else if (update.isStatsCommand()) {
      handleStatsCommand(update);
    } else {
      log.debug("Ignoring unhandled update type for update_id: {}", update.update_id());
    }
  }

  private void handlePollAnswer(TelegramUpdate update) {
    TelegramUpdate.PollAnswer pollAnswer = update.poll_answer();
    log.info(
      "Poll answer received: poll_id={}, user_id={}, options={}",
      pollAnswer.poll_id(),
      pollAnswer.user().id(),
      pollAnswer.option_ids()
    );

    if (pollAnswer.option_ids().isEmpty()) {
      log.debug("Ignoring retracted poll answer for poll_id={}", pollAnswer.poll_id());
      return;
    }

    quizAnswerApplicationService.save(
      pollAnswer.poll_id(),
      pollAnswer.user().id(),
      pollAnswer.user().first_name(),
      pollAnswer.option_ids().getFirst()
    );
  }

  private void handleStatsCommand(TelegramUpdate update) {
    log.info("Stats command received from user_id={} in chat_id={}", update.message().from().id(), update.message().chat().id());
    // TODO HDU-12: query DynamoDB and return stats
  }
}
