package com.borets.pfa.kafka.serializer

import com.borets.addon.pn.entity.Part
import com.haulmont.cuba.core.app.serialization.EntitySerializationAPI
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.AppBeans
import org.apache.kafka.common.serialization.Deserializer

class EntityDeserializer : Deserializer<Entity<*>> {
    override fun deserialize(topic: String, data: ByteArray): Entity<*> {
        val entitySerializationAPI = AppBeans.get(EntitySerializationAPI.NAME, EntitySerializationAPI::class.java)
        val string = String(data).replace("wedb$", "pn_") //hack. we have to use common Parts addon in wedb
        return entitySerializationAPI.objectFromJson(string, Part::class.java)
    }
}