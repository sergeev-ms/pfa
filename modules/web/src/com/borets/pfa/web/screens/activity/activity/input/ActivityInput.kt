package com.borets.pfa.web.screens.activity.activity.input

import com.borets.pfa.datatype.YearMonthDataType
import com.haulmont.chile.core.datatypes.Datatype
import com.haulmont.chile.core.datatypes.DatatypeRegistry
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.cuba.core.app.keyvalue.KeyValueMetaProperty
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.TimeSource
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.components.data.ValueSource
import com.haulmont.cuba.gui.components.data.options.ListOptions
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.web.gui.components.renderers.WebComponentRenderer
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject

@UiController("pfa_ActivityInput")
@UiDescriptor("activity-input.xml")
@LoadDataBeforeShow
class ActivityInput : Screen() {
    @Inject
    private lateinit var timeSource: TimeSource

    @Inject
    private lateinit var detailsDc: KeyValueCollectionContainer

    @Inject
    private lateinit var yearField: LookupField<Int>

    @Inject
    private lateinit var yearMonthField: LookupField<YearMonth>

    @Inject
    private lateinit var detailDg: DataGrid<KeyValueEntity>

    @Inject
    private lateinit var metadata: Metadata

    @Inject
    private lateinit var datatypeRegistry: DatatypeRegistry

    @Inject
    private lateinit var uiComponents: UiComponents


    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        initYearsRange()

        for (i in 0..4) {
            val element = KeyValueEntity().apply {
                this.setValue("value", i)
            }
            detailsDc.mutableItems.add(element)
        }
    }

    @Subscribe("yearField")
    private fun onYearFieldValueChange(event: HasValue.ValueChangeEvent<Int>) {
        event.value?.let {
            val options = mutableListOf<YearMonth>()
            for (i in 1..12) {
                options.add(YearMonth.of(it, i))
            }
            yearMonthField.options = ListOptions(options)
        }
    }

    @Subscribe("yearMonthField")
    private fun onYearMonthFieldValueChange(event: HasValue.ValueChangeEvent<YearMonth>) {
        event.value?.let {
            addMonthsToDetails(it, 7)
        }
    }

    private fun initYearsRange() {
        timeSource.now().year.let {
            yearField.options = ListOptions(it.rangeTo(it + 1).toList())
        }
    }


    private fun addMonthsToDetails(startMonth: YearMonth, qty: Int) {
        for (i in 0..qty) {
            val month = startMonth.plusMonths(i.toLong())
            detailsDc.addProperty(month.toString(), Integer::class.java)
            val property = KeyValueMetaProperty(metadata.getClass(KeyValueEntity::class.java),
                    month.toString(), Integer::class.java)
//            detailDg.addColumn(month.toString(), MetaPropertyPath(metadata.getClass(KeyValueEntity::class.java), property)).apply {
//                this.isEditable = true
//                this.setEditFieldGenerator {
//                    @Suppress("UnstableApiUsage")
//                    val textField = uiComponents.create(TextField.TYPE_BIGDECIMAL)
//                    textField.valueSource = it.valueSourceProvider.getValueSource(month.toString()) as ValueSource<BigDecimal>
//                    return@setEditFieldGenerator textField
//                }
//            }
            detailDg.addGeneratedColumn(month.toString(),
                    object : DataGrid.ColumnGenerator<KeyValueEntity, Component> {
                        override fun getValue(event: DataGrid.ColumnGeneratorEvent<KeyValueEntity>): Component {
                            @Suppress("UnstableApiUsage")
                            val textField = uiComponents.create(TextField.TYPE_BIGDECIMAL).apply {
                                setWidthFull()
                                addValueChangeListener {
                                    event.item.setValue(month.toString(), it.value)
                                }
                            }
                            return textField
                        }

                        override fun getType(): Class<Component> {
                            return Component::class.java
                        }
                    })
                    .apply {
                        renderer = WebComponentRenderer<KeyValueEntity>()
                    }


        }
    }

}

