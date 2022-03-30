package com.borets.pfa.web.screens.account.supplementary.supplementary

import com.borets.pfa.entity.account.supplementary.Supplementary
import com.borets.pfa.entity.account.supplementary.SupplementaryDetail
import com.borets.pfa.entity.account.supplementary.SupplementaryDetailType
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.web.beans.PivotGridInitializer
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.GridLayout
import com.haulmont.cuba.gui.components.TextField
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import com.haulmont.cuba.security.global.UserSession
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@UiController("pfa_SupplementaryFragment")
@UiDescriptor("supplementary-fragment.xml")
class SupplementaryFragment : ScreenFragment() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var userSession: UserSession

    @Inject
    private lateinit var supplementaryDc: InstanceContainer<Supplementary>
    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<SupplementaryDetail>

    @Inject
    private lateinit var pivotGrid: GridLayout
    @Inject
    private lateinit var periodFromField: DatePicker<LocalDate>
    @Inject
    private lateinit var periodToField: DatePicker<LocalDate>


    private lateinit var pivotGridHelper : PivotGridInitializer

    @Subscribe
    private fun onAfterInit(@Suppress("UNUSED_PARAMETER") event: AfterInitEvent) {
        pivotGridHelper = AppBeans.getPrototype(PivotGridInitializer::class.java, pivotGrid)
    }

    @Subscribe(target = Target.PARENT_CONTROLLER)
    private fun onBeforeShow(@Suppress("UNUSED_PARAMETER") event: Screen.BeforeShowEvent) {
        val pivotStaticProperties = listOf(
            PivotGridInitializer.StaticPropertyData("type", "Type", true,
                SupplementaryDetailType::class.java, null, null, true),
        )

        pivotGridHelper.initStaticPivotProperties(pivotStaticProperties)

        pivotGridHelper.setStaticPivotPropertiesValues(initKvEntities())

        pivotGridHelper.setStoreFunction() { key : Any, property : String, value : Any? ->
            var detail = detailsDc.mutableItems.find { it.type == key && property == it.getYearMonth().toString() }
            if (detail == null) {
                detail = dataContext.create(SupplementaryDetail::class.java).apply {
                    this.supplementary = supplementaryDc.item
                    this.type = (key as SupplementaryDetailType)
                    this.setYearMonth(YearMonth.parse(property))
                }
            }
            detail!!.value = value as BigDecimal?
        }
    }

    @Subscribe(id = "supplementaryDc", target = Target.DATA_CONTAINER)
    private fun onSupplementaryDcItemChange(@Suppress("UNUSED_PARAMETER") event: InstanceContainer.ItemChangeEvent<Supplementary>) {
        setPeriod()
        periodFromField.addValueChangeListener { initDynamic() }
        periodToField.addValueChangeListener { initDynamic() }
        initDynamic()
    }


    private fun initKvEntities(): List<KeyValueEntity> {
        return dataManager.load(SupplementaryDetailType::class.java)
            .list()
            .map {
                KeyValueEntity().apply {
                    this.setValue("type", it)
                }
            }
    }

    private fun initDynamic() {
        var startMonth = YearMonth.from(periodFromField.value!!)
        val endMonth = YearMonth.from(periodToField.value!!)

        val months : MutableList<YearMonth> = mutableListOf()

        while (startMonth <= endMonth) {
            months.add(startMonth)
            startMonth = startMonth.plus(Period.ofMonths(1))
        }

        months.map {
            PivotGridInitializer.DynamicPropertyData(
                it.toString(), it.format(DateTimeFormatter.ofPattern("MMM yy", userSession.locale)), null,
                BigDecimal::class.javaObjectType, TextField::class.java, "60px"
            )
        }.let { dynamicProperties ->
            pivotGridHelper.initDynamicProperties(dynamicProperties)
            pivotGridHelper.setDynamicPropertiesValues { kvDc : KeyValueCollectionContainer ->
                detailsDc.items.forEach { detail ->
                    detail.value?.let { value ->
                        kvDc.items
                            .find { detail.type == it.getValue<SupplementaryDetailType>("type") }
                            ?.setValue(detail.getYearMonth().toString(), value)
                    }
                }
            }
        }
    }

    private fun setPeriod() {
        val minYearMonth = detailsDc.items.map { it.getYearMonth() }.minByOrNull { it!! }
            ?: YearMonth.now().withMonth(1)
        val maxYearMonth = detailsDc.items.map { it.getYearMonth() }.maxByOrNull { it!! }
            ?: YearMonth.now().withMonth(12)

        periodFromField.value = minYearMonth.atDay(1)
        periodToField.value = maxYearMonth.atDay(1)
    }

}