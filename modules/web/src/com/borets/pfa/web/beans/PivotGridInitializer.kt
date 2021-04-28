package com.borets.pfa.web.beans

import com.haulmont.cuba.core.app.keyvalue.KeyValueMetaClass
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.Component.*
import com.haulmont.cuba.gui.components.Field
import com.haulmont.cuba.gui.components.GridLayout
import com.haulmont.cuba.gui.components.Label
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource
import com.haulmont.cuba.gui.model.CollectionChangeType
import com.haulmont.cuba.gui.model.DataComponents
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer
import com.haulmont.cuba.gui.model.impl.KeyValueContainerImpl
import com.vaadin.ui.themes.ValoTheme
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.inject.Inject

@Component(PivotGridInitializer.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class PivotGridInitializer(private var pivotGrid: GridLayout) {
    companion object {
        const val NAME = "pfa_PivotGridInitializer"
    }

    private var hackCounter: Int = 0

    @Inject
    private lateinit var dataComponents: DataComponents
    @Inject
    private lateinit var uiComponents: UiComponents

    lateinit var kvCollectionContainer: KeyValueCollectionContainer
        private set

    private lateinit var pivotStaticProperties: List<StaticPropertyData>

    private lateinit var storeFunction: (Any, String, Any?) -> Unit

    private var pivotGridRows = 1 //cause we have to skip header row


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

    fun initStaticPivotProperties(properties: List<StaticPropertyData>) {
        this.pivotStaticProperties = properties
        addKvContainerProperties(properties)

        properties.filter { it.visible }.forEach {
            addColumnCaption(it.caption)
        }
    }

    private fun addColumnCaption(caption: String) {
        if (this.hackCounter > 0) { //because grid layout decoratively initialized with 1 column
            pivotGrid.columns++
        }
        hackCounter++
        @Suppress("UnstableApiUsage")
        uiComponents.create(Label.TYPE_STRING).apply {
            this.value = caption
            this.addStyleName(ValoTheme.LABEL_H3)
            this.alignment = Alignment.MIDDLE_CENTER
        }.let {
            pivotGrid.add(it, pivotGrid.columns - 1, 0)
        }
    }


    private fun addColumnCaption(caption: String, startPosition: Int) {
        @Suppress("UnstableApiUsage")
        uiComponents.create(Label.TYPE_STRING).apply {
            this.value = caption
            this.addStyleName(ValoTheme.LABEL_H3)
            this.alignment = Alignment.MIDDLE_CENTER
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
            addStaticColumns(it)
            pivotGridRows++
        }
    }

    private fun addStaticColumns(keyValueEntity: KeyValueEntity) {
        var cols = 0
        pivotStaticProperties.filter { it.visible }.forEach { staticData ->
            val component: com.haulmont.cuba.gui.components.Component
            if (staticData.fieldType == null) {
                @Suppress("UnstableApiUsage")
                component = uiComponents.create(Label.TYPE_STRING).apply {
                    this.alignment = Alignment.MIDDLE_LEFT
                    this.value = keyValueEntity.getValue(staticData.property)
                }
            } else {
                //todo:
                @Suppress("UnstableApiUsage")
                component = uiComponents.create(staticData.fieldType!!).apply {
                    this.value = keyValueEntity.getValue(staticData.property)
                    staticData.fieldWidth?.let { this.setWidth(it) }
                }
            }
            pivotGrid.add(component, cols++, pivotGridRows)
        }
    }

    fun <T> initDynamicDcPivotProperties(properties: List<DynamicPropertyData<T>>){
        addKvContainerProperties(properties)

        initDynamicColumnsCaptions(properties)

        initDynamicPivotPropertiesFields(properties)
    }

    private fun addKvContainerProperties(properties : List<KvContainerProperty>) {
        properties.forEach {
            kvCollectionContainer.addProperty(it.property, it.clazz)
        }
    }

    private fun <T> initDynamicColumnsCaptions(properties: List<DynamicPropertyData<T>>) {
        var startCol = pivotGrid.columns
        pivotGrid.columns += properties.size
        properties.forEach {
            addColumnCaption(it.caption, startCol++)
        }
    }

    fun initDynamicPropertiesValues(function: (KeyValueCollectionContainer) -> Unit) {
        function.invoke(kvCollectionContainer)
    }

    fun <T> initDynamicPivotPropertiesFields(dynamicProperties: List<DynamicPropertyData<T>>) {
        val skipRows = 1 //avoid captions
        val skipColumns = pivotStaticProperties.filter { it.visible }.size //avoid static fields
        kvCollectionContainer.items.forEachIndexed { rowIndex, keyValueEntity ->
            dynamicProperties.forEachIndexed { colIndex, propertyData ->
                @Suppress("UnstableApiUsage")
                val field = uiComponents.create(propertyData.fieldType).apply {
                    val container = KeyValueContainerImpl(kvCollectionContainer.entityMetaClass as KeyValueMetaClass).apply {
                        setItem(keyValueEntity)
                        addItemPropertyChangeListener { event ->
                            val keyProperty = event.item.getValue<Any>(pivotStaticProperties.find { it.key }!!.property)
                            storeFunction.invoke(keyProperty!!, event.property, event.value)
                        }
                    }
                    this.valueSource = ContainerValueSource(container, propertyData.property)
                    this.setWidth(propertyData.fieldWidth)

                }
                pivotGrid.add(field, colIndex + skipColumns, rowIndex + skipRows)
            }
        }

    }

    fun setStoreFunction(function: (Any, String, Any?) -> Unit) {
        this.storeFunction = function
    }



    class StaticPropertyData(
        override val property: String,
        val caption: String,
        val key : Boolean = false,
        override val clazz: Class<*>,
        var fieldType: Class<out Field<*>>?,
        var fieldWidth: String?,
        val visible: Boolean = true
    ) : KvContainerProperty

    class DynamicPropertyData<T>(
        override val property: String,
        var caption: String,
        override val clazz: Class<T>,
        var fieldType: Class<out Field<*>>,
        var fieldWidth: String
    ) : KvContainerProperty

    interface KvContainerProperty {
        val property: String
        val clazz: Class<*>
    }
}