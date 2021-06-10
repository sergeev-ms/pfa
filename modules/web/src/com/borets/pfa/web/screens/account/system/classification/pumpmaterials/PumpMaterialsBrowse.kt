package com.borets.pfa.web.screens.account.system.classification.pumpmaterials

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.PumpMaterials

@UiController("pfa_PumpMaterials.browse")
@UiDescriptor("pump-materials-browse.xml")
@LookupComponent("pumpMaterialsTable")
@LoadDataBeforeShow
class PumpMaterialsBrowse : StandardLookup<PumpMaterials>()