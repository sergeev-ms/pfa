package com.borets.pfa.web.screens.analytic.analyticset

import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.cuba.gui.components.GroupTable
import com.haulmont.cuba.gui.model.CollectionLoader
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_AnalyticSet.browse")
@UiDescriptor("analytic-set-browse.xml")
@LookupComponent("analyticSetsTable")
@LoadDataBeforeShow
class AnalyticSetBrowse : StandardLookup<AnalyticSet>() {
    @Inject
    private lateinit var analyticSetsDl: CollectionLoader<AnalyticSet>

    @Inject
    private lateinit var analyticSetsTable: GroupTable<AnalyticSet>

    private var selectedAnalyticSets: MutableList<AnalyticSet>? = null

    companion object {
       const val AVAILABLE_ANALYTICS_PARAM_NAME : String = "availableAnalytics"
       const val SELECTED_ANALYTICS_PARAM_NAME : String = "selectedAnalytics"
    }

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val params : MutableMap<String, Any>? = (event.options as? MapScreenOptions?)?.params
        @Suppress("UNCHECKED_CAST")
        val analyticSets = params?.get(AVAILABLE_ANALYTICS_PARAM_NAME) as MutableList<AnalyticSet>?
        if (analyticSets != null) {
            analyticSetsDl.setLoadDelegate { return@setLoadDelegate analyticSets }
        }

        @Suppress("UNCHECKED_CAST")
        selectedAnalyticSets = params?.get(SELECTED_ANALYTICS_PARAM_NAME) as MutableList<AnalyticSet>?
    }

    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        selectedAnalyticSets?.let {
            analyticSetsTable.setSelected(it)
        }
    }


}