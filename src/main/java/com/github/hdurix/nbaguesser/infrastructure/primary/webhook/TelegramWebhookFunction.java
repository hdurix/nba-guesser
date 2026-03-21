package com.github.hdurix.nbaguesser.infrastructure.primary.webhook;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component("telegramWebhook")
class TelegramWebhookFunction implements Function<String, String> {

  private static final Logger log = LoggerFactory.getLogger(TelegramWebhookFunction.class);

  private final ObjectMapper objectMapper;

  TelegramWebhookFunction(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
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
    log.info(
      "Poll answer received: poll_id={}, user_id={}, options={}",
      update.poll_answer().poll_id(),
      update.poll_answer().user().id(),
      update.poll_answer().option_ids()
    );
    // TODO HDU-12: persist poll answer to DynamoDB
  }

  private void handleStatsCommand(TelegramUpdate update) {
    log.info("Stats command received from user_id={} in chat_id={}", update.message().from().id(), update.message().chat().id());
    // TODO HDU-12: query DynamoDB and return stats
  }
}
