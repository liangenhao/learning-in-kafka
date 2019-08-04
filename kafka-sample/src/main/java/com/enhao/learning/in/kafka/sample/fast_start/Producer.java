package com.enhao.learning.in.kafka.sample.fast_start;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * 快速入门生产者
 *
 * @author enhao
 */
public class Producer {

    /**
     * kafka 集群地址
     */
    private static final String BROKER_LIST = "localhost:9092";

    /**
     * 主题名称
     */
    private static final String TOPIC = "topic-demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("bootstrap.servers", BROKER_LIST);

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "hello, Kafka!");
        try {
            // 发后即忘的发送消息模式
            producer.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.close();
    }
}
