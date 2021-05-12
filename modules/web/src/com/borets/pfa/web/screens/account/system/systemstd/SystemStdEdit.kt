package com.borets.pfa.web.screens.account.system.systemstd

import com.borets.addon.pn.entity.Part
import com.borets.pfa.entity.account.appdata.EquipmentType
import com.borets.pfa.entity.account.system.SystemDetail
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.QueryUtils
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.components.data.ValueSource
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource
import com.haulmont.cuba.gui.model.CollectionContainer
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import javax.inject.Inject

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
    private lateinit var detailsDc: CollectionPropertyContainer<SystemDetail>
    @Inject
    private lateinit var equipmentTypesDc: CollectionContainer<EquipmentType>

    @Inject
    private lateinit var detailsTable: Table<SystemDetail>

    @Inject
    private lateinit var dataManager: DataManager


    @Subscribe("detailsTable.create")
    private fun onDetailsTableCreate(event: Action.ActionPerformedEvent) {
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
                    .query("from pn_Part e where e.wtPartNumber like :searchString")
                    .parameter("searchString", "%${escapeForLike}%")
                    .list() as List<Any>
            }
            this.minSearchStringLength = 4
        }
        return suggestionPickerField
    }
}