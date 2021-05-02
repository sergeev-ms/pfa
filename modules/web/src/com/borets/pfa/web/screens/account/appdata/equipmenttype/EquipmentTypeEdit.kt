package com.borets.pfa.web.screens.account.appdata.equipmenttype

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.appdata.EquipmentType

@UiController("pfa_EquipmentType.edit")
@UiDescriptor("equipment-type-edit.xml")
@EditedEntityContainer("equipmentTypeDc")
@LoadDataBeforeShow
class EquipmentTypeEdit : StandardEditor<EquipmentType>()