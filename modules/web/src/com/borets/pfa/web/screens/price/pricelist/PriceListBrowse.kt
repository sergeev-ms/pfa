package com.borets.pfa.web.screens.price.pricelist

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.price.PriceList

@UiController("pfa_PriceList.browse")
@UiDescriptor("price-list-browse.xml")
@LookupComponent("priceListsTable")
@LoadDataBeforeShow
class PriceListBrowse : StandardLookup<PriceList>()