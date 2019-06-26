---
title: 02-Kafka安装与配置
date: 2019-06-25
categories: learning-in-kafka
tags: [Kafka]
---



## 开发环境

- 操作系统：macOS Mojave 10.14.4
- JDK：1.8.0_181
- ZooKeeper：3.4.12
- Kafka：2.11-2.0.0

## Zookeeper安装与配置

在 `Kafka`集群中，`Zookeeper`的作用是对元数据信息的管理。

使用`Zookeeper`的版本：`zookeeper-3.4.12`。

### 单机模式配置

从官网下载后解压，进入`conf`文件夹，复制一份`zoo_sample.cfg`文件并重命名为`zoo.cfg`，并进行配置：

```properties
# The number of milliseconds of each tick
# zookeeper服务器心跳时间，单位为毫秒ms，即每隔tickTime时间发送一个心跳。
tickTime=2000

# The number of ticks that the initial 
# synchronization phase can take
# LF初始通信时限
# 集群中的Follower跟随者服务器与Leader领导者服务器之间初始连接时能容忍的最多心跳数（tickTime的数量），用它来限定集群中的Zookeeper服务器连接到Leader的时限。
# 10个心跳帧，时间为tickTime * initLimit
initLimit=10

# The number of ticks that can pass between 
# sending a request and getting an acknowledgement
# LF同步通信时限
# 集群中Leader与Follower之间的最大响应时间单位，假如响应超过syncLimit * tickTime，Leader认为Follwer死掉，从服务器列表中删除Follwer。
syncLimit=5

# the directory where the snapshot is stored.
# do not use /tmp for storage, /tmp here is just 
# example sakes.
# 数据目录
dataDir=/Users/enhao/develop/zookeeper-3.4.12/data
# 日志目录
dataLogDir=/Users/enhao/develop/zookeeper-3.4.12/log

# the port at which the clients will connect
# zookeeper对外服务端口
clientPort=2181
```

在上面配置的`dataDir`的路径中创建一个`myid`文件，并写入一个数值，用来存放的是服务器的编号，比如0。

### 集群模式配置

集群模式配置是在单机模式基础进行。

集群模式下的每个`zookeeper`节点的`myid`文件中的数值都要不一样。

假设在三台机器上部署了`zookeeper`，需要在这三台机器的`zoo.cfg`配置文件中都配置：

```properties
server.0=192.168.0.2:2888:3888
server.1=192.168.0.3:2888:3888
server.2=192.168.0.4:2888:3888
```

> `server.A=B:C:D`
>
> A：是一个数字，表示第几号服务器，是`data/myid`中的值。
>
> B：这个服务器的ip地址/域名。
>
> C：这个服务器与集群中的Leader服务器交换信息的端口。
>
> D：万一集群中的Leader服务器挂了，需要一个端口来重新进行选举，选出一个新的Leader，而这个端口就是用来执行选举时服务器相互通信的端口。

### 启动服务

- 启动服务：

  ```shell
  bin/zkServer.sh start
  ```

  > 如果是集群环境，则集群中的每个节点都要启动。

- 查看状态

  ```shell
  zkServer.sh status
  ```

- 关闭服务：

  ```shell
  bin/zkServer.sh stop
  ```

- 连接客户端：

  ```shell
  bin/zkCli.sh
  ```

  > 退出客户端：quit

## Kafka安装与配置

使用的`Kafka`版本：`kafka_2.11-2.0.0`

### 单机/集群配置

从官网下载后解压，修改`conf/servet.properties`文件：

```properties
# 编号，如果集群中有多个broker，则每个broker的编号需要设置的不同
broker.id=0
# broker对外提供的服务入口地址
listeners=PLAINTEXT://localhost:9092
# 存放消息日志文件的地址
log.dirs=/Users/enhao/develop/kafka_2.11-2.0.0/log
# Kafka所需的ZooKeeper集群地址，为了方便演示，我们假设Kafka和ZooKeeper都安装在本机
zookeeper.connect=localhost:2181/kafka
```

