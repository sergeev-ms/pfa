package com.borets.pfa.web.screens.demand.demandtype

import com.borets.pfa.entity.demand.DemandType
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_DemandType.browse")
@UiDescriptor("demand-type-browse.xml")
@LookupComponent("demandTypesTable")
@LoadDataBeforeShow
class DemandTypeBrowse : StandardLookup<DemandType>()