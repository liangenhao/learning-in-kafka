---
title: 26-主题与分区-初识KafkaAdminClient-主题合法性验证
date: 2020-02-18
categories: learning-in-kafka
tags: [Kafka]
---



我们在使用`KafkaAdminClient`之类的工具创建主题的时候，有可能创建了不符合运维规范的主题。那么就需要做主题合法性验证。

【服务端参数】：

- 服务端参数`create.topic.policy.class.name`，默认值为null，它提供了一个入口用来验证主题创建的合法性。
- 自定义类实现`org.apache.kafka.server.policy.CreateTopicPolicy`接口，例如：`PolicyDemo`。
- 配置参数：`create.topic.policy.class.name=org.apache.kafka.server.policy.PolicyDemo`

> 【疑问】：配置了create.topic.policy.class. name这个参数，kafka是怎么去读到我们自定义的实现类的。
>
> 尝试后也没有成功，报出异常：ERROR Exiting Kafka due to fatal exception (kafka.Kafka$)
> org.apache.kafka.common.config.ConfigException: Invalid value org.apache.kafka.server.policy.PolicyDemo for configuration create.topic.policy.class.name: Class org.apache.kafka.server.policy.PolicyDemo could not be found.

【`CreateTopicPolicy`】：

- `configure()`：在kafka服务启动时执行。
- `validate()`：用来鉴定主题参数的合法性，在创建主题时执行。
- `close()`：关闭kafka服务时执行。