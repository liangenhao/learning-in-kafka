package com.enhao.learning.in.kafka.sample.producer_client.serializer;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * 自定义序列化器 - Object 序列化器
 */
public class ObjectSerializer implements Serializer<Object> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {
        // nothing to do
    }

    @Override
    public byte[] serialize(String s, Object object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Error when serializing object to byte[] due to ", e);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}