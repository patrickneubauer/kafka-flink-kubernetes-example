package com.patrickneubauer.examples.streaming.producer;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.patrickneubauer.examples.streaming.Settings;

import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KafkaFruitProducer {
   
	private static final Logger logger = LogManager.getLogger(KafkaFruitProducer.class);

    private static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Settings.APP_KAFKA_SERVER);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaFruitProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }// createProducer

    private static void createTopic() {
        int sessionTimeoutMs = 10 * 1000;
        int connectionTimeoutMs = 8 * 1000;

        ZkClient zkClient = new ZkClient(
                Settings.APP_ZOOKEEPER_SERVER,
                sessionTimeoutMs,
                connectionTimeoutMs,
                ZKStringSerializer$.MODULE$);

        boolean isSecureKafkaCluster = false;
        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(Settings.APP_ZOOKEEPER_SERVER), isSecureKafkaCluster);

        int partitions = 1;
        int replication = 1;

        // Add topic configuration here
        Properties topicConfig = new Properties();
        if (!AdminUtils.topicExists(zkUtils, Settings.APP_KAFKA_TOPIC)) {
            AdminUtils.createTopic(zkUtils, Settings.APP_KAFKA_TOPIC, partitions, replication, topicConfig, RackAwareMode.Safe$.MODULE$);
            logger.info("Topic {} created.", Settings.APP_KAFKA_TOPIC);
        } else {
            logger.info("Topic {} already exists.", Settings.APP_KAFKA_TOPIC);
        }

        zkClient.close();
    }// createTopic
    
    public static void main(final String... args) {
        // Create topic
//        createTopic(); // may require load balancer to access zookeeper

        String[] words = new String[]{"apple", "banana", "blueberry", "lemon", "pineapple", "orange", "coconut", "cranberry", "kiwifruit", "raspberry"};
        Random ran = new Random(System.currentTimeMillis());

        final Producer<String, String> producer = createProducer();
        int APP_PRODUCER_INTERVAL = System.getenv("APP_PRODUCER_INTERVAL") != null ?
                Integer.parseInt(System.getenv("APP_PRODUCER_INTERVAL")) : 100;

        try {
            while (true) {
                String word = words[ran.nextInt(words.length)];
                String uuid = UUID.randomUUID().toString();

                ProducerRecord<String, String> record = new ProducerRecord<>(Settings.APP_KAFKA_TOPIC, uuid, word);
                RecordMetadata metadata = producer.send(record).get();

                logger.info("Sent ({}, {}) to topic {} @ {}.", uuid, word, Settings.APP_KAFKA_TOPIC, metadata.timestamp());

                Thread.sleep(APP_PRODUCER_INTERVAL);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("An error occurred.", e);
        } finally {
            producer.flush();
            producer.close();
        }
    }// main
    
}// KafkaFruitProducer
