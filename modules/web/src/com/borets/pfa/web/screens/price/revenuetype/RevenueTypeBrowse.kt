package com.borets.pfa.web.screens.price.revenuetype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.price.RevenueType

@UiController("pfa_RevenueType.browse")
@UiDescriptor("revenue-type-browse.xml")
@LookupComponent("revenueTypesTable")
@LoadDataBeforeShow
class RevenueTypeBrowse : StandardLookup<RevenueType>()