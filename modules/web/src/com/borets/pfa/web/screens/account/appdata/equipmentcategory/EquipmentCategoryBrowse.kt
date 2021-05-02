package com.borets.pfa.web.screens.account.appdata.equipmentcategory

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.appdata.EquipmentCategory

@UiController("pfa_EquipmentCategory.browse")
@UiDescriptor("equipment-category-browse.xml")
@LookupComponent("equipmentCategoriesTable")
@LoadDataBeforeShow
class EquipmentCategoryBrowse : StandardLookup<EquipmentCategory>()