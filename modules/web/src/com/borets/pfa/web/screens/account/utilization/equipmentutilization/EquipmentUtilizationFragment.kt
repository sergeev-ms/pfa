package com.borets.pfa.web.screens.account.utilization.equipmentutilization

import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.utilization.*
import com.borets.pfa.entity.activity.RecordType
import com.borets.pfa.web.beans.CountrySettingsBean
import com.borets.pfa.web.beans.PivotGridInitializer
import com.haulmont.chile.core.datatypes.DatatypeRegistry
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.GridLayout
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.components.TextField
import com.haulmont.cuba.gui.model.*
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@UiController("pfa_EquipmentUtilizationFragment")
@UiDescriptor("equipment-utilization-fragment.xml")
class EquipmentUtilizationFragment : ScreenFragment() {
    @Inject
    private lateinit var messages: Messages
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var countrySettingsBean: CountrySettingsBean
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var datatypeRegistry: DatatypeRegistry

    @Inject
    private lateinit var equipmentUtilizationDc: InstanceContainer<EquipmentUtilization>
    @Inject
    private lateinit var utilizationDetailsDc: CollectionPropertyContainer<EquipmentUtilizationDetail>
    @Inject
    private lateinit var equipmentUtilizationDetailValueDc: CollectionContainer<EquipmentUtilizationDetailValue>


    @Inject
    private lateinit var validFromField: DatePicker<LocalDate>
    @Inject
    private lateinit var recordTypeField: LookupField<RecordType>
    @Inject
    private lateinit var pivotGrid: GridLayout

    private lateinit var pivotGridHelper: PivotGridInitializer

    private var pivotGridEditable: Boolean = true


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        pivotGridHelper = AppBeans.getPrototype(PivotGridInitializer::class.java, pivotGrid)
        pivotGridHelper.initStaticPivotProperties(prepareStaticProperties())
    }

    @Subscribe(target = Target.PARENT_CONTROLLER)
    private fun onAfterShow(event: Screen.AfterShowEvent) {
        equipmentUtilizationDc.itemOrNull?.let {
            initPivot()
        }
    }


    fun initPivot() {
        initStatic()
        initDynamic()
    }

    private fun initStatic() {
        pivotGridHelper.setStaticPivotPropertiesValues(initKvEntities())

        pivotGridHelper.setStoreFunction { key: Any, property: String, value: Any? ->
            if (property == "revenueMode") {
                (key as EquipmentUtilizationDetail).setValue(property, value)
            } else {
                var detailValue = equipmentUtilizationDetailValueDc.mutableItems
                    .find { it.detail == key && property == it.valueType!!.id.toString() }
                if (detailValue == null) {
                    detailValue = dataContext.create(EquipmentUtilizationDetailValue::class.java).apply {
                        this.detail = key as EquipmentUtilizationDetail
                        this.valueType =
                            dataManager.getReference(EquipmentUtilizationValueType::class.java, UUID.fromString(property)
                            )
                    }
                }
                detailValue!!.value = value as BigDecimal?
            }
        }
    }

    private fun initDynamic() {
        countrySettingsBean.getEquipmentUtilizationDetailValueType(equipmentUtilizationDc.item.account!!.country!!)
            .map { PivotGridInitializer.DynamicPropertyData(
                it.id.toString(),
                it.name!!,
                null,
                BigDecimal::class.javaObjectType,
                TextField::class.java,
                "100px",
                { pivotGridEditable },
                false,
                datatypeRegistry.get("percentage")
                )
            }
            .let { dynamicProperties ->
                pivotGridHelper.initDynamicProperties(dynamicProperties)
                pivotGridHelper.setDynamicPropertiesValues { kvDc : KeyValueCollectionContainer ->
                    equipmentUtilizationDetailValueDc.items.forEach { detailValue ->
                        detailValue.value?.let { value ->
                            kvDc.items
                                .find { detailValue.detail == it.getValue<EquipmentUtilizationDetail>("detail") }
                                ?.setValue(detailValue.valueType!!.id.toString(), value)
                        }
                    }
                }
            }
    }

    private fun prepareStaticProperties(): List<PivotGridInitializer.StaticPropertyData> {
        return listOf(
            PivotGridInitializer.StaticPropertyData(
                "detail", "", true,
                EquipmentUtilizationDetail::class.java, null, null, false
            ),

            PivotGridInitializer.StaticPropertyData(
                "equipmentType",
                messages.getMessage(EquipmentUtilizationDetail::class.java, "EquipmentUtilizationDetail.equipmentType"),
                true, EquipmentType::class.java, null, null
            ),
            PivotGridInitializer.StaticPropertyData(
                "revenueMode",
                messages.getMessage(EquipmentUtilizationDetail::class.java, "EquipmentUtilizationDetail.revenueMode"),
                true, RevenueMode::class.java, LookupField::class.java, null, true,
                { pivotGridEditable }, true
            )
        )
    }

    private fun initKvEntities(): List<KeyValueEntity> {
        return utilizationDetailsDc.items
            .map {
                KeyValueEntity().apply {
                    this.setValue("detail", it)
                    this.setValue("equipmentType", it.equipmentType)
                    this.setValue("revenueMode", it.getRevenueMode())
                }
            }
    }

    fun setEditable(editable : Boolean) {
        validFromField.isEditable = editable
        recordTypeField.isEditable = editable
        pivotGridEditable = editable
    }
}