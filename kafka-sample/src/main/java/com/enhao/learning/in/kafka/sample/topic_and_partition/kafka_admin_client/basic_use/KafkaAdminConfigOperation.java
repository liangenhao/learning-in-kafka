package com.enhao.learning.in.kafka.sample.topic_and_partition.kafka_admin_client.basic_use;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 使用KafkaAdminClient管理配置
 *
 * @author enhao
 */
public class KafkaAdminConfigOperation {

    private static String brokerList = "localhost:19092,localhost:19093,localhost:19094";
    private static String topic = "topic-admin";

    public static void main(String[] args) {
        // 查询主题配置信息
        // describeTopicConfigs();

        // 修改主题配置信息
        // alterTopicConfigs();

        // 增加主题分区
        createTopicPartitions();
    }

    /**
     * 查询主题配置信息
     */
    private static void describeTopicConfigs() {
        // 1. 配置信息
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // 2. 创建一个 KafkaAdminClient 实例
        AdminClient adminClient = AdminClient.create(props);

        // 3. 指定配置的类型：类型是主题，名称是主题名称
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);

        // 4. 查询主题配置信息
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Collections.singleton(resource));

        try {
            Map<ConfigResource, Config> configResourceConfigMap = describeConfigsResult.all().get();
            Config config = configResourceConfigMap.get(resource);
            // 列出了主题全部的配置信息，不只是被覆盖的配置信息
            System.out.println(config);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }

    /**
     * 修改主题配置信息
     */
    private static void alterTopicConfigs() {
        // 1. 配置信息
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // 2. 创建一个 KafkaAdminClient 实例
        AdminClient adminClient = AdminClient.create(props);

        // 3. 指定配置的类型：类型是主题，名称是主题名称
        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);

        // 4. 修改主题配置
        ConfigEntry entry = new ConfigEntry("cleanup.policy", "delete");
        Config config = new Config(Collections.singleton(entry));
        Map<ConfigResource, Config> configs = new HashMap<>();
        configs.put(configResource, config);
        AlterConfigsResult alterConfigsResult = adminClient.alterConfigs(configs);
        try {
            alterConfigsResult.all().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }

    /**
     * 增加主题分区
     */
    private static void createTopicPartitions() {
        // 1. 配置信息
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // 2. 创建一个 KafkaAdminClient 实例
        AdminClient adminClient = AdminClient.create(props);

        // 3. 增加主题分区
        NewPartitions newPartitions = NewPartitions.increaseTo(5);
        Map<String, NewPartitions> newPartitionsMap = new HashMap<>();
        newPartitionsMap.put(topic, newPartitions);
        CreatePartitionsResult result = adminClient.createPartitions(newPartitionsMap);
        try {
            result.all().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }

    }


}
