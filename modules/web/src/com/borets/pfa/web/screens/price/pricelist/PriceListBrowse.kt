package com.borets.pfa.web.screens.price.pricelist

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.web.screens.price.pricelist.input.PriceListPivotEdit
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.GroupTable
import javax.inject.Inject

@UiController("pfa_PriceList.browse")
@UiDescriptor("price-list-browse.xml")
@LookupComponent("priceListsTable")
@LoadDataBeforeShow
class PriceListBrowse : StandardLookup<PriceList>() {
    @Inject
    private lateinit var screenBuilders: ScreenBuilders

    @Inject
    private lateinit var priceListsTable: GroupTable<PriceList>

    @Subscribe("priceListsTable.openPivot")
    private fun onPriceListsTableOpenPivot(event: Action.ActionPerformedEvent) {
        screenBuilders.editor(priceListsTable)
            .editEntity(priceListsTable.singleSelected!!)
            .withScreenClass(PriceListPivotEdit::class.java)
            .show()
    }
}