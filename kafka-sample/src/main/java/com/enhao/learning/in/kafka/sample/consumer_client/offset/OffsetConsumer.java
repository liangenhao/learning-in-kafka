package com.enhao.learning.in.kafka.sample.consumer_client.offset;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * lastConsumedOffset、committed offset和position关系
 * @author enhao
 */
public class OffsetConsumer {
    /**
     * kafka 集群地址
     */
    private static final String BROKER_LIST = "localhost:9092";

    /**
     * 主题名称
     */
    private static final String TOPIC = "topic-demo";

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

        // 2. 订阅主题和分区
        // topic分区编号为0的分区
        TopicPartition topicPartition = new TopicPartition(TOPIC, 0);
        consumer.assign(Arrays.asList(topicPartition));

        // 当前消费到的位移
        long lastConsumedOffset = -1;

        // 3. 消息消费
        try {
            while (true) {
                // 拉取消息
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) {
                    break;
                }
                // 获取消息集中指定分区的消息
                List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
                // 拉取到的最后一个消息，就是当前消费到的位移
                lastConsumedOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                // 同步提交位移
                consumer.commitSync();
            }
            System.out.println("lastConsumedOffset is " + lastConsumedOffset);
            OffsetAndMetadata offsetAndMetadata = consumer.committed(topicPartition);
            System.out.println("committed offset is " + offsetAndMetadata.offset());
            long position = consumer.position(topicPartition);
            System.out.println("position is " + position);
            System.out.println();
        } finally {
            consumer.close();
        }
    }
}
