package com.enhao.learning.in.kafka.sample.consumer_client.specified_offset_consumption;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

/**
 * 指定位移消费 - 从分区末尾消费
 *
 * @author enhao
 */
public class SpecifiedOffsetConsumer3 {
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
        consumer.subscribe(Arrays.asList(TOPIC));

        // 3. 如果未分配到分区，则循环调用poll()方法。
        Set<TopicPartition> assignment = new HashSet<>();
        while (assignment.size() == 0) {
            consumer.poll(Duration.ofMillis(100));
            // 获取消费者所分配到的分区信息
            assignment = consumer.assignment();
        }

        // 获取指定分区的末尾的消费位置
        // 末尾的位置是将要写入最新消息的位置
        // 如果从分区开头进行消费，则使用 beginningOffsets 方法。
        Map<TopicPartition, Long> offsets = consumer.endOffsets(assignment);
        for (TopicPartition topicPartition : assignment) {
            // 4. 指定位移消费
            consumer.seek(topicPartition, offsets.get(topicPartition));
        }
        // 便捷方式：从分区末尾消费
        // consumer.seekToEnd(assignment);

        // 5. 消息消费
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }
}
