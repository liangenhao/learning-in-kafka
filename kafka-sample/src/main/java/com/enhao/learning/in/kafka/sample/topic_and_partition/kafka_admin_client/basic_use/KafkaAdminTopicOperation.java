package com.enhao.learning.in.kafka.sample.topic_and_partition.kafka_admin_client.basic_use;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * 使用KafkaAdminClient管理主题
 *
 * @author enhao
 */
public class KafkaAdminTopicOperation {

    private static String brokerList = "localhost:19092,localhost:19093,localhost:19094";
    private static String topic = "topic-admin";

    public static void main(String[] args) {
        // 创建一个主题
        // createTopic();

        // 查看主题详情
        describeTopic();

        // 查询所有可用的主题
        // listTopic();

        // 删除主题
        // deleteTopic();
    }

    /**
     * 创建一个主题
     */
    private static void createTopic() {
        // 1. 配置信息
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // 2. 创建一个 KafkaAdminClient 实例
        AdminClient adminClient = AdminClient.create(props);

        // 3. NewTopic 用来设定所要创建主题的具体信息，包含创建主题时需要的主题名称、分区数、副本因子、分配方案和配置。
        NewTopic newTopic = new NewTopic(topic, 4, (short) 1);
        Map<String, String> configs = new HashMap<>();
        configs.put("cleanup.policy", "compact");
        newTopic.configs(configs);

        // 4. 创建主题
        // result中的futures类型是 Map<String, KafkaFuture<Void>>：key 表示主题名称，value 表示创建后的返回值类型Void
        CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));

        try {
            // 5. 等待服务器端返回
            result.all().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            adminClient.close();
        }
    }

    /**
     * 查看主题的详情
     */
    private static void describeTopic() {
        // 1. 配置信息
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // 2. 创建一个 KafkaAdminClient 实例
        AdminClient adminClient = AdminClient.create(props);

        // 3. 查看主题的信息
        DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Arrays.asList(topic));

        KafkaFuture<Map<String, TopicDescription>> mapKafkaFuture = describeTopicsResult.all();
        try {
            Map<String, TopicDescription> stringTopicDescriptionMap = mapKafkaFuture.get();
            stringTopicDescriptionMap.forEach((topic, description) -> {
                System.out.println(description);
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }

    /**
     * 查询所有可用的主题
     */
    private static void listTopic() {
        // 1. 配置信息
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // 2. 创建一个 KafkaAdminClient 实例
        AdminClient adminClient = AdminClient.create(props);

        // 3. 查看所有可用的主题
        ListTopicsResult listTopicsResult = adminClient.listTopics();

        KafkaFuture<Map<String, TopicListing>> mapKafkaFuture = listTopicsResult.namesToListings();
        try {
            Map<String, TopicListing> stringTopicListingMap = mapKafkaFuture.get();
            stringTopicListingMap.forEach((topic, description) -> {
                System.out.println(description);
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }

    /**
     * 删除主题
     */
    private static void deleteTopic() {
        // 1. 配置信息
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // 2. 创建一个 KafkaAdminClient 实例
        AdminClient adminClient = AdminClient.create(props);

        // 3. 删除主题
        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(topic));
        try {
            deleteTopicsResult.all().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            adminClient.close();
        }
    }


}
