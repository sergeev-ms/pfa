package com.borets.pfa.web.screens.account.utilization.equipmentutilization

import com.borets.pfa.entity.account.utilization.EquipmentUtilization
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetail
import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetailValue
import com.borets.pfa.web.beans.CountrySettingsBean
import com.haulmont.cuba.gui.model.CollectionContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_EquipmentUtilization.edit")
@UiDescriptor("equipment-utilization-edit.xml")
@EditedEntityContainer("equipmentUtilizationDc")
class EquipmentUtilizationEdit : StandardEditor<EquipmentUtilization>() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var countrySettings: CountrySettingsBean

    @Inject
    private lateinit var equipmentUtilizationDetailValueDc: CollectionContainer<EquipmentUtilizationDetailValue>


    private var copyFrom: EquipmentUtilization? = null

    @javax.inject.Inject
    private lateinit var equipmentUtilizationDl: com.haulmont.cuba.gui.model.InstanceLoader<com.borets.pfa.entity.account.utilization.EquipmentUtilization>

    @javax.inject.Inject
    private lateinit var equipmentUtilizationDetailValueDl: com.haulmont.cuba.gui.model.CollectionLoader<com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetailValue>

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        copyFrom = (event.options as? MapScreenOptions)?.params?.get("copyFrom") as EquipmentUtilization?
    }

    @Subscribe
    private fun onInitEntity(event: InitEntityEvent<EquipmentUtilization>) {
        createDetails(event.entity)
    }

    @Subscribe
    private fun onBeforeShow(event: BeforeShowEvent) {
        //early load and the order is important!
        equipmentUtilizationDetailValueDl.setParameter("container_equipmentUtilizationDc", editedEntity)
        equipmentUtilizationDetailValueDl.load()
        equipmentUtilizationDl.load()
    }


    private fun createDetails(entity: EquipmentUtilization) {
        countrySettings.getEquipmentTypesForUtilizationModel(editedEntity.account!!.country!!)
            .map {
                return@map dataContext.create(EquipmentUtilizationDetail::class.java).apply {
                    this.equipmentUtilization = entity
                    this.equipmentType = it
                }.copyDetail {
                    copyFrom?.details?.let { copyFromDetails ->
                        copyFromDetails.find { copyFromDetail -> copyFromDetail.equipmentType == it }
                    }
                }
            }.let {
                entity.details = it.toMutableList()
            }

    }

    private inline fun EquipmentUtilizationDetail.copyDetail(function: () -> EquipmentUtilizationDetail?)
            : EquipmentUtilizationDetail {
        val copyFrom = function.invoke()
        this.setRevenueMode(copyFrom?.getRevenueMode())
        this.values = copyFrom?.values?.map {
            dataContext.create(EquipmentUtilizationDetailValue::class.java).apply {
                this.detail = this@copyDetail
                this.valueType = it.valueType
                this.value = it.value
            }
        }?.toMutableList()
            ?.also {
                equipmentUtilizationDetailValueDc.mutableItems.addAll(it)
            }
        return this
    }
}
