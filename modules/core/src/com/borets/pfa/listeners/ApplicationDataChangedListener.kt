package com.borets.pfa.listeners

import com.borets.pfa.entity.account.appdata.ApplicationData
import com.haulmont.cuba.core.app.events.EntityPersistingEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component("pfa_ApplicationDataChangedListener")
open class ApplicationDataChangedListener {

    @EventListener
    open fun beforePersist(event: EntityPersistingEvent<ApplicationData>) {
        event.entity.country = event.entity.account?.country
    }
}
