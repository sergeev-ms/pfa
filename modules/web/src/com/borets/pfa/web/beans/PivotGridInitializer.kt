package com.borets.pfa.web.beans

import com.borets.pfa.entity.analytic.AnalyticSet
import com.haulmont.chile.core.datatypes.DatatypeRegistry
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.components.data.HasValueSource
import com.haulmont.cuba.gui.model.CollectionChangeType
import com.haulmont.cuba.gui.model.DataComponents
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.vaadin.ui.themes.ValoTheme
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.inject.Inject
import kotlin.reflect.KClass

@Component(PivotGridInitializer.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class PivotGridInitializer(private var pivotGrid: GridLayout) {
    companion object {
        const val NAME = "pfa_PivotGridInitializer"
    }

    @Inject
    private lateinit var dataComponents: DataComponents
    @Inject
    private lateinit var messages: Messages
    @Inject
    private lateinit var uiComponents: UiComponents

    lateinit var kvCollectionContainer: KeyValueCollectionContainer
        private set

    lateinit var pivotStaticProperties: Map<String, KClass<out Any>>

    private lateinit var storeFunction: (Any, String, Any?) -> Unit

    private var pivotGridRows = 1 //cause we have to skip header row

    @Inject
    private lateinit var datatypeRegistry: DatatypeRegistry

    @PostConstruct
    fun postConstruct() {
        kvCollectionContainer = dataComponents.createKeyValueCollectionContainer()
        kvCollectionContainer.addCollectionChangeListener { event ->
            when (event.changeType) {
                CollectionChangeType.ADD_ITEMS -> {
                    addRows(event.changes)
                }
                else -> {} //todo:
            }
        }
    }

    fun setPivotRowVisibility(rowIndex: Int, visible: Boolean) {
        for (colIndex in 0 until pivotGrid.columns) {
            pivotGrid.getComponentNN(colIndex, rowIndex).isVisible = visible
        }

    }

    fun initStaticPivotProperties(pivotStaticProperties: Map<String, KClass<out Any>>) {
        this.pivotStaticProperties = pivotStaticProperties

        initStaticColumnsCaptions(pivotStaticProperties)

        pivotStaticProperties.forEach { (property, clazz) ->
            kvCollectionContainer.addProperty(property, clazz.java)
        }
        //fixme: hardcoded
        kvCollectionContainer.addProperty("analytic", AnalyticSet::class.java)
    }

    private fun initStaticColumnsCaptions(pivotStaticProperties: Map<String, KClass<out Any>>) {
        pivotGrid.columns = pivotStaticProperties.size
        var colsStartPosition = 0
        pivotStaticProperties.forEach { (property, _) ->
            val caption = messages.getMessage(AnalyticSet::class.java, "AnalyticSet.${property}")
            addColumnCaption(caption, colsStartPosition++)
        }
    }

    private fun addColumnCaption(caption: String, startPosition: Int) {
        @Suppress("UnstableApiUsage")
        uiComponents.create(Label.TYPE_STRING).apply {
            this.value = caption
            this.addStyleName(ValoTheme.LABEL_H3)
            this.alignment = com.haulmont.cuba.gui.components.Component.Alignment.MIDDLE_CENTER
        }.let {
            pivotGrid.add(it, startPosition, 0)
        }
    }

    fun initStaticPivotPropertiesValues(initialEntities: List<KeyValueEntity>) {
        kvCollectionContainer.mutableItems.addAll(initialEntities)
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
                this.alignment = com.haulmont.cuba.gui.components.Component.Alignment.MIDDLE_LEFT
                this.value = keyValueEntity.getValue(property)
            }.also {
                pivotGrid.add(it, cols++, pivotGridRows)
            }
        }
    }

    fun <T> initDynamicColumnsCaptions(properties: List<DynamicPropertyData<T>>) {
        var startCol = pivotGrid.columns
        pivotGrid.columns += properties.size
        properties.forEach {
            addColumnCaption(it.caption, startCol++)
        }
    }

    fun <T> initDynamicDcPivotProperties(properties: List<DynamicPropertyData<T>>){
        properties.forEach {
            kvCollectionContainer.addProperty(it.property, it.clazz)
        }
    }

    fun initDynamicPropertiesValues(function: (KeyValueCollectionContainer) -> Unit) {
        function.invoke(kvCollectionContainer)
    }

    fun <T> initDynamicPivotPropertiesFields(dynamicProperties: List<DynamicPropertyData<T>>) {

        val skipRows = 1 //avoid captions
        val skipColumns = pivotStaticProperties.size //avoid static fields
        kvCollectionContainer.items.forEachIndexed { rowIndex, keyValueEntity ->
            dynamicProperties.forEachIndexed { colIndex, propertyData ->
                @Suppress("UnstableApiUsage")
                val field = uiComponents.create(propertyData.fieldType).apply {
                    this.value = keyValueEntity.getValue<T>(propertyData.property)?.toString()
                    this.setWidth(propertyData.fieldWidth)
                    this.addValueChangeListener {
                        keyValueEntity.setValue(propertyData.property, it.value)
                        storeFunction.invoke(keyValueEntity.getValue<AnalyticSet>("analytic")!! , propertyData.property, it.value)
                        return@addValueChangeListener
                    }
                }
                pivotGrid.add(field, colIndex + skipColumns, rowIndex + skipRows)
            }
        }

    }

    fun setStoreFunction(function: (Any, String, Any?) -> Unit) {
        this.storeFunction = function
    }


    class DynamicPropertyData<T>(
        var property: String,
        var caption: String,
        var clazz: Class<T>,
        var fieldType: Class<out HasValue<*>>,
        var fieldWidth: String
    )
}