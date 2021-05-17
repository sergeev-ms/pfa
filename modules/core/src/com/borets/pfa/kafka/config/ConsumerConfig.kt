package com.borets.pfa.kafka.config

import com.borets.pfa.kafka.serializer.EntityDeserializer
import com.haulmont.cuba.core.entity.Entity
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.UUIDDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import java.util.HashMap

@EnableKafka
@Configuration
open class ConsumerConfig {
    @Value("\${pfa.kafka.serverConfig}")
    private lateinit var kafkaServer: String

    @Value("\${pfa.kafka.parts.consumer.group}")
    private lateinit var consumerGroup: String

    @Bean
    open fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Entity<*>>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Entity<*>>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    open fun consumerFactory(): ConsumerFactory<String, Entity<*>> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    open fun consumerConfigs(): Map<String, Any?> {
        val props: MutableMap<String, Any?> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaServer
        props[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
        props[ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG] = "100"
        props[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = "15000"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = UUIDDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = EntityDeserializer::class.java
        return props
    }
}