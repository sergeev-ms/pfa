package com.borets.pfa.web.screens.analytic.analyticset

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.analytic.AnalyticSet

@UiController("pfa_AnalyticSet.edit")
@UiDescriptor("analytic-set-edit.xml")
@EditedEntityContainer("analyticSetDc")
@LoadDataBeforeShow
class AnalyticSetEdit : StandardEditor<AnalyticSet>()