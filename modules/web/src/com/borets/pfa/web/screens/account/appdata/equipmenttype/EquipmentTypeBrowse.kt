package com.borets.pfa.web.screens.account.appdata.equipmenttype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.appdata.EquipmentType

@UiController("pfa_EquipmentType.browse")
@UiDescriptor("equipment-type-browse.xml")
@LookupComponent("equipmentTypesTable")
@LoadDataBeforeShow
class EquipmentTypeBrowse : StandardLookup<EquipmentType>()