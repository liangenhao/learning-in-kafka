package com.enhao.learning.in.kafka.sample.producer_client.producer_interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义拦截器
 * - 为消息添加前缀
 * - 统计消息发送成功率
 *
 * @author enhao
 */
@Slf4j
public class PrefixProducerInterceptor implements ProducerInterceptor<String, String> {

    private AtomicLong sendSuccess = new AtomicLong(0);
    // private volatile long sendSuccess = 0;
    private AtomicLong sendFailure = new AtomicLong(0);
    // private volatile long sendFailure = 0;

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        // 为消息value添加前缀
        String modifiedValue = "prefix1-" + record.value();
        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(), record.key(), modifiedValue, record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            sendSuccess.getAndIncrement();
        } else {
            sendFailure.getAndIncrement();
        }
    }

    @Override
    public void close() {
        // 统计成功率
        double successRatio = (double) sendSuccess.get() / (sendSuccess.get() + sendFailure.get());
        log.info("发送成功率 = " + String.format("%f", successRatio * 100) + "%");
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // nothing to do
    }
}
