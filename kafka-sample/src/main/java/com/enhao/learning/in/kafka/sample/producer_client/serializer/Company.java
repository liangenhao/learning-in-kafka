package com.enhao.learning.in.kafka.sample.producer_client.serializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author enhao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    private String name;

    private String address;
}
