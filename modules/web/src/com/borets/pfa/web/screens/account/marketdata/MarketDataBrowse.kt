package com.borets.pfa.web.screens.account.marketdata

import com.borets.pfa.entity.account.marketdata.MarketData
import com.haulmont.cuba.gui.model.CollectionLoader
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_MarketData.browse")
@UiDescriptor("market-data-browse.xml")
@LookupComponent("marketDatasTable")
@LoadDataBeforeShow
class MarketDataBrowse : StandardLookup<MarketData>() {
    @Inject
    private lateinit var marketDatasDl: CollectionLoader<MarketData>


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val account = (event.options as? MapScreenOptions)?.params?.get("account")
        marketDatasDl.setParameter("account", account)
    }
}