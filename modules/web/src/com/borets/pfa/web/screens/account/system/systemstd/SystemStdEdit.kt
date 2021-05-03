package com.borets.pfa.web.screens.account.system.systemstd

import com.borets.pfa.entity.account.system.SystemDetail
import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.account.system.SystemStd
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.model.CollectionPropertyContainer
import com.haulmont.cuba.gui.model.DataContext
import javax.inject.Inject

@UiController("pfa_SystemStd.edit")
@UiDescriptor("system-std-edit.xml")
@EditedEntityContainer("systemStdDc")
@LoadDataBeforeShow
class SystemStdEdit : StandardEditor<SystemStd>() {
    @Inject
    private lateinit var dataContext: DataContext

    @Inject
    private lateinit var detailsDc: CollectionPropertyContainer<SystemDetail>

    @Subscribe("detailsTable.create")
    private fun onDetailsTableCreate(event: Action.ActionPerformedEvent) {
        dataContext.create(SystemDetail::class.java).apply {
            this.system = editedEntity
        }.let {
            detailsDc.mutableItems.add(it)
        }
    }
}