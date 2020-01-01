package com.enhao.learning.in.kafka.sample.consumer_client.rebalance;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 自定义消费者再均衡监听器的消费者
 * @author enhao
 */
public class CustomizeRebalanceListenerConsumer {
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

    private static AtomicBoolean running = new AtomicBoolean(true);

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


        Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
        // 2. 订阅主题和分区 : 集合订阅方式，自定义消费者再均衡监听器
        consumer.subscribe(Arrays.asList(TOPIC), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                // 在发生再均衡动作前，同步提交消费位移
                consumer.commitSync(currentOffsets);
                currentOffsets.clear();
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

            }
        });

        // 3. 消息消费
        try {
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());

                    currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                }
                consumer.commitAsync(currentOffsets, null);
            }
        } finally {
            consumer.close();
        }
    }

}
