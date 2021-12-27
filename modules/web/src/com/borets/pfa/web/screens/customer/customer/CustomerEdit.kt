package com.borets.pfa.web.screens.customer.customer

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.customer.Customer

@UiController("pfa_Customer.edit")
@UiDescriptor("customer-edit.xml")
@EditedEntityContainer("customerDc")
@LoadDataBeforeShow
class CustomerEdit : StandardEditor<Customer>()