package com.patrickneubauer.examples.streaming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.patrickneubauer.examples.streaming.consumer.FlinkFruitConsumer;
import com.patrickneubauer.examples.streaming.producer.KafkaFruitProducer;

public class FruitRunner {
	 
    private static final Logger logger = LogManager.getLogger(FruitRunner.class);

    public static void main(final String... args) {
        String APP_GOAL = System.getenv("APP_GOAL") != null ?
                System.getenv("APP_GOAL") : "producer";

        logger.info("Kafka Topic: {}", Settings.APP_KAFKA_TOPIC);
        logger.info("Kafka Server: {}", Settings.APP_KAFKA_SERVER);
        logger.info("Zookeeper Server: {}", Settings.APP_ZOOKEEPER_SERVER);
        logger.info("GOAL: {}", APP_GOAL);

        switch (APP_GOAL.toLowerCase()) {
            case "producer":
                KafkaFruitProducer.main();
                break;
            case "consumer.flink":
                FlinkFruitConsumer.main();
                break;
            default:
                logger.error("No valid goal to run.");
                break;
        }
    }// main
    
}// FruitRunner
