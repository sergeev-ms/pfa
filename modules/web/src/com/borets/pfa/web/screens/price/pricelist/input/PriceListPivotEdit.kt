package com.borets.pfa.web.screens.price.pricelist.input

import com.borets.pfa.entity.activity.ContractType
import com.borets.pfa.entity.activity.JobType
import com.borets.pfa.entity.activity.WellEquip
import com.borets.pfa.entity.activity.WellTag
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.price.PriceListDetail
import com.borets.pfa.entity.price.RevenueType
import com.borets.pfa.web.beans.PivotGridInitializer
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.components.CurrencyField
import com.haulmont.cuba.gui.components.GridLayout
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.components.TextField
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.screen.*
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@UiController("pfa_PriceListPivot.edit")
@UiDescriptor("price-list-pivot-edit.xml")
@EditedEntityContainer("priceListDc")
@LoadDataBeforeShow
class PriceListPivotEdit : StandardEditor<PriceList>() {

    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var messages: Messages
    @Inject
    private lateinit var dataContext: DataContext

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<PriceListDetail>

    @Inject
    private lateinit var pivotGrid: GridLayout
    @Inject
    private lateinit var filterContractTypeField: LookupField<ContractType>
    @Inject
    private lateinit var filterJobTypeField: LookupField<JobType>
    @Inject
    private lateinit var filterWellEquipField: LookupField<WellEquip>
    @Inject
    private lateinit var filterWellTagField: LookupField<WellTag>

    private lateinit var pivotGridHelper: PivotGridInitializer


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        pivotGridHelper = AppBeans.getPrototype(PivotGridInitializer::class.java, pivotGrid)
        initFilter()
    }

    @Subscribe
    private fun onBeforeShow(event: BeforeShowEvent) {
        val pivotStaticProperties = listOf(
            PivotGridInitializer.StaticPropertyData("analytic", "", true, AnalyticSet::class.java, null, null, false),

            PivotGridInitializer.StaticPropertyData(
                "contractType", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.contractType"),
                false, ContractType::class.java, null, null, true
            ),
            PivotGridInitializer.StaticPropertyData(
                "jobType", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.jobType"),
                true, JobType::class.java, null, null, true
            ),
            PivotGridInitializer.StaticPropertyData(
                "wellEquip", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.wellEquip"),
                true, WellEquip::class.java, null, null, true
            ),
            PivotGridInitializer.StaticPropertyData(
                "wellTag", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.wellTag"),
                true, WellTag::class.java, null, null, true
            )
        )



        pivotGridHelper.initStaticPivotProperties(pivotStaticProperties)

        pivotGridHelper.setStaticPivotPropertiesValues(initKvEntities())

        pivotGridHelper.setStoreFunction() { key : Any, property : String, value : Any? ->
            var detail = detailsDc.mutableItems.find { it.analytic == key && property == it.revenueType!!.id.toString() }
            if (detail == null) {
                detail = dataContext.create(PriceListDetail::class.java).apply {
                    this.priceList = editedEntity
                    this.analytic = key as AnalyticSet
                    this.revenueType = dataManager.getReference(RevenueType::class.java, UUID.fromString(property))
                }
            }
            detail!!.value = value as BigDecimal?
        }
    }

    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        initDynamic()
    }

    private fun initDynamic() {
        dataManager.load(RevenueType::class.java).list()
            .map { PivotGridInitializer.DynamicPropertyData(
                it.id.toString(),
                it.name!!,
                BigDecimal::class.javaObjectType, CurrencyField::class.java, "70px")
            }
            .let { dynamicProperties ->
                pivotGridHelper.initDynamicProperties(dynamicProperties)
                pivotGridHelper.setDynamicPropertiesValues { kvDc : KeyValueCollectionContainer ->
                    detailsDc.items.forEach { detail ->
                        detail.value?.let { value ->
                            kvDc.items
                                .find { detail.analytic == it.getValue<AnalyticSet>("analytic") }
                                ?.setValue(detail.revenueType!!.id.toString(), value)
                        }
                    }
                }
            }
    }

    private fun initKvEntities(): List<KeyValueEntity> {
        return dataManager.load(AnalyticSet::class.java)
            .list()
            .map {
                KeyValueEntity().apply {
                    this.setValue("analytic", it)
                    this.setValue("contractType", it.getContractType())
                    this.setValue("jobType", it.getJobType())
                    this.setValue("wellEquip", it.getWellEquip())
                    this.setValue("wellTag", it.getWellTag())
                }
            }
    }

    private fun initFilter() {
        listOf(filterContractTypeField, filterJobTypeField, filterWellEquipField, filterWellTagField).forEach { field ->
            field.addValueChangeListener { _ ->
                pivotGridHelper.kvCollectionContainer.items.forEachIndexed { index, keyValueEntity ->
                    val isContract = filterContractTypeField.value?.let {
                        keyValueEntity.getValueEx<ContractType>("analytic.contractType") == it
                    } ?: true
                    val isJobType = filterJobTypeField.value?.let {
                        keyValueEntity.getValueEx<JobType>("analytic.jobType") == it
                    } ?: true
                    val isWellEquip = filterWellEquipField.value?.let {
                        keyValueEntity.getValueEx<WellEquip>("analytic.wellEquip") == it
                    } ?: true
                    val isWellTag = filterWellTagField.value?.let {
                        keyValueEntity.getValueEx<WellTag>("analytic.wellTag") == it
                    } ?: true

                    pivotGridHelper.setPivotRowVisibility(index + 1, isContract && isJobType && isWellEquip && isWellTag)
                }
            }
        }
    }
}