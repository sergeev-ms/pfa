package com.borets.pfa.kafka.serializer

import com.haulmont.cuba.core.app.serialization.EntitySerializationAPI
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.AppBeans
import org.apache.kafka.common.serialization.Serializer

class EntitySerializer : Serializer<Entity<*>> {
    override fun serialize(topic: String, data: Entity<*>?): ByteArray? {
        if (data == null) {
            return null
        }
        val entitySerializationAPI = AppBeans.get(EntitySerializationAPI.NAME, EntitySerializationAPI::class.java)
        return entitySerializationAPI.toJson(data).toByteArray()
    }
}