package com.kafka.practice001;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class ProducerConsumer002Application{
	@Autowired
	private KafkaTemplate<String, String > kafkaTemplate;
	@Autowired
	private KafkaListenerEndpointRegistry registry; // Para habilitar el consumo de mensajes
	@Autowired
	private MeterRegistry meterRegistry;
	private static final Logger log = LoggerFactory.getLogger(ProducerConsumer002Application.class);

	/**
	 * id = "devTest", autoStartup = "false": Util para pausar por defecto el consumo de mensaje
	 *
	 */
	@KafkaListener(id = "devTest", autoStartup = "true",topics ="test-topic",containerFactory = "kafkaListenerContainerFactory", groupId ="test-group",
			properties = {
			"max.poll.interval.ms:4000", "max.poll.records:50"
	})
	// Consumiendo mensajes completos List<ConsumerRecord<String, String>>
	public void listen(List<ConsumerRecord<String, String>> messages) {
		log.info("Batch start");
//		messages.forEach(message -> log.info("Received Message in test-group: Offset {}, Partition= {}, Key ={}, Value = {}", message.offset(), message.partition(),message.key(),message.value()));
		log.info("Batch completed");
	}

	public static void main(String[] args) {
		SpringApplication.run(ProducerConsumer002Application.class, args);
	}
	@Scheduled(fixedDelay = 2000, initialDelay = 100)
	public void sendKafkaMessages() {
		log.info("Sending messages ");
		for(int i= 0;i< 200;i++) {
			kafkaTemplate.send("test-topic",String.valueOf(i),String.format("init: Sample message %d", i));
		}
	}
	// Mostrando métricas
	@Scheduled(fixedDelay = 2000, initialDelay = 500)
	public void messageCountMetric() {
		var metrics = meterRegistry.getMeters();
		// Métricas disponibles
		for(Meter meter: metrics) {
			log.info("Metric {} ",meter.getId());
		}
		// Si no hay métricas disponibles se producirá una MeterNotFoundException
		double count = meterRegistry.get("kafka.producer.record.send.total").functionCounter().count();
		log.info("Count {} ",count);
	}
	// Pausar y reanudando el consumo de mensajes
//	@Override
//	public void run(String... args) throws Exception {
//		for (int i = 0; i < 100; i++) {
//			kafkaTemplate.send("test-topic",String.valueOf(i),String.format("init: Sample message %d", i));
//		}
//		Thread.sleep(5000);
//		log.info("Starting consuming messages");
//		Objects.requireNonNull(registry.getListenerContainer("devTest")).start();
//		Thread.sleep(5000);
//		Objects.requireNonNull(registry.getListenerContainer("devTest")).stop();
//		log.info("Stop consuming messages");
//	}
	// Ejemplo: Producer/Enviar mensajes de forma síncrona
//	@Override
//	public void run(String... args) throws Exception {
//		kafkaTemplate.send("test-topic","init: Sample message").get();
//		kafkaTemplate.send(new ProducerRecord<String,String>("test-topic","init2: Sample message with timeout")).get(10, TimeUnit.SECONDS);
//
//	}

// Ejemplo: Callbacks con Spring Async
//	@Override
//	public void run(String... args) throws Exception {
//		CompletableFuture<SendResult<String, String>> future =  kafkaTemplate.send("test-topic","init: Sample message");
//		future.thenAccept(result -> {
//			log.info("Message send: {}", result.getRecordMetadata().offset());
//		}).exceptionally(ex -> {
//			log.error("Error sending message", ex);
//			return null;
//		});
//	}
}
