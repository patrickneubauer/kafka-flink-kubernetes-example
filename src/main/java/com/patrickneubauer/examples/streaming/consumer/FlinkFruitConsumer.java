package com.patrickneubauer.examples.streaming.consumer;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.patrickneubauer.examples.streaming.Settings;

import java.util.Properties;

public class FlinkFruitConsumer {
    private static final Logger logger = LogManager.getLogger(FlinkFruitConsumer.class);

    public static void main(final String... args) {
        // Create execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(); // works with port-forwarding
//    	StreamExecutionEnvironment env = StreamExecutionEnvironment
//    	        .createRemoteEnvironment(Settings.FLINK_SERVER, Settings.FLINK_SERVER_PORT);//, "/home/user/udfs.jar");
    	
        // Properties
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Settings.APP_KAFKA_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "FlinkConsumerGroup");

        DataStream<String> messageStream = env.addSource(new FlinkKafkaConsumer010<>(Settings.APP_KAFKA_TOPIC, new SimpleStringSchema(), props));

        // Split up the lines in pairs (2-tuples) containing: (fruit,1)
        messageStream.flatMap(new Tokenizer())
                // group by the tuple field "0" (kind of fruit) and sum up tuple field "1" (occurrences)
                .keyBy(0)
                .sum(1)
                .print();

        try {
            env.execute();
        } catch (Exception e) {
            logger.error("An error occurred.", e);
        }
    }// main

    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

		private static final long serialVersionUID = -4818649534455099787L;

		@Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }// Tokenizer
    
}//FlinkFruitConsumer
