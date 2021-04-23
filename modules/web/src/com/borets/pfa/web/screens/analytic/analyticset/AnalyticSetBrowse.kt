package com.borets.pfa.web.screens.analytic.analyticset

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.analytic.AnalyticSet

@UiController("pfa_AnalyticSet.browse")
@UiDescriptor("analytic-set-browse.xml")
@LookupComponent("analyticSetsTable")
@LoadDataBeforeShow
class AnalyticSetBrowse : StandardLookup<AnalyticSet>()