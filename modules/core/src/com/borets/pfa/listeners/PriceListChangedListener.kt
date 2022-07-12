package com.borets.pfa.listeners

import com.borets.pfa.entity.price.PriceList
import com.haulmont.cuba.core.app.events.EntityPersistingEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component("pfa_PriceListChangedListener")
open class PriceListChangedListener {

    @EventListener
    open fun beforePersist(event: EntityPersistingEvent<PriceList>) {
        event.entity.country = event.entity.account?.country
    }
}
