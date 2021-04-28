package com.borets.pfa.web.screens.activity.activity.input

import com.borets.pfa.config.ActivityInputConfig
import com.borets.pfa.entity.activity.*
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.web.beans.PivotGridInitializer
import com.google.common.reflect.TypeToken
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.TimeSource
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.GridLayout
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.components.TextField
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.security.global.UserSession
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@UiController("pfa_ActivityPivot.edit")
@UiDescriptor("activity-pivot-edit.xml")
@EditedEntityContainer("activityDc")
@LoadDataBeforeShow
class ActivityPivotEdit : StandardEditor<Activity>() {
    @Inject
    private lateinit var dataManager: DataManager

    @Inject
    private lateinit var pivotGrid: GridLayout

    @Inject
    private lateinit var filterWellEquipField: LookupField<WellEquip>
    @Inject
    private lateinit var filterJobTypeField: LookupField<JobType>
    @Inject
    private lateinit var filterContractTypeField: LookupField<ContractType>
    @Inject
    private lateinit var filterWellTagField: LookupField<WellTag>

    private lateinit var pivotGridHelper : PivotGridInitializer

    @Inject
    private lateinit var activityInputConfig: ActivityInputConfig

    @Inject
    private lateinit var timeSource: TimeSource

    @Inject
    private lateinit var userSession: UserSession

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<ActivityDetail>

    @Inject
    private lateinit var dataContext: DataContext

    @Inject
    private lateinit var uiComponents: UiComponents


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        pivotGridHelper = AppBeans.getPrototype(PivotGridInitializer::class.java, pivotGrid)
        initFilter()
    }

    @Subscribe
    private fun onBeforeShow(event: BeforeShowEvent) {
        val pivotStaticProperties = mapOf(
            Pair("contractType", ContractType::class), Pair("jobType", ContractType::class),
            Pair("wellEquip", WellEquip::class), Pair("wellTag", WellTag::class))

        pivotGridHelper.initStaticPivotProperties(pivotStaticProperties)
        pivotGridHelper.initStaticPivotPropertiesValues(initKvEntities())
        pivotGridHelper.setStoreFunction() { analytic : Any, property : String, value : Any? ->
            var detail = detailsDc.mutableItems.find { it.analytic == analytic && property == it.getYearMonth().toString() }
            if (detail == null) {
                detail = dataContext.create(ActivityDetail::class.java).apply {
                    this.activity = editedEntity
                    this.analytic = analytic as AnalyticSet
                    this.setYearMonth(YearMonth.parse(property))
                }
            }
            detail!!.value = value as Int?
        }
    }

    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        initDynamicPivotProperties()
    }

    private fun initDynamicPivotProperties() {
        val monthQty = activityInputConfig.getDefaultMonthQty()
        val startMonth = YearMonth.of(editedEntity.year ?: timeSource.now().year, 1)
        val months : MutableList<YearMonth> = mutableListOf()
        for (monthNumber in 0..monthQty) {
            months.add(startMonth.plusMonths(monthNumber.toLong()))
        }
        months.map {
               PivotGridInitializer.DynamicPropertyData<Int>(it.toString(),
                it.format(DateTimeFormatter.ofPattern("MMM yy", userSession.locale)),
                Int::class.javaObjectType, TextField::class.java, "60px")

        }.let { dynamicProperties ->
            pivotGridHelper.initDynamicColumnsCaptions(dynamicProperties)
            pivotGridHelper.initDynamicDcPivotProperties(dynamicProperties)
            pivotGridHelper.initDynamicPropertiesValues { kvDc : KeyValueCollectionContainer ->
                detailsDc.items.forEach { detail ->
                    detail.value?.let { value ->
                        kvDc.items
                            .find { detail.analytic == it.getValue<AnalyticSet>("analytic") }
                            ?.setValue(detail.getYearMonth().toString(), value)
                    }
                }
            }
            pivotGridHelper.initDynamicPivotPropertiesFields(dynamicProperties)
        }


    }

    private fun initKvEntities(): List<KeyValueEntity> {
        return dataManager.load(AnalyticSet::class.java)
            .list()
            .map {
                KeyValueEntity().apply {
                    this.setValue("contractType", it.getContractType())
                    this.setValue("jobType", it.getJobType())
                    this.setValue("wellEquip", it.getWellEquip())
                    this.setValue("wellTag", it.getWellTag())
                    //fixme: hardcoded
                    this.setValue("analytic", it)
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