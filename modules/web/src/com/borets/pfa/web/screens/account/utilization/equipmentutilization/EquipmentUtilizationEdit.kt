package com.borets.pfa.web.screens.account.utilization.equipmentutilization

import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetail
import com.borets.pfa.web.beans.CountrySettingsBean
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_EquipmentUtilization.edit")
@UiDescriptor("equipment-utilization-edit.xml")
@EditedEntityContainer("equipmentUtilizationDc")
@LoadDataBeforeShow
class EquipmentUtilizationEdit : StandardEditor<EquipmentUtilization>() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var countrySettings: CountrySettingsBean

    private var copyFrom : EquipmentUtilization? = null

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        copyFrom = (event.options as? MapScreenOptions)?.params?.get("copyFrom") as EquipmentUtilization?
    }

    @Subscribe
    private fun onInitEntity(event: InitEntityEvent<EquipmentUtilization>) {
        createDetails(event.entity)
    }

    private fun createDetails(entity: EquipmentUtilization) {
        val breakdowns = countrySettings.getEquipmentTypesForUtilizationModel(editedEntity.account!!.country!!)
            .map {
                dataContext.create(EquipmentUtilizationDetail::class.java).apply {
                    this.equipmentUtilization = editedEntity
                    this.equipmentType = it

                    //copy details values from copyFrom
//                    copyFrom?.details?.let { copyFromDetails ->
//                        copyFromDetails.find { copyFromDetail -> copyFromDetail.equipmentType == it }
//                            ?.let { foundedDetail ->
//                                this.setRevenueMode(foundedDetail.getRevenueMode())
//                                this.firstRunValue = foundedDetail.firstRunValue
//                                this.sequentRunValue = foundedDetail.sequentRunValue
//                                this.sequentRunCompetitorValue = foundedDetail.sequentRunCompetitorValue
//                            }
//                    }
                }
            }
        entity.details = breakdowns.toMutableList()
    }
}