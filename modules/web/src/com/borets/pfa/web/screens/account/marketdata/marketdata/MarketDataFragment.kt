package com.borets.pfa.web.screens.account.marketdata.marketdata

import com.haulmont.cuba.gui.components.Form
import com.haulmont.cuba.gui.components.LinkButton
import com.haulmont.cuba.gui.screen.ScreenFragment
import com.haulmont.cuba.gui.screen.UiController
import com.haulmont.cuba.gui.screen.UiDescriptor
import javax.inject.Inject

@UiController("pfa_MarketDataFragment")
@UiDescriptor("market-data-fragment.xml")
class MarketDataFragment : ScreenFragment() {
    @Inject
    private lateinit var activityInputForm: Form
    @Inject
    private lateinit var customerDataForm: Form
    @Inject
    private lateinit var boretsDataForm: Form
    @Inject
    private lateinit var additionalDataForm: Form
    @Inject
    private lateinit var headerForm: Form
    @Inject
    private lateinit var calculateCustomerDataBtn: LinkButton


    fun setEditable(editable : Boolean) {
        activityInputForm.isEditable = editable
        customerDataForm.isEditable = editable
        boretsDataForm.isEditable = editable
        additionalDataForm.isEditable = editable
        headerForm.isVisible = editable
        calculateCustomerDataBtn.isVisible = editable
    }
}