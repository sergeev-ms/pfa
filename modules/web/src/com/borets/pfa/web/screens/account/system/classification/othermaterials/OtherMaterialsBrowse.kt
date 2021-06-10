package com.borets.pfa.web.screens.account.system.classification.othermaterials

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.OtherMaterials

@UiController("pfa_OtherMaterials.browse")
@UiDescriptor("other-materials-browse.xml")
@LookupComponent("otherMaterialsTable")
@LoadDataBeforeShow
class OtherMaterialsBrowse : StandardLookup<OtherMaterials>()