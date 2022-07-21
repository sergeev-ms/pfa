package com.borets.pfa.listeners

import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.haulmont.cuba.core.app.events.EntityPersistingEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component("pfa_EquipmentUtilizationChangedListener")
open class EquipmentUtilizationChangedListener {

    @EventListener
    open fun beforePersist(event: EntityPersistingEvent<EquipmentUtilization>) {
        event.entity.country = event.entity.account?.country
    }
}
