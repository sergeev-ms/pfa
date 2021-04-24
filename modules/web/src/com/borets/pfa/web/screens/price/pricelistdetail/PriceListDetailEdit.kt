package com.borets.pfa.web.screens.price.pricelistdetail

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.price.PriceListDetail

@UiController("pfa_PriceListDetail.edit")
@UiDescriptor("price-list-detail-edit.xml")
@EditedEntityContainer("priceListDetailDc")
@LoadDataBeforeShow
class PriceListDetailEdit : StandardEditor<PriceListDetail>()