package com.borets.pfa.web.screens.activity.activity.input

import com.borets.pfa.entity.activity.*
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.web.beans.CountrySettingsBean
import com.borets.pfa.web.beans.PivotGridInitializer
import com.borets.pfa.web.beans.PivotGridInitializer.DynamicPropertyData
import com.borets.pfa.web.beans.PivotGridInitializer.StaticPropertyData
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import com.haulmont.cuba.security.global.UserSession
import java.time.*
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
    private lateinit var userSession: UserSession
    @Inject
    private lateinit var metadataTools: MetadataTools
    @Inject
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var screenBuilders: ScreenBuilders
    @Inject
    private lateinit var screenValidation: ScreenValidation
    @Inject
    private lateinit var countrySettings: CountrySettingsBean

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<ActivityDetail>

    @Inject
    private lateinit var pivotGrid: GridLayout
    @Inject
    private lateinit var filterWellEquipField: LookupField<WellEquip>
    @Inject
    private lateinit var filterJobTypeField: LookupField<JobType>
    @Inject
    private lateinit var filterWellTagField: LookupField<WellTag>
    @Inject
    private lateinit var headerForm: Form

    private lateinit var pivotGridHelper : PivotGridInitializer



    @Subscribe
    private fun onAfterInit(@Suppress("UNUSED_PARAMETER") event: AfterInitEvent) {
        pivotGridHelper = AppBeans.getPrototype(PivotGridInitializer::class.java, pivotGrid)
        initFilter()
    }

    @Subscribe
    private fun onInitEntity(event: InitEntityEvent<Activity>) {
        event.entity.periodFrom = YearMonth.now().withMonth(1).atDay(1)
        event.entity.periodTo = YearMonth.now().withMonth(12).atEndOfMonth()
    }


    @Subscribe
    private fun onAfterShow(@Suppress("UNUSED_PARAMETER") event: AfterShowEvent) {
        initPivotGrid()
        initDynamic()
        setWindowCaption()
    }


    @Subscribe(id = "activityDc", target = Target.DATA_CONTAINER)
    private fun onActivityDcItemPropertyChange(event: InstanceContainer.ItemPropertyChangeEvent<Activity>) {
        if ((event.property == "periodFrom" || event.property == "periodTo") && event.value != null) {
            initDynamic()
        }
    }

    private fun initPivotGrid() {
        val pivotStaticProperties = listOf(
            StaticPropertyData("analytic", "", true, AnalyticSet::class.java, null, null, false),

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

    private fun setWindowCaption() {
        val value : String =
            if (entityStates.isNew(editedEntity)) {
                "New"
            } else {
                metadataTools.getInstanceName(editedEntity)
            }
        window.caption = window.caption?.format(value)
    }


    private fun initDynamic() {
        var startMonth = YearMonth.from(editedEntity.periodFrom)
        val endMonth = YearMonth.from(editedEntity.periodTo)

        val months : MutableList<YearMonth> = mutableListOf()

        while (startMonth <= endMonth) {
            months.add(startMonth)
            startMonth = startMonth.plus(Period.ofMonths(1))
        }

        months.map {
            DynamicPropertyData(it.toString(), it.format(DateTimeFormatter.ofPattern("MMM yy", userSession.locale)), null,
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
        return countrySettings.getActivityAnalyticSets(editedEntity.account!!.country!!)
            .map {
                KeyValueEntity().apply {
                    this.setValue("analytic", it)
                    this.setValue("jobType", it.getJobType())
                    this.setValue("wellEquip", it.getWellEquip())
                    this.setValue("wellTag", it.getWellTag())
                }
            }
    }


    private fun initFilter() {
        listOf(filterJobTypeField, filterWellEquipField, filterWellTagField).forEach { field ->
            field.addValueChangeListener { _ ->
                pivotGridHelper.kvCollectionContainer.items.forEachIndexed { index, keyValueEntity ->
                    val isJobType = filterJobTypeField.value?.let {
                        keyValueEntity.getValueEx<JobType>("analytic.jobType") == it
                    } ?: true
                    val isWellEquip = filterWellEquipField.value?.let {
                        keyValueEntity.getValueEx<WellEquip>("analytic.wellEquip") == it
                    } ?: true
                    val isWellTag = filterWellTagField.value?.let {
                        keyValueEntity.getValueEx<WellTag>("analytic.wellTag") == it
                    } ?: true

                    pivotGridHelper.setPivotRowVisibility(index + 1, isJobType && isWellEquip && isWellTag)
                }
            }
        }
    }

    @Subscribe("fillFromPrevBtn")
    private fun onFillFromPrevBtnClick(event: Button.ClickEvent) {
        if (!validate())
            return

        screenBuilders.lookup(Activity::class.java, this)
            .withOpenMode(OpenMode.DIALOG)
            .withOptions(MapScreenOptions(mapOf(
                Pair("account", editedEntity.account),
                Pair("year", editedEntity.year)))
            )
            .withSelectHandler { fillPrevData(it.first()) }
            .show()
    }

    private fun fillPrevData(copyFromActivityPlan: Activity) {
        val view = ViewBuilder.of(Activity::class.java)
            .add("details") {
                it.addView(View.LOCAL)
                    .add("analytic", View.MINIMAL)
            }.build()
        dataManager.reload(copyFromActivityPlan, view ).let { prevAct ->
            prevAct.details?.map {
                dataContext.create(ActivityDetail::class.java).apply {
                    this.activity = editedEntity
                    this.analytic = it.analytic
                    this.year = it.year
                    this.month = it.month
                    this.value = it.value
                }
            }?.forEach { detailsDc.mutableItems.add(it) }

            initDynamic()
        }
    }

    private fun validate() : Boolean {
        screenValidation.validateUiComponents(headerForm).also {
            return if (!it.isEmpty) {
                screenValidation.showValidationErrors(this, it)
                false
            } else true
        }
    }
}