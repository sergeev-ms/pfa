package com.borets.pfa.kafka.listener

import com.haulmont.cuba.core.entity.BaseGenericIdEntity
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.EntityStates
import org.slf4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import javax.inject.Inject


@Component
class KafkaPartNumberConsumer {
    @Inject
    private lateinit var log: Logger
    @Inject
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var dataManager: DataManager


    @KafkaListener(topics = ["#{'\${pfa.kafka.parts.consumer.topics}'.split(',')}"])
    fun messageReceived(entity: Entity<*>, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) id: String) {
        log.info("Received: {}:{}", id, entity)
        entityStates.makeDetached(entity as BaseGenericIdEntity<*>)
        dataManager.commit(entity)
    }
}