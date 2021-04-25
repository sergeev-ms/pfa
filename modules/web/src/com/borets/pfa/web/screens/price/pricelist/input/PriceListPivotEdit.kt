package com.borets.pfa.web.screens.price.pricelist.input

import com.borets.pfa.entity.activity.ContractType
import com.borets.pfa.entity.activity.WellEquip
import com.borets.pfa.entity.activity.WellTag
import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.price.PriceList
import com.borets.pfa.entity.price.PriceListDetail
import com.borets.pfa.entity.price.RevenueType
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.model.*
import com.haulmont.cuba.gui.screen.Target
import com.vaadin.ui.themes.ValoTheme
import java.math.BigDecimal
import javax.inject.Inject

@UiController("pfa_PriceListPivot.edit")
@UiDescriptor("price-list-pivot-edit.xml")
@EditedEntityContainer("priceListDc")
@LoadDataBeforeShow
class PriceListPivotEdit : StandardEditor<PriceList>() {
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var uiComponents: UiComponents
    @Inject
    private lateinit var messages: Messages
    @Inject
    private lateinit var dataContext: DataContext

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<PriceListDetail>
    @Inject
    private lateinit var detailsPivotDc: KeyValueCollectionContainer

    @Inject
    private lateinit var pivotGrid: GridLayout

    private var pivotStaticProperties = mapOf(
        Pair("contractType", ContractType::class), Pair("jobType", ContractType::class),
        Pair("wellEquip", WellEquip::class), Pair("wellTag", WellTag::class))

    private var pivotGridRows = 1 //cause we have to skip header row

    @Subscribe
    private fun onBeforeShow(event: BeforeShowEvent) {
        initStaticPivotProperties()
        initStaticPivotPropertiesValue()
    }

    @Subscribe
    private fun onAfterShow(event: AfterShowEvent) {
        initDynamicPivotProperties()
    }


    private fun initStaticPivotProperties() {
        initStaticColumnsCaptions()
        pivotStaticProperties.forEach { (property, clazz) ->
            detailsPivotDc.addProperty(property, clazz.java)
        }
        //fixme: hardcoded
        detailsPivotDc.addProperty("analytic", AnalyticSet::class.java)
    }

    private fun initDynamicPivotProperties() {
        dataManager.load(RevenueType::class.java)
            .list()
            .toList()
            .let { revenueTypes ->
                initDynamicColumnsCaptions(revenueTypes.map { it.name!! })
                initDynamicDcPivotProperties(revenueTypes)
                initDynamicPivotPropertiesFields(revenueTypes)
            }
    }

    private fun initDynamicDcPivotProperties(revenueTypes: List<RevenueType>) {
        revenueTypes.forEach {
            detailsPivotDc.addProperty(it.name!!, BigDecimal::class.java)

            initDynamicPropertiesValues(it)
        }
    }

    private fun initDynamicPropertiesValues(revenueType: RevenueType) {
        detailsDc.items.forEach { detail ->
            if (detail.value != null) {
                detailsPivotDc.items
                    .find { detail.analytic == it.getValue<AnalyticSet>("analytic")
                            && detail.revenueType == revenueType}
                    ?.setValue(revenueType.name!!, detail.value)
            }
        }
    }

    private fun initStaticColumnsCaptions() {
        pivotGrid.columns = pivotStaticProperties.size
        var colsStartPosition = 0
        pivotStaticProperties.forEach { (property, _) ->
            val caption = messages.getMessage(AnalyticSet::class.java, "AnalyticSet.${property}")
            addColumnCaption(caption, colsStartPosition++)
        }
    }

    private fun initDynamicColumnsCaptions(captions: List<String>){
        var startCol = pivotGrid.columns
        pivotGrid.columns += captions.size
        captions.forEach {
            addColumnCaption(it, startCol++)
        }
    }

    private fun addColumnCaption(caption: String, startPosition: Int) {
        @Suppress("UnstableApiUsage")
        uiComponents.create(Label.TYPE_STRING).apply {
            this.value = caption
            this.addStyleName(ValoTheme.LABEL_H3)
            this.alignment = Component.Alignment.MIDDLE_CENTER
        }.let {
            pivotGrid.add(it, startPosition, 0)
        }
    }

    private fun initStaticPivotPropertiesValue() {
        dataManager.load(AnalyticSet::class.java)
            .list()
            .forEach {
                detailsPivotDc.mutableItems.add(
                    KeyValueEntity().apply {
                        this.setValue("contractType", it.getContractType())
                        this.setValue("jobType", it.getJobType())
                        this.setValue("wellEquip", it.getWellEquip())
                        this.setValue("wellTag", it.getWellTag())
                        //fixme: hardcoded
                        this.setValue("analytic", it)
                    }
                )
            }
    }

    private fun initDynamicPivotPropertiesFields(revenueTypes: List<RevenueType>) {
        val skipRows = 1 //avoid captions
        val skipColumns = pivotStaticProperties.size //avoid static fields
        detailsPivotDc.items.forEachIndexed { rowIndex, keyValueEntity ->
            revenueTypes.forEachIndexed { colIndex, revenueType ->
                @Suppress("UnstableApiUsage")
                uiComponents.create(CurrencyField.TYPE_BIGDECIMAL).apply {
                    this.value = keyValueEntity.getValue(revenueType.name!!)
                    this.setWidth("85px")
                    this.addValueChangeListener {
                        keyValueEntity.setValue(revenueType.name!!, it.value) //set "visual" value
                        setStoredValue(keyValueEntity.getValue("analytic")!!, revenueType, it.value) //set "real" value
                    }
                }.also {
                    pivotGrid.add(it, colIndex + skipColumns, rowIndex +skipRows)
                }
            }
        }
    }



    @Subscribe(id = "detailsPivotDc", target = Target.DATA_CONTAINER)
    private fun onDetailsPivotDcCollectionChange(event: CollectionContainer.CollectionChangeEvent<KeyValueEntity>) {
        when (event.changeType) {
            CollectionChangeType.ADD_ITEMS -> {
                addRows(event.changes)
            }
        }
    }

    private fun addRows(changes: Collection<KeyValueEntity>) {
        pivotGrid.rows = pivotGrid.rows + changes.size
        changes.forEach {
            addColumns(it)
            pivotGridRows++
        }
    }

    private fun addColumns(keyValueEntity: KeyValueEntity) {
        var cols = 0
        pivotStaticProperties.forEach { (property, _) ->
            @Suppress("UnstableApiUsage")
            uiComponents.create(Label.TYPE_STRING).apply {
                this.alignment = Component.Alignment.MIDDLE_LEFT
                this.value = keyValueEntity.getValue(property)
            }.also {
                pivotGrid.add(it, cols++, pivotGridRows)
            }
        }
    }

    // tried to use ItemPropertyChangeEvent<KeyValueEntity> but isn't working
    private fun setStoredValue(analytic: AnalyticSet, revenueType: RevenueType, value: BigDecimal?) {
        var detail = detailsDc.mutableItems.find { it.analytic == analytic && revenueType == it.revenueType }
        if (detail == null) {
            detail = dataContext.create(PriceListDetail::class.java).apply {
                this.priceList = editedEntity
                this.analytic = analytic
                this.revenueType = revenueType
            }
        }
        detail!!.value = value
    }
}