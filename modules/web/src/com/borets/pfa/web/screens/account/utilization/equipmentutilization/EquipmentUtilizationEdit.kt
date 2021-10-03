package com.borets.pfa.web.screens.account.utilization.equipmentutilization

import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetail
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.EntityStates
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_EquipmentUtilization.edit")
@UiDescriptor("equipment-utilization-edit.xml")
@EditedEntityContainer("equipmentUtilizationDc")
@LoadDataBeforeShow
class EquipmentUtilizationEdit : StandardEditor<EquipmentUtilization>() {
    @Inject
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var dataContext: DataContext

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<EquipmentUtilizationDetail>

    private var copyFrom : EquipmentUtilization? = null

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        copyFrom = (event.options as? MapScreenOptions)?.params?.get("copyFrom") as EquipmentUtilization?
    }

    @Subscribe
    private fun onAfterShow(@Suppress("UNUSED_PARAMETER") event: AfterShowEvent) {
        if (entityStates.isNew(editedEntity)) {
            createDetails()
        }
    }

    private fun createDetails() {
        val breakdowns = dataManager.load(EquipmentType::class.java)
            .query("order by e.order")
            .list()
            .map {
                dataContext.create(EquipmentUtilizationDetail::class.java).apply {
                    this.equipmentUtilization = editedEntity
                    this.equipmentType = it

                    //copy details values from copyFrom
                    copyFrom?.details?.let { copyFromDetails ->
                        copyFromDetails.find { copyFromDetail -> copyFromDetail.equipmentType == it }
                            ?.let { foundedDetail ->
                                this.setRevenueMode(foundedDetail.getRevenueMode())
                                this.firstRunValue = foundedDetail.firstRunValue
                                this.sequentRunValue = foundedDetail.sequentRunValue
                                this.sequentRunCompetitorValue = foundedDetail.sequentRunCompetitorValue
                            }
                    }
                }
            }
        detailsDc.mutableItems.addAll(breakdowns)
    }
}