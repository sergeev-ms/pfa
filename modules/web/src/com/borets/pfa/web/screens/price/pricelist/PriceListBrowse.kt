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

    @Subscribe("priceListsTable.openPlain")
    private fun onPriceListsTableOpenPlain(event: Action.ActionPerformedEvent) {
        screenBuilders.editor(priceListsTable)
            .editEntity(priceListsTable.singleSelected!!)
            .show()
    }

    @Subscribe("priceListsTable.edit")
    private fun onPriceListsTableEdit(event: Action.ActionPerformedEvent) {
        screenBuilders.editor(priceListsTable)
            .editEntity(priceListsTable.singleSelected!!)
            .withScreenClass(PriceListPivotEdit::class.java)
            .show()
    }

    @Subscribe("priceListsTable.create")
    private fun onPriceListsTableCreate(event: Action.ActionPerformedEvent) {
        screenBuilders.editor(priceListsTable)
            .newEntity()
            .withScreenClass(PriceListPivotEdit::class.java)
            .show()
    }


}