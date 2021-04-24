package com.borets.pfa.web.screens.price.revenuetype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.price.RevenueType

@UiController("pfa_RevenueType.edit")
@UiDescriptor("revenue-type-edit.xml")
@EditedEntityContainer("revenueTypeDc")
@LoadDataBeforeShow
class RevenueTypeEdit : StandardEditor<RevenueType>()