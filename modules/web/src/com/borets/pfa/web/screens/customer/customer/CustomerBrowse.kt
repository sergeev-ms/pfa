package com.borets.pfa.web.screens.customer.customer

import com.haulmont.cuba.gui.screen.*
import com.borets.pfa.entity.customer.Customer

@UiController("pfa_Customer.browse")
@UiDescriptor("customer-browse.xml")
@LookupComponent("customersTable")
@LoadDataBeforeShow
class CustomerBrowse : StandardLookup<Customer>()