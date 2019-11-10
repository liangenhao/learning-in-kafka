package com.enhao.learning.in.kafka.sample.consumer_client.deserializer;

import com.enhao.learning.in.kafka.sample.producer_client.serializer.Company;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 自定义反序列化器 - company 反序列化器
 *
 * @author enhao
 */
public class CompanyDeserializer implements Deserializer<Company> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public Company deserialize(String topic, byte[] data) {
        // 如果 data 为null，直接返回null，不要抛出一个异常。
        if (data == null) {
            return null;
        }
        if (data.length < 8) {
            throw new SerializationException("Size of data received by CompanyDeserializer is shorter than expected!");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        int nameLen, addressLen;
        String name, address;

        nameLen = buffer.getInt();
        byte[] nameBytes = new byte[nameLen];
        buffer.get(nameBytes);
        addressLen = buffer.getInt();
        byte[] addressBytes = new byte[addressLen];
        buffer.get(addressBytes);

        name = new String(nameBytes, StandardCharsets.UTF_8);
        address = new String(addressBytes, StandardCharsets.UTF_8);

        return Company.builder().name(name).address(address).build();
    }

    @Override
    public void close() {
        // nothing to do
    }
}
