package com.borets.pfa.web.screens.account.appdata.equipmentcategory

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.appdata.EquipmentCategory

@UiController("pfa_EquipmentCategory.edit")
@UiDescriptor("equipment-category-edit.xml")
@EditedEntityContainer("equipmentCategoryDc")
@LoadDataBeforeShow
class EquipmentCategoryEdit : StandardEditor<EquipmentCategory>()