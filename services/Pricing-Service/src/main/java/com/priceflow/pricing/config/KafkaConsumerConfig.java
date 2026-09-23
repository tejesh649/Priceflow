package com.priceflow.pricing.config;

import com.priceflow.events.costrequest.CostRequestApprovedEvent;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, CostRequestApprovedEvent> consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "pricing-service"
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
                "schema.registry.url",
                "http://localhost:8085/apis/ccompat/v7"
        );

        config.put(
                "specific.avro.reader",
                true
        );
        config.put(
                "avro.use.logical.type.converters",
                false
        );

        return new DefaultKafkaConsumerFactory<String, CostRequestApprovedEvent>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CostRequestApprovedEvent>
    kafkaListenerContainerFactory() {

        var factory =
                new ConcurrentKafkaListenerContainerFactory<String, CostRequestApprovedEvent>();

        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}