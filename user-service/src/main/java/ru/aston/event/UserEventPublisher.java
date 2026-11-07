package ru.aston.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(UserEventPublisher.class);
    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserEventPublisher(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(UserEvent event) {
        String key = String.valueOf(event.id());
        kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        logger.error("Can't send message {} for user with id={}",
                                event.type(), event.id(), ex);
                    } else {
                        logger.info("Event {} send to user with id={} (partition={}, offset={})",
                                event.type(), event.id(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
