package com.borets.pfa.web.screens.account.utilization.equipmentutilization

import com.borets.pfa.entity.account.utilization.EquipmentUtilizationDetail
import com.borets.pfa.entity.activity.RecordType
import com.haulmont.cuba.gui.components.DataGrid
import com.haulmont.cuba.gui.components.DatePicker
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.screen.ScreenFragment
import com.haulmont.cuba.gui.screen.UiController
import com.haulmont.cuba.gui.screen.UiDescriptor
import java.time.LocalDate
import javax.inject.Inject

@UiController("pfa_EquipmentUtilizationFragment")
@UiDescriptor("equipment-utilization-fragment.xml")
class EquipmentUtilizationFragment : ScreenFragment() {
    @Inject
    private lateinit var utilizationDg: DataGrid<EquipmentUtilizationDetail>

    @Inject
    private lateinit var validFromField: DatePicker<LocalDate>

    @Inject
    private lateinit var recordTypeField: LookupField<RecordType>


    fun setEditable(editable : Boolean) {
        validFromField.isEditable = editable
        recordTypeField.isEditable = editable
        utilizationDg.isEditorEnabled = editable
    }
}