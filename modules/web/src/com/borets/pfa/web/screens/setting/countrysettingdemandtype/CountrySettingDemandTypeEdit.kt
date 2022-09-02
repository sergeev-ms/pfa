package com.borets.pfa.web.screens.setting.countrysettingdemandtype

import com.borets.pfa.entity.account.utilization.EquipmentUtilizationValueType
import com.borets.pfa.entity.analytic.AnalyticSet
import com.borets.pfa.entity.setting.CountrySettingDemandType
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.screen.*
import javax.inject.Inject

@UiController("pfa_CountrySettingDemandType.edit")
@UiDescriptor("country-setting-demand-type-edit.xml")
@EditedEntityContainer("countrySettingDemandTypeDc")
@LoadDataBeforeShow
class CountrySettingDemandTypeEdit : StandardEditor<CountrySettingDemandType>() {
    @Inject
    private lateinit var dataManager: DataManager

    @Inject
    private lateinit var scriptField: SourceCodeEditor

    @Subscribe
    private fun onAfterInit(event: AfterInitEvent) {
        val utilizationValueTypes = dataManager.load(EquipmentUtilizationValueType::class.java)
            .view(View.LOCAL)
            .list()

        val analyticSets = dataManager.load(AnalyticSet::class.java)
            .view { it.add("variableName") }
            .list()

        val contextHelp = buildString {
            append("<h3>Usage-variables list:</h3>")
            utilizationValueTypes.forEach {
                append("<br>")
                append(it.usageVariableName)
            }
            append("<h3>Utilization-variables list:</h3>")
            utilizationValueTypes.forEach {
                append("<br>")
                append(it.utilizationVariableName)
            }
            append("<h3>Analytic-variables list:</h3>")
            analyticSets.filter { !it.variableName.isNullOrBlank() }
                .forEach {
                    append("<br>")
                    append(it.variableName)
                }
            append("<br><br>")
            append("Also there is <b>qty</b> and <b>revenueMode</b> variables.")
            append("<h2>Caution:</h2> There is no guarantee that variables will be present in the script!<br>")
            append("Please check it before using the variable.<br>")
            append("For example: <i>def testVar = binding.variables['var'] == null ? 0.0 : var</i>")
        }
        scriptField.contextHelpText = contextHelp
        scriptField.isContextHelpTextHtmlEnabled = true
    }


}