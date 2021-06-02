package com.borets.pfa.web.screens.employee

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.Employee

@UiController("pfa_Employee.browse")
@UiDescriptor("employee-browse.xml")
@LookupComponent("employeesTable")
@LoadDataBeforeShow
class EmployeeBrowse : StandardLookup<Employee>()