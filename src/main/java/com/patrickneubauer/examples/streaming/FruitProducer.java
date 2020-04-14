package com.patrickneubauer.examples.streaming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.patrickneubauer.examples.streaming.consumer.FlinkFruitConsumer;
import com.patrickneubauer.examples.streaming.producer.KafkaFruitProducer;

public class FruitProducer {
	 
    private static final Logger logger = LogManager.getLogger(FruitProducer.class);

    public static void main(final String... args) {
        
		KafkaFruitProducer.main();
        
    }// main
    
}// FruitRunner
