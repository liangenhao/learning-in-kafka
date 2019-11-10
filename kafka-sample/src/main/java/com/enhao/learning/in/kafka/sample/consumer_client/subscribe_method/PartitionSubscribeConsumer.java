package com.enhao.learning.in.kafka.sample.consumer_client.subscribe_method;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 指定分区订阅方式的消费者
 * @author enhao
 */
public class PartitionSubscribeConsumer {

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

        // 2. 订阅主题和分区 : 指定分区的订阅方式
        // 方式一 : 直接指定分区，这里表示只订阅TOPIC主题中的分区编号为0的分区。
        // consumer.assign(Arrays.asList(new TopicPartition(TOPIC, 0)));

        // 方式二 : 当不知道主题的分区情况时，可以先获取主题的分区元数据信息
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(TOPIC);
        if (partitionInfos != null) {
            List<TopicPartition> topicPartitions = partitionInfos.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()))
                    .collect(Collectors.toList());
            // 订阅主题的全部分区
            consumer.assign(topicPartitions);
        }


        // 3. 消息消费
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
