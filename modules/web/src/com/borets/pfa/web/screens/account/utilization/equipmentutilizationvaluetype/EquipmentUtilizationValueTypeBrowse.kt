package com.borets.pfa.web.screens.account.utilization.equipmentutilizationvaluetype

import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_EquipmentUtilizationValueType.browse")
@UiDescriptor("equipment-utilization-value-type-browse.xml")
@LookupComponent("equipmentUtilizationValueTypesTable")
@LoadDataBeforeShow
class EquipmentUtilizationValueTypeBrowse : StandardLookup<EquipmentUtilizationValueType>()