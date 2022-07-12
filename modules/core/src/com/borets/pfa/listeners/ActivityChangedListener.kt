package com.borets.pfa.listeners

import com.borets.pfa.entity.activity.Activity
import com.haulmont.cuba.core.app.events.EntityPersistingEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component("pfa_ActivityChangedListener")
open class ActivityChangedListener {

    @EventListener
    open fun beforePersist(event: EntityPersistingEvent<Activity>) {
        event.entity.country = event.entity.account?.country
    }
}
