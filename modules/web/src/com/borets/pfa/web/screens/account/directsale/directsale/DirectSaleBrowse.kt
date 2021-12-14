package com.borets.pfa.web.screens.account.directsale.directsale

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.directsale.DirectSale

@UiController("pfa_DirectSale.browse")
@UiDescriptor("direct-sale-browse.xml")
@LookupComponent("directSalesTable")
@LoadDataBeforeShow
class DirectSaleBrowse : StandardLookup<DirectSale>()