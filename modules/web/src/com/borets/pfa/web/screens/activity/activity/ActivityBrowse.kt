package com.borets.pfa.web.screens.activity.activity

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.activity.Activity
import com.haulmont.cuba.gui.model.CollectionLoader
import javax.inject.Inject

@UiController("pfa_Activity.browse")
@UiDescriptor("activity-browse.xml")
@LookupComponent("activitiesTable")
@LoadDataBeforeShow
class ActivityBrowse : StandardLookup<Activity>() {
    @Inject
    private lateinit var activitiesDl: CollectionLoader<Activity>

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val options = event.options
        (options as? MapScreenOptions)?.params?.run {
            this["account"]?.let { activitiesDl.setParameter("account", it) }
            this["year"]?.let { activitiesDl.setParameter("year", it) }
        }
    }

}