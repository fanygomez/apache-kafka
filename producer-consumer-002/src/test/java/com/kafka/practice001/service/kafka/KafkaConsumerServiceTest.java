package com.kafka.practice001.service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaConsumerServiceTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void consume() {
        KafkaConsumerService consumerService = mock(KafkaConsumerService.class);
        String message = "Test Message";

        consumerService.consume(message);

        verify(consumerService).consume(message);
    }
}