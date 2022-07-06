package com.borets.pfa.web.screens.employee

import com.borets.addon.country.entity.Country
import com.borets.pfa.entity.Employee
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.model.InstanceContainer
import com.haulmont.cuba.gui.screen.*
import com.haulmont.cuba.gui.screen.Target
import javax.inject.Inject

@UiController("pfa_Employee.edit")
@UiDescriptor("employee-edit.xml")
@EditedEntityContainer("employeeDc")
@LoadDataBeforeShow
class EmployeeEdit : StandardEditor<Employee>() {
    @Inject
    private lateinit var dataManager: DataManager

    @Subscribe
    private fun onInitEntity(event: InitEntityEvent<Employee>) {
        val countries = dataManager.load(Country::class.java)
            .list()

        if (countries.size == 1) {
            event.entity.country = countries[0]
        }
    }


    @Subscribe(id = "employeeDc", target = Target.DATA_CONTAINER)
    private fun onEmployeeDcItemPropertyChange(event: InstanceContainer.ItemPropertyChangeEvent<Employee>) {
        if (event.property == "firstName" || event.property == "lastName") {
            editedEntity.name = "${editedEntity.lastName.orEmpty()} ${editedEntity.firstName.orEmpty()}"
        }
    }

}