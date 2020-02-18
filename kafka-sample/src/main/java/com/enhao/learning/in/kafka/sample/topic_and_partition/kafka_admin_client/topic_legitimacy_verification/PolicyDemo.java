package com.enhao.learning.in.kafka.sample.topic_and_partition.kafka_admin_client.topic_legitimacy_verification;

import org.apache.kafka.common.errors.PolicyViolationException;
import org.apache.kafka.server.policy.CreateTopicPolicy;

import java.util.Map;

/**
 * 主题合法性校验 Demo
 * <p>
 * 同时需要在broker端的配置文件 config/server.properties 中配置参数 create.topic.policy.class.name 的值为 com.enhao.learning.in.kafka.sample.topic_and_partition.kafka_admin_client.topic_legitimacy_verification.PolicyDemo
 *
 * @author enhao
 */
public class PolicyDemo implements CreateTopicPolicy {
    @Override
    public void validate(RequestMetadata requestMetadata) throws PolicyViolationException {
        if (requestMetadata.numPartitions() != null || requestMetadata.replicationFactor() != null) {
            if (requestMetadata.numPartitions() < 5) {
                throw new PolicyViolationException("Topic should have at least 5 partitions, received: " + requestMetadata.numPartitions());
            }
            if (requestMetadata.replicationFactor() <= 1) {
                throw new PolicyViolationException("Topic should have at least 2 replication factor, received: " + requestMetadata.replicationFactor());
            }
        }
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
