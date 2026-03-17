package com.logistics.company.infrastructure.messaging;

import com.logistics.company.application.port.out.CompanyEventPublisher;
import com.logistics.shared.domain.event.DomainEvent;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka adapter implementing the {@link CompanyEventPublisher} outbound port.
 *
 * All domain events are published to the {@code logistics.company-events}
 * topic keyed by {@code aggregateId} (company UUID).  Keying by ID ensures
 * that all events for the same company are processed in order within a
 * single Kafka partition, which is critical for status-change sequencing.
 *
 * Failures are logged with the event ID so they can be replayed from a
 * dead-letter topic.  For production use a transactional outbox pattern
 * (Debezium CDC) should replace this fire-and-forget approach.
 */
@Component
@Observed(name = "company.kafka.publisher")
public class KafkaCompanyEventPublisher implements CompanyEventPublisher {

    private static final Logger log  = LoggerFactory.getLogger(KafkaCompanyEventPublisher.class);
    public  static final String TOPIC = "logistics.company-events";

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public KafkaCompanyEventPublisher(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(DomainEvent event) {
        CompletableFuture<SendResult<String, DomainEvent>> future =
                kafkaTemplate.send(TOPIC, event.getAggregateId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event eventId={} type={} aggregateId={}",
                        event.getEventId(), event.getEventType(), event.getAggregateId(), ex);
            } else {
                log.debug("Published event eventId={} type={} partition={} offset={}",
                        event.getEventId(), event.getEventType(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
