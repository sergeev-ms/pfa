package com.borets.pfa.web.screens.account.system.systemstd

import com.borets.addon.mu.datatypes.Length
import com.borets.addon.mu.entity.MeasurementUnit
import com.borets.addon.mu.entity.MuType
import com.borets.addon.mu.service.MeasurementService
import com.borets.addon.pn.entity.Part
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.marketdata.MarketData
import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.chile.core.datatypes.DatatypeRegistry
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.QueryUtils
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource
import com.haulmont.cuba.gui.model.CollectionContainer
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject
import javax.inject.Named

@UiController("pfa_SystemStd.edit")
@UiDescriptor("system-std-edit.xml")
@EditedEntityContainer("systemStdDc")
@LoadDataBeforeShow
class SystemStdEdit : StandardEditor<SystemStd>() {
    @Inject
    private lateinit var dataContext: DataContext
    @Inject
    private lateinit var uiComponents: UiComponents
    @Inject
    private lateinit var dataManager: DataManager
    @Inject
    private lateinit var datatypeRegistry: DatatypeRegistry
    @Inject
    private lateinit var measurementService: MeasurementService

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<SystemDetail>
    @Inject
    private lateinit var equipmentTypesDc: CollectionContainer<EquipmentType>

    @Inject
    private lateinit var detailsTable: Table<SystemDetail>

    @field:Named("detailsTable.copy")
    private lateinit var detailsTableCopy: ItemTrackingAction

    @Subscribe
    private fun onBeforeShow(@Suppress("UNUSED_PARAMETER") event: BeforeShowEvent) {
        setCaptions()
        detailsTableCopy.addActionPerformedListener {
            val newDetail = dataContext.create(SystemDetail::class.java)
            newDetail.copyFrom(detailsTable.singleSelected!!)
            var index = detailsDc.getItemIndex(detailsTable.singleSelected!!)
            detailsDc.mutableItems.add(++index, newDetail)
        }
    }

    @Subscribe("detailsTable.create")
    private fun onDetailsTableCreate(@Suppress("UNUSED_PARAMETER") event: Action.ActionPerformedEvent) {
        dataContext.create(SystemDetail::class.java).apply {
            this.system = editedEntity
        }.let {
            detailsDc.mutableItems.add(it)
        }
    }

    fun equipmentTypeGenerator(systemDetail: SystemDetail): Component {
        val lookupField = uiComponents.create(LookupField::class.java)
        @Suppress("UNCHECKED_CAST")
        (lookupField as LookupField<EquipmentType>).apply {
            this.setWidthFull()
            this.setOptionsList(equipmentTypesDc.items)
            this.valueSource = ContainerValueSource(detailsTable.getInstanceContainer(systemDetail), "equipmentType")
        }
        return lookupField
    }

    fun partNumberGenerator(systemDetail: SystemDetail): Component {
        val suggestionPickerField = uiComponents.create(SuggestionPickerField::class.java)
        @Suppress("UNCHECKED_CAST")
        (suggestionPickerField as SuggestionPickerField<Part>).apply {
            this.setWidthFull()
            this.valueSource = ContainerValueSource(detailsTable.getInstanceContainer(systemDetail), "partNumber")
            this.setSearchExecutor { searchString, _ ->
                val escapeForLike = QueryUtils.escapeForLike(searchString)
                return@setSearchExecutor dataManager.load(Part::class.java)
                    .query("from pn_Part e where e.wtPartNumber like :searchString OR e.name like :searchString")
                    .parameter("searchString", "%${escapeForLike}%")
                    .list() as List<Any>
            }
            this.minSearchStringLength = 4
        }
        return suggestionPickerField
    }

    fun lengthGenerator(systemDetail: SystemDetail): Component? {
        return if ("DH Cable" == systemDetail.equipmentType?.category?.name) {
            uiComponents.create(TextField::class.java).apply {
                this.valueSource = ContainerValueSource(detailsTable.getInstanceContainer(systemDetail), "length")
                this.datatype = datatypeRegistry.get(Length.NAME)
                this.isRequired = true
                this.setWidthFull()
            }
        } else null
    }

    private fun setCaptions() {
        val measurementUnit: MeasurementUnit? = measurementService.getMeasurementUnit(MuType.LENGTH)
        val lengthColumn = detailsTable.getColumn("length")
        lengthColumn.caption = lengthColumn.caption?.format(measurementUnit?.name)
    }

    private inline fun SystemDetail.copyFrom(other : SystemDetail) {
        listOf("equipmentType", "length", "partNumber", "system")
            .forEach {
                this.setValue(it, other.getValue(it))
            }
    }
}