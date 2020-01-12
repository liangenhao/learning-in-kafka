package com.enhao.learning.in.kafka.sample.topic_and_partition.topic_management.create_topic;

/**
 * 通过{@link kafka.admin.TopicCommand}创建主题
 *
 * @author enhao
 */
public class CreateTopicByTopicCommand {

    public static void main(String[] args) {
        createTopic();
    }

    public static void createTopic() {
        String[] options = new String[] {
                "--zookeeper", "localhost:2181/kafka_cluster",
                "--create",
                "--topic", "topic-create-api",
                "--partitions", "1",
                "--replication-factor", "1"
        };

        kafka.admin.TopicCommand.main(options);
    }
}
