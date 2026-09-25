package com.priceflow.notification_service.service;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DltReplayService {

    private static final String SOURCE_TOPIC =
            "priceflow.cost-request.events.DLT";

    private static final String TARGET_TOPIC =
            "priceflow.cost-request.events";

    private static final String REPLAY_GROUP =
            "notification-dlt-replay";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public int replayAll() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                REPLAY_GROUP
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                KafkaAvroDeserializer.class
        );

        config.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false
        );

        config.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        config.put(
                "schema.registry.url",
                "http://localhost:8085/apis/ccompat/v7"
        );

        config.put(
                "specific.avro.reader",
                true
        );

        int replayedCount = 0;

        try (KafkaConsumer<String, SpecificRecord> consumer =
                     new KafkaConsumer<>(config)) {

            consumer.subscribe(List.of(SOURCE_TOPIC));

            var records = consumer.poll(Duration.ofSeconds(5));

            for (var record : records) {

                log.info(
                        "Replaying DLT record: partition={}, offset={}, key={}, eventType={}",
                        record.partition(),
                        record.offset(),
                        record.key(),
                        record.value().getClass().getSimpleName()
                );

                kafkaTemplate
                        .send(
                                TARGET_TOPIC,
                                record.key(),
                                record.value()
                        )
                        .join();

                replayedCount++;
            }

            if (replayedCount > 0) {
                consumer.commitSync();
            }
        }

        log.info(
                "DLT replay completed. replayedCount={}",
                replayedCount
        );

        return replayedCount;
    }
}