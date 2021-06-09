package com.borets.pfa.web.screens.account.system.classification.materials

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.classification.Materials

@UiController("pfa_Materials.browse")
@UiDescriptor("materials-browse.xml")
@LookupComponent("materialsesTable")
@LoadDataBeforeShow
class MaterialsBrowse : StandardLookup<Materials>()