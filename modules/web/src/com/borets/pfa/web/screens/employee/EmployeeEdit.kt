package com.borets.pfa.web.screens.employee

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.Employee
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.screen.Target

@UiController("pfa_Employee.edit")
@UiDescriptor("employee-edit.xml")
@EditedEntityContainer("employeeDc")
@LoadDataBeforeShow
class EmployeeEdit : StandardEditor<Employee>() {
    @Subscribe(id = "employeeDc", target = Target.DATA_CONTAINER)
    private fun onEmployeeDcItemPropertyChange(event: InstanceContainer.ItemPropertyChangeEvent<Employee>) {
        if (event.property == "firstName" || event.property == "lastName") {
            editedEntity.name = "${editedEntity.lastName.orEmpty()} ${editedEntity.firstName.orEmpty()}"
        }
    }

}