> 集群模式的配置和上面的一样，只需要注意`broker.id`不要重复，`listeners`配置为broker对应的ip或域名。

### 启动服务

> 注意：在启动 kafka 服务器，请先确保启动了zookeeper服务。

- 后台启动服务

  ```shell
  bin/kafka-server-start.sh -daemon config/server.properties
  ```

  > 如果集群中有多个broker。则每一个broker都需要启动。

- 关闭服务

  ```shell
  bin/kafka-server-stop.sh stop
  ```


## 生产与消费测试

- 创建主题：

  单机模式下使用，副本和分区都设置为1。

  ```shell
  bin/kafka-topics.sh --zookeeper localhost: 2181/kafka --create --topic topic-demo --replication-factor 1 --partitions 1
  ```

  > `--create`：创建指令。
  >
  > `--zookeeper`：kafka连接的zookeeper服务地址。
  >
  > `--topic`：指定创建的主题名称。
  >
  > `--replication-factor `：副本因子。
  >
  > `--partitions`：分区数。

- 查看主题详细信息：

  ```shell
  bin/kafka-topics.sh --zookeeper localhost: 2181/kafka --describe --topic topic-demo
  ```

  > 由于设置的分区和副本都是1，所以相信信息如下：
  >
  > ```
  > Topic:topic-demo	PartitionCount:1	ReplicationFactor:1	Configs:
  > 	Topic: topic-demo	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
  > ```
  >
  > 分区号为0，leader副本是0，所有副本是0，ISR集合有0
  >
  > 如果像书中一样，创建了一个分区数为4，副本因子为3的主题，则详情如下：
  >
  > ```
  > Topic:topic-demo	PartitionCount:4	ReplicationFactor:3	Configs:
  > 	Topic: topic-demo	Partition: 0	Leader: 2	Replicas: 2,1,0	Isr: 2,1,0
  > 	Topic: topic-demo	Partition: 1	Leader: 0	Replicas: 0,2,1	Isr: 0,2,1
  > 	Topic: topic-demo	Partition: 2	Leader: 1	Replicas: 1,0,2	Isr: 1,0,2
  > 	Topic: topic-demo	Partition: 3	Leader: 2	Replicas: 2,0,1	Isr: 2,0,1
  > ```
  >
  > 以分区0为例（第一条记录），leader 副本是2号分区，所有副本包括2,1,0，ISR集合包括2,1,0。即OSR为空。

- 打开一个消费者：

  ```shell
  bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic-demo
  ```

  > `--bootstrap-server`：kafka集群地址。
  >
  > `--topic`：消费的主题名称。

- 打开一个生产者，发送消息：

  ```shell
  bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topic-demo
  ```

  > `--broker-list`：kafka集群地址。
  >
  > `--topic`：消费的主题名称。

  打开生产者后，键入消息内容，回车。在消费者端就可以接收到消息。

### Java客户端测试

> 代码路径：`kafka-sample`下的`com.enhao.learning.in.kafka.sample.fast_start`。

首先引入kafka客户端依赖：

```xml
<!-- kafka 客户端 -->
<dependency>
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>2.0.0</version>
</dependency>
```

生产者：

```java
public class Producer {

    /**
     * kafka 集群地址
     */
    private static final String BROKER_LIST = "localhost:9092";

    /**
     * 主题名称
     */
    private static final String TOPIC = "topic-demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("bootstrap.servers", BROKER_LIST);

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "hello, Kafka!");
        try {
            producer.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.close();
    }
}
```

消费者：

```java
public class Consumer {
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

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("bootstrap.servers", BROKER_LIST);
        //设置消费组的名称
        properties.put("group.id", GROUP_ID);

        //创建一个消费者客户端实例
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        //订阅主题
        consumer.subscribe(Collections.singletonList(TOPIC));
        //循环消费消息
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());
            }
        }
    }
}
```

首先启动消费者，然后启动生产者。

生产者启动后，可在消费者的控制台看到收到的消息。



## 参考资料

- 《深入理解Kafka 核心设计与实践原理》