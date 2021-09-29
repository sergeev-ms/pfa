package com.borets.pfa.web.screens.account.marketdata.marketdata

import com.borets.pfa.entity.account.marketdata.RunsNumber
import com.haulmont.cuba.gui.components.Form
import com.haulmont.cuba.gui.components.HasValue
import com.haulmont.cuba.gui.components.LinkButton
import com.haulmont.cuba.gui.components.TextField
import com.haulmont.cuba.gui.screen.ScreenFragment
import com.haulmont.cuba.gui.screen.Subscribe
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
    private lateinit var secondRunDurationField: TextField<Int>
    @Inject
    private lateinit var thirdRunDurationField: TextField<Int>
    @Inject
    private lateinit var thirdPlusRunDurationField: TextField<Int>
    @Inject
    private lateinit var calculateCustomerDataBtn: LinkButton


    @Subscribe("runsNumberField")
    private fun onRunsNumberFieldValueChange(event: HasValue.ValueChangeEvent<RunsNumber>) {
        secondRunDurationField.isVisible = event.value == RunsNumber.TWO || event.value == RunsNumber.THREE || event.value ==  RunsNumber.THREE_PLUS
        thirdRunDurationField.isVisible = event.value == RunsNumber.THREE || event.value ==  RunsNumber.THREE_PLUS
        thirdPlusRunDurationField.isVisible = event.value == RunsNumber.THREE_PLUS
    }

    fun setEditable(editable : Boolean) {
        activityInputForm.isEditable = editable
        customerDataForm.isEditable = editable
        boretsDataForm.isEditable = editable
        additionalDataForm.isEditable = editable
        headerForm.isVisible = editable
        calculateCustomerDataBtn.isVisible = editable
    }
}