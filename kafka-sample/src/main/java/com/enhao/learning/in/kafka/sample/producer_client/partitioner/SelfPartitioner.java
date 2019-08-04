package com.enhao.learning.in.kafka.sample.producer_client.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义分区器
 * {@link org.apache.kafka.clients.producer.internals.DefaultPartitioner} 默认分区器在key为null时，不会选择非可用的分区。
 * 这里自定义分区器打破这一限制。
 * @author enhao
 */
public class SelfPartitioner implements Partitioner {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (null == keyBytes) {
            // 轮询方式，从所有分区中获取分区号
            return counter.getAndIncrement() % numPartitions;
        } else {
            // 使用MurmurHash2算法进行hash，然后和所有分区数取余来得到分区号
            return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // nothing to do
    }
}
