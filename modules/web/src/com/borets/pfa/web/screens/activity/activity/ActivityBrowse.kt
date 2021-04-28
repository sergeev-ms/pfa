package com.borets.pfa.web.screens.activity.activity

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.activity.Activity
import com.borets.pfa.web.screens.activity.activity.input.ActivityPivotEdit
import com.borets.pfa.web.screens.price.pricelist.input.PriceListPivotEdit
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.GroupTable
import com.haulmont.cuba.gui.model.CollectionLoader
import javax.inject.Inject

@UiController("pfa_Activity.browse")
@UiDescriptor("activity-browse.xml")
@LookupComponent("activitiesTable")
@LoadDataBeforeShow
class ActivityBrowse : StandardLookup<Activity>() {
    @Inject
    private lateinit var screenBuilders: ScreenBuilders

    @Inject
    private lateinit var activitiesDl: CollectionLoader<Activity>

    @Inject
    private lateinit var activitiesTable: GroupTable<Activity>

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val options = event.options
        (options as? MapScreenOptions)?.params?.run {
            this["account"]?.let { activitiesDl.setParameter("account", it) }
            this["year"]?.let { activitiesDl.setParameter("year", it) }
        }
    }

    @Subscribe("activitiesTable.edit")
    private fun onActivitiesTableEdit(event: Action.ActionPerformedEvent) {
        screenBuilders.editor(activitiesTable)
            .editEntity(activitiesTable.singleSelected!!)
            .withScreenClass(ActivityPivotEdit::class.java)
            .show()
    }

    @Subscribe("activitiesTable.openPlain")
    private fun onActivitiesTableOpenPlain(event: Action.ActionPerformedEvent) {
        screenBuilders.editor(activitiesTable)
            .editEntity(activitiesTable.singleSelected!!)
            .show()
    }
}