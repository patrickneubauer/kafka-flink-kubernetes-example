package com.patrickneubauer.examples.streaming;

public class Settings {

	public final static String APP_KAFKA_TOPIC = System.getenv("APP_KAFKA_TOPIC") != null
			? System.getenv("APP_KAFKA_TOPIC")
			: "fruits2";
			
	public final static String APP_KAFKA_SERVER = System.getenv("APP_KAFKA_SERVER") != null
			? System.getenv("APP_KAFKA_SERVER")
			: "178.62.77.170:31053";
			
	public final static String APP_ZOOKEEPER_SERVER = System.getenv("APP_ZOOKEEPER_SERVER") != null
			? System.getenv("APP_ZOOKEEPER_SERVER")
			: "localhost:32500";
			
	public final static String FLINK_SERVER = System.getenv("FLINK_SERVER") != null
			? System.getenv("FLINK_SERVER")
			: "localhost";
			
	public final static int FLINK_SERVER_PORT = System.getenv("FLINK_SERVER_PORT") != null
			? Integer.parseInt(System.getenv("FLINK_SERVER_PORT"))
			: 8081;

}
