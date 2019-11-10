package com.enhao.learning.in.kafka.sample.consumer_client.deserializer;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * 自定义反序列化器 - Object 反序列化器
 */
public class ObjectDeserializer implements Deserializer<Object> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {
        // nothing to do
    }

    @Override
    public Object deserialize(String s, byte[] bytes) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Error when deserializing byte[] to object.", e);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
