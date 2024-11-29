package com.kafka.practice001.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConfiguration {
    public Map<String, Object> consumerProps() {
        Map<String, Object>props=new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }
    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true); // Leer  batch
        factory.setConcurrency(3);// Con esto se tendran 3 hilos consumiendo mensajes de forma concurrente
        return factory;
    }
    private Map<String, Object> producerProps() {
        Map<String, Object> props=new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    // KafkaTemplate: Se utiliza para producir los mensajes
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<String, String>(producerProps());
        // Metricas utilizando Micrometer Paso 3: A;andir listener de tipo MicrometerProducerListener , para incluir meterRegistry() quien estará dando seguimiento a las métricas
        producerFactory.addListener(new MicrometerProducerListener<String,String>(meterRegistry()));
        KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory);
        return template;
    }
    // Metricas utilizando Micrometer: Paso #2 configurar ben de tipo MetricRegistry
    @Bean
    public MeterRegistry meterRegistry() {
        PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry (PrometheusConfig.DEFAULT);
        return prometheusMeterRegistry;
    }
}
