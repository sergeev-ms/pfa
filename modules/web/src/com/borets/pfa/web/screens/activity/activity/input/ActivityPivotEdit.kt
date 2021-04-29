package com.borets.pfa.web.screens.activity.activity.input

import com.borets.pfa.config.ActivityInputConfig
import com.borets.pfa.entity.activity.*
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.web.beans.PivotGridInitializer
import com.borets.pfa.web.beans.PivotGridInitializer.DynamicPropertyData
import com.borets.pfa.web.beans.PivotGridInitializer.StaticPropertyData
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.TimeSource
import com.haulmont.cuba.gui.components.GridLayout
import com.haulmont.cuba.gui.components.HasValue
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
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var messages: Messages
    @Inject
    private lateinit var timeSource: TimeSource
    @Inject
    private lateinit var userSession: UserSession
    @Inject
    private lateinit var activityInputConfig: ActivityInputConfig

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<ActivityDetail>

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


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        pivotGridHelper = AppBeans.getPrototype(PivotGridInitializer::class.java, pivotGrid)
        initFilter()
    }

    @Subscribe
    private fun onBeforeShow(event: BeforeShowEvent) {
        val pivotStaticProperties = listOf(
            StaticPropertyData("analytic", "", true, AnalyticSet::class.java, null, null, false),

            StaticPropertyData("contractType", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.contractType"),
                false, ContractType::class.java, null, null, true),
            StaticPropertyData("jobType", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.jobType"),
                true, JobType::class.java, null, null, true),
            StaticPropertyData("wellEquip", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.wellEquip"),
                true, WellEquip::class.java, null, null, true),
            StaticPropertyData("wellTag", messages.getMessage(AnalyticSet::class.java, "AnalyticSet.wellTag"),
                true, WellTag::class.java, null, null, true)
        )



        pivotGridHelper.initStaticPivotProperties(pivotStaticProperties)

        pivotGridHelper.setStaticPivotPropertiesValues(initKvEntities())

        pivotGridHelper.setStoreFunction() { key : Any, property : String, value : Any? ->
            var detail = detailsDc.mutableItems.find { it.analytic == key && property == it.getYearMonth().toString() }
            if (detail == null) {
                detail = dataContext.create(ActivityDetail::class.java).apply {
                    this.activity = editedEntity
                    this.analytic = key as AnalyticSet
                    this.setYearMonth(YearMonth.parse(property))
                }
            }
            detail!!.value = value as Int?
        }
    }

    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        initDynamic(editedEntity.year)
    }

    @Subscribe("yearField")
    private fun onYearFieldValueChange(event: HasValue.ValueChangeEvent<Integer>) {
        if (event.isUserOriginated)
            initDynamic(event.value as Int)
    }


    private fun initDynamic(year: Int?) {
        val monthQty = activityInputConfig.getDefaultMonthQty()
        val startMonth = YearMonth.of(year ?: timeSource.now().year, 1)
        val months : MutableList<YearMonth> = mutableListOf()
        for (monthNumber in 0..monthQty) {
            months.add(startMonth.plusMonths(monthNumber.toLong()))
        }
        months.map {
               DynamicPropertyData(it.toString(),
                it.format(DateTimeFormatter.ofPattern("MMM yy", userSession.locale)),
                Int::class.javaObjectType, TextField::class.java, "60px")

        }.let { dynamicProperties ->
            pivotGridHelper.initDynamicProperties(dynamicProperties)
            pivotGridHelper.setDynamicPropertiesValues { kvDc : KeyValueCollectionContainer ->
                detailsDc.items.forEach { detail ->
                    detail.value?.let { value ->
                        kvDc.items
                            .find { detail.analytic == it.getValue<AnalyticSet>("analytic") }
                            ?.setValue(detail.getYearMonth().toString(), value)
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