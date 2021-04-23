package com.borets.pfa.web.dimcustomers

import com.borets.pfa.entity.customer.DimCustomers
import com.haulmont.cuba.gui.screen.*

@UiController("pfa_DimCustomers.browse")
@UiDescriptor("dim-customers-browse.xml")
@LookupComponent("dimCustomersesTable")
@LoadDataBeforeShow
class DimCustomersBrowse : StandardLookup<DimCustomers>()