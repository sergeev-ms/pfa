package com.borets.pfa.web.screens.price.pricelist.input

import com.borets.pfa.entity.activity.JobType
import com.borets.pfa.entity.activity.WellEquip
import com.borets.pfa.entity.activity.WellTag
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.price.PriceListDetail
import com.borets.pfa.entity.price.RevenueType
import com.borets.pfa.web.beans.PivotGridInitializer
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.gui.ScreenBuilders
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.screen.*
import java.math.BigDecimal
import java.time.YearMonth
import java.time.ZoneId
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
    private lateinit var entityStates: EntityStates
    @Inject
    private lateinit var metadataTools: MetadataTools
    @Inject
    private lateinit var screenBuilders: ScreenBuilders
    @Inject
    private lateinit var screenValidation: ScreenValidation

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<PriceListDetail>

    @Inject
    private lateinit var pivotGrid: GridLayout
    @Inject
    private lateinit var filterJobTypeField: LookupField<JobType>
    @Inject
    private lateinit var filterWellEquipField: LookupField<WellEquip>
    @Inject
    private lateinit var filterWellTagField: LookupField<WellTag>
    @Inject
    private lateinit var yearMonthField: DatePicker<Date>
    @Inject
    private lateinit var headerForm: Form

    private lateinit var pivotGridHelper: PivotGridInitializer


    @Subscribe
    private fun onAfterInit(@Suppress("UNUSED_PARAMETER") event: AfterInitEvent) {
        pivotGridHelper = AppBeans.getPrototype(PivotGridInitializer::class.java, pivotGrid)
        initFilter()

    }

    @Subscribe
    private fun onBeforeShow(@Suppress("UNUSED_PARAMETER") event: BeforeShowEvent) {
        initPivotGrid()
    }

    @Subscribe
    private fun onAfterShow(@Suppress("UNUSED_PARAMETER") event: AfterShowEvent) {
        initDynamic()
        setWindowCaption()
        editedEntity.getYearMonth()?.let {
            yearMonthField.value = Date.from(it
                .atDay(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant())
        }
    }

    @Subscribe("yearMonthField")
    private fun onYearMonthFieldValueChange(event: HasValue.ValueChangeEvent<Date>) {
        event.value.let {
            var yearMonth: YearMonth? = null
            if (it != null)
                yearMonth = YearMonth.from(it.toInstant().atZone(ZoneId.systemDefault()))
            editedEntity.setYearMonth(yearMonth)
        }
    }

    private fun initPivotGrid() {
        val pivotStaticProperties = listOf(
            PivotGridInitializer.StaticPropertyData("analytic", "", true, AnalyticSet::class.java, null, null, false),

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

        pivotGridHelper.setStoreFunction { key: Any, property: String, value: Any? ->
            var detail =
                detailsDc.mutableItems.find { it.analytic == key && property == it.revenueType!!.id.toString() }
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

    private fun initDynamic() {
        dataManager.load(RevenueType::class.java)
            .query("select e from pfa_RevenueType e order by e.order, e.name")
            .list()
            .map { PivotGridInitializer.DynamicPropertyData(
                it.id.toString(),
                it.name!!,
                it.fullName,
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

    private fun setWindowCaption() {
        if (!entityStates.isNew(editedEntity)) {
            window.caption = metadataTools.getInstanceName(editedEntity)
        }
    }

    @Subscribe("fillFromPrevBtn")
    private fun onFillFromPrevBtnClick(event: Button.ClickEvent) {
        if (!validate())
            return

        screenBuilders.lookup(PriceList::class.java, this)
            .withOpenMode(OpenMode.DIALOG)
            .withOptions(MapScreenOptions(mapOf(Pair("account", editedEntity.account))))
            .withSelectHandler { fillPrevData(it.first()) }
            .show()
    }

    private fun fillPrevData(copyFromPriceList: PriceList) {
        val view = ViewBuilder.of(PriceList::class.java)
            .addAll("rentalRate", "remoteMonitoring", "wellCheck")
            .add("details") {
                it.addView(View.LOCAL)
                    .add("analytic", View.MINIMAL)
                    .add("revenueType", View.MINIMAL)
            }.build()
        dataManager.reload(copyFromPriceList, view ).let { prevPrice ->
            editedEntity.rentalRate = prevPrice.rentalRate
            editedEntity.remoteMonitoring = prevPrice.remoteMonitoring
            editedEntity.wellCheck = prevPrice.wellCheck
            prevPrice.details?.map {
                dataContext.create(PriceListDetail::class.java).apply {
                    this.priceList = editedEntity
                    this.analytic = it.analytic
                    this.revenueType = it.revenueType
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