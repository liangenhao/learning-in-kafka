package com.enhao.learning.in.kafka.sample.consumer_client.consumptionLatitude;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 按主题纬度进行消费的消费者
 * @author enhao
 */
public class TopicLatitudeConsumer {
    /**
     * kafka 集群地址
     */
    private static final String BROKER_LIST = "localhost:9092";

    /**
     * 主题名称
     */
    private static final String TOPIC_1 = "topic-demo-1";
    private static final String TOPIC_2 = "topic-demo-2";

    /**
     * 消费者组名称
     */
    private static final String GROUP_ID = "group.demo";

    private static Properties initConfig() {
        Properties properties = new Properties();
        // 集群地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        // 反序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 消费者组名称
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        // 客户端id
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer.client.id.demo");

        return properties;
    }

    public static void main(String[] args) {
        Properties properties = initConfig();

        // 1. 创建消费者实例
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // 2. 订阅主题和分区 : 集合订阅方式
        List<String> topicList = Arrays.asList(TOPIC_1, TOPIC_2);
        consumer.subscribe(topicList);

        // 3. 消息消费 : 按主题纬度进行消费
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                topicList.forEach(topic -> {
                    Iterable<ConsumerRecord<String, String>> consumerRecords = records.records(topic);
                    for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                        System.out.println(consumerRecord.topic() + " : " + consumerRecord.value());
                    }
                });
            }
        } finally {
            consumer.close();
        }
    }
}
