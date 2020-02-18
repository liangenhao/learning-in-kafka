---
title: 25-主题与分区-初识KafkaAdminClient-基本使用
date: 2020-02-02
categories: learning-in-kafka
tags: [Kafka]
---

从 0.11.0.0 开始，kafka提供了工具类`org.apache.kafka.clients.admin.KafkaAdminClient`，作用：

1. 管理broker。
2. 配置。
3. ACL。
4. 管理主题。

## 常用方法

`KafkaAdminClient`继承了`AdminClient`抽象类。提供了常用方法：

- 创建主题：

  ```java
  CreateTopicsResult createTopics(Collection<NewTopic> newTopics);
  CreateTopicsResult createTopics(Collection<NewTopic> newTopics, 
                                  CreateTopicsOptions options);
  ```

- 删除主题：

  ```java
  DeleteTopicsResult deleteTopics(Collection<String> topics);
  DeleteTopicsResult deleteTopics(Collection<String> topics, 
                                  DeleteTopicsOptions options);
  ```

- 列出所有可用的主题：

  ```java
  ListTopicsResult listTopics();
  ListTopicsResult listTopics(ListTopicsOptions options);
  ```

- 查看主题的信息：

  ```java
  DescribeTopicsResult describeTopics(Collection<String> topicNames);
  DescribeTopicsResult describeTopics(Collection<String> topicNames,
                                      DescribeTopicsOptions options);
  ```

- 查询配置信息：

  ```java
  DescribeConfigsResult describeConfigs(Collection<ConfigResource> resources);
  DescribeConfigsResult describeConfigs(Collection<ConfigResource> resources,
                                        DescribeConfigsOptions options);
  ```

- 修改配置信息：

  ```java
  AlterConfigsResult alterConfigs(Map<ConfigResource, Config> configs);
  AlterConfigsResult alterConfigs(Map<ConfigResource, Config> configs, 
                                  AlterConfigsOptions options);
  ```

- 增加分区：

  ```java
  CreatePartitionsResult createPartitions(Map<String, NewPartitions> newPartitions);
  CreatePartitionsResult createPartitions(Map<String, NewPartitions> newPartitions,
                                          CreatePartitionsOptions options);
  ```

  

## 管理主题

【创建主题】：创建一个分区数为4、副本因子为1的主题`topic-admin`。

```java
private static String brokerList = "localhost:19092,localhost:19093,localhost:19094";
private static String topic = "topic-admin";

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
```

> 其他关于管理主题的操作详见：`KafkaAdminTopicOperation`。



## 管理配置

【查询主题的配置信息】：

```java
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
```

> 其他关于管理配置的操作详见：`KafkaAdminConfigOperation`。