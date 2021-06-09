package com.borets.pfa.web.screens.account.system.classification.pumptype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.PumpType

@UiController("pfa_PumpType.browse")
@UiDescriptor("pump-type-browse.xml")
@LookupComponent("pumpTypesTable")
@LoadDataBeforeShow
class PumpTypeBrowse : StandardLookup<PumpType>()