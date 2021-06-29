package com.borets.pfa.web.screens.account.utilization.equipmentutilization

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.haulmont.cuba.gui.model.CollectionLoader
import javax.inject.Inject

@UiController("pfa_EquipmentUtilization.browse")
@UiDescriptor("equipment-utilization-browse.xml")
@LookupComponent("equipmentUtilizationsTable")
@LoadDataBeforeShow
class EquipmentUtilizationBrowse : StandardLookup<EquipmentUtilization>() {
    @Inject
    private lateinit var equipmentUtilizationsDl: CollectionLoader<EquipmentUtilization>

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val account = (event.options as? MapScreenOptions)?.params?.get("account")
        equipmentUtilizationsDl.setParameter("account", account)
    }
}