package com.borets.pfa.web.screens.account.utilization.equipmentutilizationvaluetype

import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_EquipmentUtilizationValueType.edit")
@UiDescriptor("equipment-utilization-value-type-edit.xml")
@EditedEntityContainer("equipmentUtilizationValueTypeDc")
@LoadDataBeforeShow
class EquipmentUtilizationValueTypeEdit : StandardEditor<EquipmentUtilizationValueType>()