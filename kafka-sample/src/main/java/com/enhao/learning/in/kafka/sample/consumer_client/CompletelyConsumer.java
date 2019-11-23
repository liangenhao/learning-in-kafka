package com.enhao.learning.in.kafka.sample.consumer_client;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 完整的消费程序
 *
 * @author enhao
 */
public class CompletelyConsumer {
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

        return properties;
    }

    public static void main(String[] args) {
        Properties properties = initConfig();

        // 1. 创建消费者实例
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // 2. 订阅主题和分区
        consumer.subscribe(Arrays.asList(TOPIC));

        // 3. 消息消费
        try {
            while (running.get()) {
                // 拉取消息
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                // 处理消息
                // 如果想关闭消费逻辑，可以调用consumer.wakeup()，或者running.set(false)

                // 提交位移

            }
        } catch (WakeupException e) {
            // 忽略，WakeupException异常为跳出循环的方式，不作处理
        } catch (Exception e) {
            // 处理异常
        } finally {
            consumer.close();
        }
    }
}
