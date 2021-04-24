package com.borets.pfa.web.screens.price.pricelist

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.price.PriceList

@UiController("pfa_PriceList.edit")
@UiDescriptor("price-list-edit.xml")
@EditedEntityContainer("priceListDc")
@LoadDataBeforeShow
class PriceListEdit : StandardEditor<PriceList>()