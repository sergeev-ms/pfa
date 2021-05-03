package com.borets.pfa.web.screens.account.appdata.applicationdata

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.appdata.ApplicationData
import com.borets.pfa.entity.account.marketdata.MarketData
import com.haulmont.cuba.gui.model.CollectionLoader
import javax.inject.Inject

@UiController("pfa_ApplicationData.browse")
@UiDescriptor("application-data-browse.xml")
@LookupComponent("applicationDatasTable")
@LoadDataBeforeShow
class ApplicationDataBrowse : StandardLookup<ApplicationData>() {
    @Inject
    private lateinit var applicationDatasDl: CollectionLoader<ApplicationData>


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val account = (event.options as? MapScreenOptions)?.params?.get("account")
        applicationDatasDl.setParameter("account", account)
    }
}