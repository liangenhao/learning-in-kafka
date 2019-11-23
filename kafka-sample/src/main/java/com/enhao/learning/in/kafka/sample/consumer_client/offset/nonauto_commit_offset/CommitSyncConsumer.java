package com.enhao.learning.in.kafka.sample.consumer_client.offset.nonauto_commit_offset;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 手动位移提交 - 同步提交
 *
 * @author enhao
 */
public class CommitSyncConsumer {

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
        // 手动位移提交方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return properties;
    }

    public static void main(String[] args) {
        Properties properties = initConfig();

        // 1. 创建消费者实例
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // 2. 订阅主题和分区 : 集合订阅方式
        consumer.subscribe(Arrays.asList(TOPIC));

        // 3. 消息消费

        // 方式一 : 按照拉取批次提交
        // try {
        //     while (true) {
        //         ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        //         for (ConsumerRecord<String, String> record : records) {
        //             System.out.println(record.value());
        //         }
        //
        //         // 同步位移提交 : 无参的commitSync()方法，只能提交当前批次的position值。
        //         consumer.commitSync();
        //     }
        // } finally {
        //     consumer.close();
        // }

        // 方式二 : 指定位移提交
        // try {
        //     while (true) {
        //         ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
        //         for (ConsumerRecord<String, String> record : records) {
        //             System.out.println(record.value());
        //
        //             long offset = record.offset();
        //             TopicPartition partition = new TopicPartition(record.topic(), record.partition());
        //             // 同步位移提交 : 指定位移提交
        //             consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(offset + 1))); // 指定需要提交的位移
        //         }
        //     }
        // } finally {
        //     consumer.close();
        // }

        // 方式三 : 按照分区粒度提交
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (TopicPartition partition : records.partitions()) {
                    // 按照分区维度进行消费
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                    for (ConsumerRecord<String, String> record : partitionRecords) {
                        System.out.println(record.value());
                    }
                    // 按照分区维度进行提交位移
                    long lastConsumedOffset = partitionRecords.get(partitionRecords.size() - 1).offset();// 获取这个分区的最后一条记录的offset, 就是当前消费到的位置
                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastConsumedOffset + 1))); // 指定需要提交的位移
                }
            }
        } finally {
            consumer.close();
        }
    }
}
